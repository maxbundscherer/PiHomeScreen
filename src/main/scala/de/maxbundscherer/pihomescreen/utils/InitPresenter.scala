package de.maxbundscherer.pihomescreen.utils

import scalafx.Includes._
import scala.language.postfixOps

trait InitPresenter extends TimelineHelper {

  /**
   * Start init timeline
   */
  this.startNewTimeline(interval = 1 s,
    repeat = false,
    title="Init Timeline",
    handler = () => {this.initPresenter()}
  )

  /**
   * Init Presenter
   */
  def initPresenter(): Unit

}