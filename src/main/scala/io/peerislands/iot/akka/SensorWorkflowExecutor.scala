package io.peerislands.iot.akka

import akka.actor.{Actor, ActorRef}
import io.peerislands.iot.akka.IoTWorkflowManager.Sensor

import scala.collection.mutable

class SensorWorkflowExecutor extends Actor {


  override def receive: Receive = {
    case SensorPlanPayload(sensor, sensorPlan) =>
      val seqOfExecutionOfActors: mutable.Seq[ActorRef] = mutable.Seq()
      sensorPlan match {
        case Some(plan) => plan.foreach {
          step =>
            step.foreach {
              case Some(actorRef) => seqOfExecutionOfActors.appended(actorRef)
              case _ => context.system.log.debug("No Step")
            }
        }
        case _ => context.system.log.debug("No Plan")
      }
      seqOfExecutionOfActors.head ! Process(sensor, seqOfExecutionOfActors.tail.toSeq)
  }
}

final case class SensorPlanPayload(sensor: Sensor, sensorPlan: Option[Seq[Seq[Option[ActorRef]]]])
