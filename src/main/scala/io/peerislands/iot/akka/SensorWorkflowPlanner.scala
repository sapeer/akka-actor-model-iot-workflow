package io.peerislands.iot.akka

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import io.peerislands.iot.akka.IoTWorkflowManager.Sensor
import io.peerislands.iot.akka.UtilActors._


/**
 * Create a Dynamic plan for Sensor based on the Sensor Data available inside the Sensor payload
 */
class SensorWorkflowPlanner extends Actor with ActorLogging {
  override def receive: Receive = {
    case InitiateSensorProcessing(sensor: Sensor, actorRef: ActorRef) =>
      actorRef ! SensorPlanPayload(sensor, buildIoTWorkflow(sensor))
  }

  private def buildIoTWorkflow(sensor: Sensor, currentStep: Int = 0): Option[Seq[Seq[Option[ActorRef]]]] = {
    val workSteps = Steps.values.filter(_.stepOrder > currentStep)
    if (workSteps.knownSize > 1) {
      Some(workSteps.toSeq.map(_.actor).map(qualifyStep(sensor, _)))
    } else {
      None
    }
  }

  /**
   *
   * @param sensor Sensor Payload
   * @param actors Actors
   * @return Qualify to include actor or not
   */
  private def qualifyStep(sensor: Sensor, actors: Seq[Actor]): Seq[Option[ActorRef]] = {
    actors.map {
      case x: ValidateDataActor => Some(setActorToContext(sensor, x))
      case x: ConvertTempToFahrenheitActor => if (!sensor.reading.temp.isNaN && sensor.reading.unitType.trim.nonEmpty) Some(setActorToContext(sensor, x)) else None
      case x: FetchAddressActor =>
        if (!sensor.lat.isNaN && !sensor.long.isNaN && -91.0 < sensor.long &&
          91.0 > sensor.long && -91.0 < sensor.lat && 91 > sensor.lat) Some(setActorToContext(sensor, x)) else None
    }
  }

  /**
   *
   * @param sensor Sensor Data
   * @param actor Actor Class
   * @return Actor Reference which is set into context
   */
  def setActorToContext(sensor: Sensor, actor: Actor): ActorRef = context.actorOf(Props(actor), sensor.sensorID)

  /**
   * Plan Step With the Order of Execution
   */
  object Steps extends Enumeration {


    import scala.language.implicitConversions

    implicit def valueToStep(x: Value): StepValue = x.asInstanceOf[StepValue]

    type Step = Value
    val ValidateData: StepValue = StepValue(1, Seq(new ValidateDataActor))
    val ConvertData: StepValue = StepValue(2, Seq(new ConvertTempToFahrenheitActor))
    val EnrichData: StepValue = StepValue(3, Seq(new FetchAddressActor))

    protected case class StepValue(stepOrder: Int, actor: Seq[Actor], persistState: Boolean = false) extends super.Val


  }
}

/**
 * Message format to pass around
 */
final case class ValidateDate(sensor: Sensor)

final case class InitiateSensorProcessing(sensor: Sensor, replyTo: ActorRef)

final case class ExecutionWorkflow(sensor: Sensor, flowThrough: Seq[ActorRef])


