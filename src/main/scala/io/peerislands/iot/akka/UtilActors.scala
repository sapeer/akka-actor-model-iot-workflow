package io.peerislands.iot.akka

import akka.actor.{Actor, ActorLogging, ActorRef}
import io.peerislands.iot.akka.IoTWorkflowManager.Sensor

object UtilActors {

  def continueProcessing(sensor: Sensor, replySeq: Seq[ActorRef]): Unit =
    replySeq.head ! Process(sensor, replySeq.tail)

  class ConvertTempToFahrenheitActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case Process(sensor, replySeq: Seq[ActorRef]) =>
        continueProcessing(sensor, replySeq)
    }
  }

  class ValidateDataActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case Process(sensor, replySeq: Seq[ActorRef]) =>
        continueProcessing(sensor, replySeq)

    }
  }

  class FetchAddressActor extends Actor with ActorLogging {
    override def receive: Receive = {
      case Process(sensor, replySeq: Seq[ActorRef]) =>
        continueProcessing(sensor, replySeq)

    }
  }
}

final case class Process(sensor: Sensor, replySeq: Seq[ActorRef])
