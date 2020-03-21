package de.maxbundscherer.pihomescreen.presenter

import de.maxbundscherer.pihomescreen.img.ImageHelper
import de.maxbundscherer.pihomescreen.services.{HueService, MockHueService}
import de.maxbundscherer.pihomescreen.utils.{InitPresenter, Logger, ProgressBarSlider}

import scalafx.scene.control.ToggleButton
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

@sfxml
class MainPresenter(

                      //System
                      private val panBackground: Pane,

                      //Bulbs
                      private val tobKitchenTop: ToggleButton,
                      private val tobKitchenTable: ToggleButton,
                      private val tobKitchenBottom: ToggleButton,
                      private val tobLivingRoomLeft: ToggleButton,
                      private val tobLivingRoomTruss: ToggleButton,
                      private val tobLivingRoomRight: ToggleButton,
                      private val tobLivingRoomCouch: ToggleButton,
                      private val tobLivingRoomCloset: ToggleButton,
                      private val tobBedroomBack: ToggleButton,
                      private val tobBedroomFront: ToggleButton,

                   ) extends InitPresenter with ProgressBarSlider {

  private val logger: Logger          = new Logger(getClass.getSimpleName)
  private val hueService: HueService  = new MockHueService()

  /**
   * Init Presenter
   */
  override def initPresenter(): Unit = {
    logger.debug("Init Presenter")
    panBackground.setBackground(ImageHelper.getBackground())
    this.updateLightStates()
  }

  /**
   * Updates states from toggle buttons
   */
  def updateLightStates(): Unit = {

    def styleTranslator(state: Boolean): String = if(state) "-fx-background-color: yellow" else "-fx-background-color: grey"

    for ( (id, newState) <- this.hueService.getStates ) {

      id match {

        case 7    => tobKitchenTop.setStyle(styleTranslator(newState))
        case 8    => tobKitchenTable.setStyle(styleTranslator(newState))
        case 2    => tobKitchenBottom.setStyle(styleTranslator(newState))

        case 5    => tobLivingRoomLeft.setStyle(styleTranslator(newState))
        case 11   => tobLivingRoomTruss.setStyle(styleTranslator(newState))
        case 6    => tobLivingRoomRight.setStyle(styleTranslator(newState))
        case 4    => tobLivingRoomCouch.setStyle(styleTranslator(newState))
        case 10   => tobLivingRoomCloset.setStyle(styleTranslator(newState))

        case 9    => tobBedroomBack.setStyle(styleTranslator(newState))
        case 1    => tobBedroomFront.setStyle(styleTranslator(newState))

        case _    => logger.error("Light not found")

      }

    }

  }

  def prb_onMouseMoved(event: MouseEvent): Unit = {
    updateProgressBar(event)
  }

  def tob_onMouseMoved(event: MouseEvent): Unit = {

    val tob = event.getSource.asInstanceOf[javafx.scene.control.ToggleButton]

    val id: Int = tob.getUserData.toString.toInt

    this.hueService.toggleState(id)
    this.updateLightStates()
  }

}