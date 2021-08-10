package io.peerislands.iot.akka

import akka.actor.{Actor, ActorRef}
import io.peerislands.iot.akka.IoTWorkflowManager.Sensor

class SensorWorkflowExecutor extends Actor {
  var seqOfExecutionOfActors = Seq()
  override def receive: Receive = {
    case SensorPlanPayload(sensor, sensorPlan) => sensorPlan match {
      case Some(plan) => plan.foreach {
        step =>
          step.foreach {
            case Some(actorRef) => actorRef ! sensor
            case _ => context.system.log.debug("No Step")
          }
      }
      case _ => context.system.log.debug("No Plan")
    }
  }
}

final case class SensorPlanPayload(sensor: Sensor, sensorPlan: Option[Seq[Seq[Option[ActorRef]]]])
