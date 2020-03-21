package de.maxbundscherer.pihomescreen

import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javafx.{fxml => jfxf}
import javafx.{scene => jfxs}
import scalafxml.core.{FXMLView, NoDependencyResolver}

object Main extends JFXApp {

  private val resource = getClass.getResource("fxml/Main.fxml")

  if(resource == null) throw new RuntimeException("Cannot load fxml file")

  private val root = FXMLView(resource, NoDependencyResolver)

  private val _ = new PrimaryStage() {
    title = "Main"
    scene = new Scene(root)
    fullScreen = true
  }

}