package de.maxbundscherer.pihomescreen.utils

import scalafx.scene.input.MouseEvent

trait ProgressBarSlider {

  /**
   * Use ProgressBar as Slider
   * @param event MouseEvent (e.g. onMouseMoved)
   * @param updateSender Set progress to caller (progressBar)
   * @return progress (0 to 1)
   */
  def processProgressBar(event: MouseEvent, updateSender: Boolean): Double = {

    val progressBar = event.getSource.asInstanceOf[javafx.scene.control.ProgressBar]
    val progress    = event.x / progressBar.getWidth

    val resultProgress = if(progress <= 0.1) 0 else if(progress >= 0.9) 1 else progress

    if(updateSender) progressBar.setProgress(resultProgress)

    resultProgress
  }

}
