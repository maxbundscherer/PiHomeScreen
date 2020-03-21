package de.maxbundscherer.pihomescreen.utils

import scalafx.Includes._
import scalafx.animation.{KeyFrame, Timeline}

import scala.language.postfixOps

trait InitPresenter {

  private val initTimeline = new Timeline {
    cycleCount = 0 //Only run one time
    autoReverse = false
    keyFrames = Seq(KeyFrame(1 s, onFinished = () => {
      initPresenter()
    }))
  }

  this.initTimeline.playFromStart()

  /**
   * Init Presenter
   */
  def initPresenter(): Unit

}