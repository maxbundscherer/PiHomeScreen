package de.maxbundscherer.pihomescreen.presenter

import de.maxbundscherer.pihomescreen.img.ImageHelper
import de.maxbundscherer.pihomescreen.utils.{InitPresenter, Logger, ProgressBarSlider}

import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

@sfxml
class MainPresenter(private val panBackground: Pane) extends InitPresenter with ProgressBarSlider {

  private val logger = new Logger(getClass.getSimpleName)

  /**
   * Init Presenter
   */
  override def initPresenter(): Unit = {
    logger.debug("Init Presenter")
    panBackground.setBackground(ImageHelper.getBackground())
  }

  def prb_onMouseMoved(event: MouseEvent): Unit = {
    updateProgressBar(event)
  }

}