package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.CalendarService

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

class SimpleCalendarService extends CalendarService {

  private def getCalendar = Calendar.getInstance()
  private def getTime: Date = this.getCalendar.getTime

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

    val dayOfTheWeek: Int = this.getCalendar.get(Calendar.DAY_OF_WEEK) -1 //Sunday ist not the first day of the week

    val dayString: String = dayOfTheWeek match {
      case 1 => "Mo"
      case 2 => "Di"
      case 3 => "Mi"
      case 4 => "Do"
      case 5 => "Fr"
      case 6 => "Sa"
      case 7 => "So"
      case _ => "??"
    }

    dayString + ", " + this.dateFormat.format(this.getTime)
  }

}
