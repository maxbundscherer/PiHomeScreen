package de.maxbundscherer.pihomescreen.utils

import scalafx.scene.input.MouseEvent

trait ProgressBarSlider {

  /**
   * Use ProgressBar as Slider
   * @param event MouseEvent (e.g. onMouseMoved)
   * @return progress (0 to 1)
   */
  def updateProgressBar(event: MouseEvent): Double = {

    val progressBar = event.source.asInstanceOf[javafx.scene.control.ProgressBar]
    val progress    = event.x / progressBar.getWidth

    progressBar.setProgress(progress)

    progress
  }

}
