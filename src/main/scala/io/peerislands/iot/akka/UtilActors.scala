package io.peerislands.iot.akka

import akka.actor.{Actor, ActorLogging}
import akka.persistence.PersistentActor
import io.peerislands.iot.akka.IoTWorkflowManager.Sensor

object UtilActors {

  class ConvertTempToFahrenheitActor extends PersistentActor with ActorLogging{
    override def receive: Receive = {
      case Sensor =>
    }

    override def receiveRecover: Receive = ???

    override def receiveCommand: Receive = ???

    override def persistenceId: String = ???
  }

  class ValidateDataActor extends PersistentActor with ActorLogging{
    override def receive: Receive = {
      case Sensor =>

    }

    override def receiveRecover: Receive = ???

    override def receiveCommand: Receive = ???

    override def persistenceId: String = ???
  }

  class FetchAddressActor extends PersistentActor with ActorLogging{
    override def receive: Receive = {
      case Sensor =>

    }

    override def receiveRecover: Receive = ???

    override def receiveCommand: Receive = ???

    override def persistenceId: String = ???
  }
}
