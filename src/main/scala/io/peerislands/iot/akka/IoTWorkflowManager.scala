package io.peerislands.iot.akka

import akka.actor.typed.{ActorRef, Behavior, SupervisorStrategy}
import akka.pattern.StatusReply
import akka.persistence.typed.PersistenceId
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior, RetentionCriteria}

import scala.concurrent.duration.DurationInt

/**
 *
 */
object IoTWorkflowManager {

  /**
   *
   * @param sensorId Sensor Id
   * @return Akka Actor Behavior
   */
  def apply(sensorId: String): Behavior[Command] = {
    EventSourcedBehavior[Command, Event, State](
      PersistenceId("Sensor", sensorId),
      State.empty,
      (state, command) => handleCommands(state, command),
      (state, event) => handleEvent(state, event)
    )
      .withRetention(RetentionCriteria.snapshotEvery(numberOfEvents = 100, keepNSnapshots = 3))
      .onPersistFailure(SupervisorStrategy.restartWithBackoff(200.millis, 5.seconds, 0.1))
  }

  /**
   *
   * @param state State of a Sensor
   * @param command command to be issued to the State for the Sensor
   * @return Persistent Reply-able Effect
   */
  private def handleCommands(state: State, command: Command): Effect[Event, State] = {
    command match {
      case GetData(replyTo) =>
        replyTo ! state.getSensorInfo
        Effect.none
      case WriteSensorData(sensor, replyTo) =>
        state.writeData(sensor)
        Effect.persist(WriteData(sensor.sensorID, sensor))
          .thenRun(updatedSensor => replyTo ! StatusReply.Success(updatedSensor.getSensorInfo))
    }
  }

  /**
   *
   * @param state State of the Sensor
   * @param event Event Request from the command
   * @return new State of Sensor
   */
  private def handleEvent(state: State, event: Event): State = {
    event match {
      case WriteData(sensorID, sensor) => state.writeData(sensor)
      case DropData(sensorID, sensor) => state.writeData(Sensor(sensorID))
    }
  }

  /**
   *
   */
  sealed trait Command extends CborSerializable

  /**
   * This interface defines all the events that the Iot Device supports.
   */
  sealed trait Event extends CborSerializable {
    def sensorID: String
  }

  /**
   * The current state held by the persistent entity.
   */
  final case class State(sensor: Sensor) extends CborSerializable {

    def writeData(sensor: Sensor): State = {
      copy(sensor = sensor)
    }

    def getSensorInfo: Sensor = sensor
  }

  final case class GetData(replyTo: ActorRef[Sensor]) extends Command

  final case class WriteSensorData(sensor: Sensor, replyTo: ActorRef[StatusReply[Sensor]]) extends Command

  final case class SensorReading(temp: Double = 0, humidity: Double = 0, dewPoint: Double = 0, airPressure: Double = 0, units: String = "", unitType: String = "")

  final case class Sensor(sensorID: String, regionID: String = "", lat: Double = 0, long: Double = 0, reading: SensorReading = SensorReading())

  final case class WriteData(sensorID: String, sensorPayload: Sensor) extends Event

  final case class DropData(sensorID: String, sensorPayload: Sensor) extends Event

  /**
   * New State of New Sensor
   */
  object State {
    val empty: State = State(Sensor(""))
  }
}
