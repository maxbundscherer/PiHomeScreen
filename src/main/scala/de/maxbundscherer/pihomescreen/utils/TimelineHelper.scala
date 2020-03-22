package de.maxbundscherer.pihomescreen.utils

import javafx.event.{ActionEvent, EventHandler}
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.util.Duration

trait TimelineHelper {

  private val logger: Logger              = new Logger(getClass.getSimpleName)

  /**
   * Start new timeline
   * @param interval e.g. 2 s
   * @param repeat Should repeat after end
   * @param title Timeline title
   * @param handler Timeline handler
   */
  def startNewTimeline(interval: Duration, repeat: Boolean, title: String, handler: EventHandler[ActionEvent]): Unit = {

    logger.debug(s"Start Timeline (title=$title) (repeat=$repeat) (interval=$interval)")

    val timeline = new Timeline {
      cycleCount = if(repeat) Timeline.Indefinite else 0
      autoReverse = false //Dont reverse
      keyFrames = Seq(KeyFrame(interval, onFinished = handler))
    }

    timeline.playFromStart()

  }

}