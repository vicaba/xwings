import java.text.SimpleDateFormat
import java.util.{Calendar, Date}


val startTime = "Jan 01 2009"
val dateFormat = new SimpleDateFormat("MMM dd yyyy")

val date = dateFormat.parse(startTime)

val calendar = Calendar.getInstance()
calendar.setTime(date)
calendar.add(Calendar.MONTH, 1)
dateFormat.format(calendar.getTime)

