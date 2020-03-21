package de.maxbundscherer.pihomescreen.presenter

import de.maxbundscherer.pihomescreen.utils.ProgressBarSlider

import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafxml.core.macros.sfxml

@sfxml
class MainPresenter(private val btnTopLeft: Button) extends ProgressBarSlider {

  def btnTopLeft_onAction(event: ActionEvent): Unit = {
    this.btnTopLeft.setText("Hello!")
  }

  def prbMiddle_onMouseMoved(event: MouseEvent): Unit = {
    println(s"Got ${updateProgressBar(event)} %")
  }

}
