package de.maxbundscherer.pihomescreen.presenter

import de.maxbundscherer.pihomescreen.img.ImageHelper
import de.maxbundscherer.pihomescreen.utils.ProgressBarSlider

import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

@sfxml
class MainPresenter(private val panBackground: Pane) extends ProgressBarSlider {

  def prb_onMouseMoved(event: MouseEvent): Unit = {
    updateProgressBar(event)
    panBackground.setBackground(ImageHelper.getBackground())
  }

}
