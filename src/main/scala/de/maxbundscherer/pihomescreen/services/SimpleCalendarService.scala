package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.CalendarService

import java.text.SimpleDateFormat
import java.util.{ Calendar, Date }

class SimpleCalendarService extends CalendarService {

  private def getCalendar   = Calendar.getInstance()
  private def getTime: Date = this.getCalendar.getTime

  private val hourFormat          = new SimpleDateFormat("HH")
  private val hourAndMinuteFormat = new SimpleDateFormat("HH:mm")
  private val dateFormat          = new SimpleDateFormat("dd.MM.")

  /**
    * Get Hour and Minute
    * @return e.g. 12:31
    */
  override def getHourAndMinuteToString: String = this.hourAndMinuteFormat.format(this.getTime)

  /**
    * Get Date
    * @return e.g. Mo, 01.02.
    */
  override def getDateToString: String = {

    val dayOfTheWeek: Int = this.getCalendar.get(Calendar.DAY_OF_WEEK)

    val dayString: String = dayOfTheWeek match {
      case 1 => "So"
      case 2 => "Mo"
      case 3 => "Di"
      case 4 => "Mi"
      case 5 => "Do"
      case 6 => "Fr"
      case 7 => "Sa"
      case _ => "??"
    }

    dayString + ", " + this.dateFormat.format(this.getTime)
  }

  override def getCurrentHour24: Int =
    this.hourFormat.format(this.getTime).toInt

  override def getCurrentDate: java.util.Date = this.getTime

}
