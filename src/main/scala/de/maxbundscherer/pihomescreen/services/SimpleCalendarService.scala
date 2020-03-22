package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.CalendarService

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

class SimpleCalendarService extends CalendarService {

  private def getTime: Date = Calendar.getInstance().getTime

  private val hourAndMinuteFormat = new SimpleDateFormat("HH:mm")
  private val dateFormat          = new SimpleDateFormat("dd.MM.")

  /**
   * Get Hour and Minute
   * @return e.g. 12:31
   */
  override def getHourAndMinuteToString: String = this.hourAndMinuteFormat.format(this.getTime)

  /**
   * Get Date
   * @return e.g. 01.02.
   */
  override def getDateToString: String = this.dateFormat.format(this.getTime)

}
