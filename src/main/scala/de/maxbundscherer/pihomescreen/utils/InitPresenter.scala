package de.maxbundscherer.pihomescreen.utils

import scalafx.Includes._
import scala.language.postfixOps

trait InitPresenter extends TimelineHelper {

  /**
   * Start init timeline
   */
  this.startNewTimeline(interval = 5 s,
    repeat = false,
    title="Init Timeline",
    handler = () => {this.initPresenter()}
  )

  /**
   * Init Presenter
   */
  def initPresenter(): Unit

}