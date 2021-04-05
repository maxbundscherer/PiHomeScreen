package de.maxbundscherer.pihomescreen.services.abstracts

abstract class CalendarService {

  /**
    * Get Hour and Minute
    * @return e.g. 12:31
    */
  def getHourAndMinuteToString: String

  /**
    * Get Date
    * @return e.g. Mo, 01.02.
    */
  def getDateToString: String

  def getCurrentHour24: Int

}
