package de.maxbundscherer.pihomescreen.presenter

import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, ChoiceBox, ProgressBar, Slider, TextArea, TextField}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.GridPane
import scalafxml.core.macros.sfxml

@sfxml
class MainPresenter(private val btnTopLeft: Button, private val prbMiddle: ProgressBar) {

  def btnTopLeft_onAction(event: ActionEvent): Unit = {
    this.btnTopLeft.setText("Hello!")
  }

  def prbMiddle_onMouseMoved(event: MouseEvent): Unit = {
    prbMiddle.progress = event.x / prbMiddle.width.value
  }

}
