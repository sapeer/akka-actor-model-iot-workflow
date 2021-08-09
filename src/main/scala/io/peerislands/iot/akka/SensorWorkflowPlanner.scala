package io.peerislands.iot.akka

import akka.actor.{Actor, ActorRef}
import akka.persistence.PersistentActor
import io.peerislands.iot.akka.IoTWorkflowManager.Sensor
import io.peerislands.iot.akka.IotMessages.InitiateSensorProcessing
import io.peerislands.iot.akka.UtilActors._


/**
 * Create a Dynamic plan for Sensor based on the Sensor Data available inside the Sensor payload
 */
class SensorWorkflowPlanner extends Actor {
  override def receive: Receive = {
    case InitiateSensorProcessing(sensor: Sensor, actorRef: ActorRef) =>
      actorRef ! buildIoTWorkflow(sensor)
  }

  private def buildIoTWorkflow(sensor: Sensor, currentStep: Int = 0): Option[Seq[Actor]] = {
    val workSteps = Steps.values.filter(_.stepOrder > currentStep)
    if (workSteps.knownSize > 1) {
      Some(workSteps.toSeq.map(_.actorClass).filter(qualifyStep(sensor, _)))
    } else {
      None
    }
  }

  /**
   *
   * @param sensor          Sensor Payload
   * @param persistentActor Actor
   * @return Qualify to include actor or not
   */
  private def qualifyStep(sensor: Sensor, persistentActor: PersistentActor): Boolean = {
    persistentActor match {
      case _: ValidateDataActor => true
      case _: ConvertTempToFahrenheitActor => !sensor.reading.temp.isNaN && sensor.reading.unitType.trim.nonEmpty
      case _: FetchAddressActor =>
        !sensor.lat.isNaN && !sensor.long.isNaN && -91.0 < sensor.long &&
          91.0 > sensor.long && -91.0 < sensor.lat && 91 > sensor.lat
    }
  }
}


/**
 * Message format to pass around
 */
object IotMessages {
  final case class ValidateDate(sensor: Sensor)

  final case class InitiateSensorProcessing(sensor: Sensor, replyTo: ActorRef)

  final case class ExecutionWorkflow(sensor: Sensor, flowThrough: Seq[ActorRef])
}

/**
 * Step Plan With Order for Execution
 */
object Steps extends Enumeration {


  import scala.language.implicitConversions

  implicit def valueToStep(x: Value): StepValue = x.asInstanceOf[StepValue]

  type Step = Value
  val ValidateData: StepValue = StepValue(1, new ValidateDataActor)
  val ConvertData: StepValue = StepValue(2, new ConvertTempToFahrenheitActor)
  val EnrichData: StepValue = StepValue(3, new FetchAddressActor)

  protected case class StepValue(stepOrder: Int, actorClass: PersistentActor, persistState: Boolean = false) extends super.Val
}