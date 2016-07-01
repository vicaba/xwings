import java.text.SimpleDateFormat
import java.util.Calendar

import scala.concurrent.duration._

import org.joda.time.DateTime

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

timestamp("19503")

timestamp("20048")