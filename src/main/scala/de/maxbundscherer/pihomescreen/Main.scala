package de.maxbundscherer.pihomescreen

import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javafx.{fxml => jfxf}
import javafx.{scene => jfxs}

object Main extends JFXApp {

  private val resource = getClass.getResource("gui/Main.fxml")

  if(resource == null) throw new RuntimeException("Cannot load fxml file")

  private val root: jfxs.Parent = FXMLLoader.load(resource)

  private val state = new PrimaryStage() {
    title = "Main"
    scene = new Scene(root)
  }

}