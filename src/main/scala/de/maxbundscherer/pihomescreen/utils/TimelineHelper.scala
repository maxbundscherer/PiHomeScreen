package de.maxbundscherer.pihomescreen.utils

import javafx.event.{ActionEvent, EventHandler}
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.util.Duration

trait TimelineHelper {

  private val logger: Logger              = new Logger(getClass.getSimpleName)

  def startNewTimeline(firstActionAfter: Duration, repeat: Boolean, title: String, src: EventHandler[ActionEvent]): Unit = {

    logger.debug(s"Start Timeline (title=$title) (repeat=$repeat) (firstActionAfter=$firstActionAfter)")

    val timeline = new Timeline {
      cycleCount = if(repeat) Timeline.Indefinite else 0
      autoReverse = false //Dont reverse
      keyFrames = Seq(KeyFrame(firstActionAfter, onFinished = src))
    }

    timeline.playFromStart()

  }

}