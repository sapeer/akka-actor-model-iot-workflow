package io.peerislands.iot.akka

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.pattern.StatusReply
import io.peerislands.iot.akka.IoTWorkflowManager.{Sensor, SensorReading, WriteSensorData}
import org.scalatest.wordspec.AnyWordSpecLike

import java.util.UUID

class IoTWorkflowManagerSpec extends ScalaTestWithActorTestKit(
  s"""
      akka.persistence.journal.plugin = "akka.persistence.journal.inmem"
      akka.persistence.snapshot-store.plugin = "akka.persistence.snapshot-store.local"
      akka.persistence.snapshot-store.local.dir = "target/snapshot-${UUID.randomUUID().toString}"
    """) with AnyWordSpecLike {

  private var counter = 0

  def newSensorId(): String = {
    counter += 1
    s"sensor-$counter"
  }

  "The IoTWorkflowManager" should {

    "add Sensor Data" in {
      val sensorIdFx = newSensorId()
      val sensor = testKit.spawn(IoTWorkflowManager(sensorIdFx))
      val probe = testKit.createTestProbe[StatusReply[IoTWorkflowManager.Sensor]]
      sensor ! WriteSensorData(Sensor(sensorIdFx), probe.ref)
      probe.expectMessage(StatusReply.Success(IoTWorkflowManager.Sensor(sensorIdFx)))
    }
    //
    "keep its state" in {
      val sensorIdFx = newSensorId()
      val sensor = testKit.spawn(IoTWorkflowManager(sensorIdFx))
      val probe = testKit.createTestProbe[StatusReply[IoTWorkflowManager.Sensor]]
      sensor ! WriteSensorData(Sensor(sensorIdFx, "", 19.3133, 81.2546, SensorReading(12, 8, 12, 1, "C,%,?,Pa", "SI")), probe.ref)
      probe.expectMessage(StatusReply.Success(IoTWorkflowManager.Sensor(sensorIdFx, "", 19.3133, 81.2546, SensorReading(12, 8, 12, 1, "C,%,?,Pa", "SI"))))

      testKit.stop(sensor)

      // start again with same cartId
      val restartedCart = testKit.spawn(IoTWorkflowManager(sensorIdFx))
      val stateProbe = testKit.createTestProbe[IoTWorkflowManager.Sensor]
      restartedCart ! IoTWorkflowManager.GetData(stateProbe.ref)
      stateProbe.expectMessage(IoTWorkflowManager.Sensor(sensorIdFx, "", 19.3133, 81.2546, SensorReading(12, 8, 12, 1, "C,%,?,Pa", "SI")))
    }
  }
}

