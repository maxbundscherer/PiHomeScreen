package de.maxbundscherer.pihomescreen.presenter

import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, ChoiceBox, TextArea, TextField}
import scalafx.scene.layout.GridPane
import scalafxml.core.macros.sfxml

@sfxml
class MainPresenter(private val btnTopLeft: Button) {

  def btnTopLeft_onAction(event: ActionEvent): Unit = {
    this.btnTopLeft.setText("Hello!")
  }

}
