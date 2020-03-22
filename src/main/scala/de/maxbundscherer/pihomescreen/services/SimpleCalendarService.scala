package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.CalendarService

import java.util.Calendar

class SimpleCalendarService extends CalendarService {

  private def getCalendar: Calendar = Calendar.getInstance()

  /**
   * Get Hour and Minute
   * @return e.g. 12:31
   */
  override def getHourAndMinuteToString: String = {

    val currentHour = getCalendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = getCalendar.get(Calendar.MINUTE)

    currentHour + ":" + currentMinute
  }

  /**
   * Get Date
   * @return e.g. 01.02.
   */
  override def getDateToString: String = {

    val currentDay = getCalendar.get(Calendar.DAY_OF_MONTH)
    val currentMoth = getCalendar.get(Calendar.MONTH)

    currentDay + "." + currentMoth + "."
  }

}
