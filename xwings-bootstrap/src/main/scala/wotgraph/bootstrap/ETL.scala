package wotgraph.bootstrap

import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Framing}
import akka.util.ByteString
import org.joda.time.DateTime

import scala.concurrent.duration._

case class MeterValue(meterId: String, date: DateTime, value: String)

object ETL {

  def apply() = {

    val regex = "(.+) (.+) (.+)".r

    implicit val sys = ActorSystem()
    implicit val mat = ActorMaterializer()

    val file = new File("/Users/vicaba/Desktop/VICTOR/File1.txt")

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

    FileIO.fromFile(file)
      .via(Framing.delimiter(ByteString(System.lineSeparator), maximumFrameLength = 256, allowTruncation = true))
      .map(_.utf8String)
      .map(parseMeterValueLine)
      .runForeach(println)

  }

  def main(args: Array[String]) {
    ETL.apply()
  }


}
