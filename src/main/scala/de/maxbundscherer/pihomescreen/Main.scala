package de.maxbundscherer.pihomescreen

import org.apache.logging.log4j.scala.Logging
import javafx.fxml.FXMLLoader
import scalafx.application.JFXApp
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import javafx.{fxml => jfxf}
import javafx.{scene => jfxs}
import scalafx.scene.paint.Color
import scalafx.stage.StageStyle
import scalafxml.core.{FXMLView, NoDependencyResolver}

object Main extends JFXApp with Logging {

  logger.debug("Init JFX")

  private val resource = getClass.getResource("fxml/Main.fxml")

  if(resource == null) throw new RuntimeException("Cannot load fxml file")

  private val root = FXMLView(resource, NoDependencyResolver)

  //use scene2

  val scene2 = new Scene(root)
  //scene2.setFill(Color.Transparent)

  val stage2 = new PrimaryStage()
  stage2.setFullScreen(true)
  stage2.setScene(scene2)
  //stage2.initStyle(StageStyle.Transparent)

  stage2.show()

}