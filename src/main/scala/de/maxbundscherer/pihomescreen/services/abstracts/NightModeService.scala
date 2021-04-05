package de.maxbundscherer.pihomescreen.services.abstracts

import scala.concurrent.Future
import java.util.Date

abstract class NightModeService() {

  protected def isNight: Boolean

  protected def calcDurationMin(start: Date, stop: Date): Double

  def reportInteraction(): Future[Unit]

  def shouldHideScreen: Boolean

}
