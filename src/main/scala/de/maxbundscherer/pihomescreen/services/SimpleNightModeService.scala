package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.{ CalendarService, NightModeService }
import de.maxbundscherer.pihomescreen.utils.Configuration

import org.apache.logging.log4j.scala.Logging

class SimpleNightModeService(calendarService: CalendarService)
    extends NightModeService()
    with Configuration
    with Logging {

  import scala.concurrent.ExecutionContext.Implicits.global

  import scala.concurrent.Future
  import java.util.Date

  private var lastInteractionTime: Date = calendarService.getCurrentDate

  override protected def isNight: Boolean = {

    val cHour: Int = calendarService.getCurrentHour24

    if (cHour >= Config.NightMode.startHour) true
    else if (cHour <= Config.NightMode.stopHour) true
    else false

  }

  override protected def calcDurationMin(start: Date, stop: Date): Double =
    ((stop.toInstant.toEpochMilli - start.toInstant.toEpochMilli) / 1000.0) / 60.0

  override def reportInteraction(): Future[Unit] =
    Future { this.lastInteractionTime = calendarService.getCurrentDate }

  override def shouldHideScreen: Boolean =
    if (!this.isNight)
      false
    else
      calcDurationMin(
        this.lastInteractionTime,
        stop = calendarService.getCurrentDate
      ) > Config.NightMode.inactiveMin

}
