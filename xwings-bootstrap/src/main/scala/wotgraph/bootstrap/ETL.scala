package wotgraph.bootstrap

import java.io.File
import java.text.SimpleDateFormat
import java.util
import java.util.{Calendar, UUID}

import akka.NotUsed
import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.actor.ActorPublisher
import akka.stream.{ActorMaterializer, ClosedShape, ThrottleMode}
import akka.stream.scaladsl.{Concat, FileIO, Flow, Framing, GraphDSL, RunnableGraph, Sink}
import akka.util.ByteString
import org.joda.time.DateTime
import org.scalactic.{Bad, Good}
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.libs.ws.ahc.AhcWSClient
import wotgraph.app.thing.application.usecase.dto.CreateThing
import wotgraph.app.thing.domain.entity.{Action, Metadata}
import wotgraph.app.thing.infrastructure.service.action.AvailableContexts

import scala.collection.immutable.Queue
import scala.concurrent.duration._

case class MeterValue(meterId: String, date: DateTime, value: String)

object ETL {

  implicit val sys = ActorSystem()
  implicit val mat = ActorMaterializer()
  val splitter = sys.actorOf(Props(new DeviceTransformer))

  def apply() = {

    val regex = "(.+) (.+) (.+)".r

    def baseCalendar: Calendar = {
      val startTime = "Jan 01 2009"
      val dateFormat = new SimpleDateFormat("MMM dd yyyy")

      val date = dateFormat.parse(startTime)
      val calendar = Calendar.getInstance()
      calendar.setTime(date)
      calendar
    }

    def timestamp(time: String): DateTime = {
      val days = Integer.parseInt(time.substring(0, 3)).days.toDays
      val minutes = (Integer.parseInt(time.substring(3, 5)) * 30).minutes.toMinutes

      val calendar = baseCalendar
      calendar.add(Calendar.MINUTE, minutes.toInt)
      calendar.add(Calendar.DAY_OF_YEAR, days.toInt)
      val date = calendar.getTime
      new DateTime(date.getTime)
    }

    def parseRawMeterValues(id: String, time: String, value: String): MeterValue =
      MeterValue(id, timestamp(time), value)

    def parseMeterValueLine(s: String) = s match {
      case regex(id, time, value) => parseRawMeterValues(id, time, value)
    }


    val file = new File("/Users/vicaba/Desktop/VICTOR/File1.txt")

    val file1 = new File("/Users/vicaba/Desktop/VICTOR/File1ex.txt")
    val file2 = new File("/Users/vicaba/Desktop/VICTOR/File2ex.txt")

    val flow = Flow[ByteString]
      .via(Framing.delimiter(ByteString(System.lineSeparator), maximumFrameLength = 256, allowTruncation = true))
      .map(_.utf8String)
      .map(parseMeterValueLine)

    val sink = Sink.actorRefWithAck[MeterValue](
      splitter,
      onInitMessage = "start",
      ackMessage = "ack",
      onCompleteMessage = "completed",
      onFailureMessage = (t) => println(t))

    val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val in = FileIO.fromFile(file)
      val in1 = FileIO.fromFile(file1)
      val in2 = FileIO.fromFile(file2)

      //val concat = builder.add(Concat[ByteString](2))

      //in1 ~> concat.in(0)
      //in2 ~> concat.in(1)

      //concat.out ~> flow ~> sink

      in ~> flow ~> sink

      ClosedShape
    })

    // Sleep for a while till all services are up
    Thread.sleep(100)

    g.run()

/*    val f = FileIO.fromFile(file)
      .via(Framing.delimiter(ByteString(System.lineSeparator), maximumFrameLength = 256, allowTruncation = true))
      .map(_.utf8String)
      .map(parseMeterValueLine)
      .runWith(
        Sink.actorRefWithAck[MeterValue](
          splitter,
          onInitMessage = "start",
          ackMessage = "ack",
          onCompleteMessage = "completed",
          onFailureMessage = (t) => println(t))
      )*/

  }

  def main(args: Array[String]) {
    ETL.apply()
  }
}

class DeviceTransformer extends Actor {

  import scala.concurrent.ExecutionContext.Implicits.global

  val meterValueTransformer = context.actorOf(Props(new MeterValueTransformer))

  def receiveWithParams(device2Thing: Map[String, UUID]): Actor.Receive = {
    case "start" => sender ! "ack"
    case mv@MeterValue(id, time, value) =>
      val _sender = sender
      val _deviceId = id
      if (!device2Thing.contains(id)) {
        ThingHelper.createThingUseCase.execute(createThing(id))(UUID.randomUUID()).map {
          case Good(t) =>
            _sender ! "ack"
            meterValueTransformer !(_deviceId, t._id)
            context.become(receiveWithParams(device2Thing + (id -> t._id)));
          case Bad(e) =>
        } recover {
          case _ => self.!(mv)(_sender)
        }
      } else {
        meterValueTransformer ! mv
        _sender ! "ack"
      }
  }

  override def receive: Receive = receiveWithParams(Map[String, UUID]())

  def createThing(id: String): CreateThing = {
    val actions = Set(
      Action(
        "putConsume", AvailableContexts.WriteToDatabaseContext, ""
      )
    )
    val metadata = Json.obj("deviceId" -> id)
    new CreateThing(Metadata(metadata.as[JsObject]), actions)
  }
}

class MeterValueTransformer extends Actor {

  val store = context.actorOf(Props(new Store))

  def receiveWithParams(
                         device2Thing: Map[String, UUID]
                       ): Actor.Receive = {
    case mv: MeterValue =>
      device2Thing.get(mv.meterId) match {
        case Some(id) =>
          ThingHelper.executeThingActionUseCase.execute(
            id.toString, "putConsume", Json.obj("value" -> mv.value, "date" -> mv.date)
          )(
            UUID.randomUUID()
          )
        case None =>
          println("fail")
          store ! mv
      }
    case id: (String, UUID) =>
      store ! "dequeueAll"
      context.become(receiveWithParams(device2Thing + id))
  }


  override def receive: Receive = receiveWithParams(Map[String, UUID]())
}

class Store extends Actor {

  def receiveWithParams(store: Queue[MeterValue]): Actor.Receive = {
    case mv: MeterValue => context.become(receiveWithParams(store :+ mv))
    case "dequeueAll" => store.dequeueOption match {
      case Some((mv, q)) =>
        context.parent ! mv
        self ! "dequeueAll"
        context.become(receiveWithParams(q))
      case None =>
    }
  }

  override def receive: Receive = receiveWithParams(Queue[MeterValue]())
}