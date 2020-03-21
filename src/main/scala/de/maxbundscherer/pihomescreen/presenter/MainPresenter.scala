package de.maxbundscherer.pihomescreen.presenter

import de.maxbundscherer.pihomescreen.img.ImageHelper
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
                      private val tobKitchenTop_7: ToggleButton,
                      private val tobKitchenTable_8: ToggleButton,
                      private val tobKitchenBottom_2: ToggleButton,
                      private val tobLivingRoomLeft_5: ToggleButton,
                      private val tobLivingRoomTruss_11: ToggleButton,
                      private val tobLivingRoomRight_6: ToggleButton,
                      private val tobLivingRoomCouch_4: ToggleButton,
                      private val tobLivingRoomCloset_10: ToggleButton,
                      private val tobBedroomBack_9: ToggleButton,
                      private val tobBedroomFront_1: ToggleButton,

                   ) extends InitPresenter with ProgressBarSlider {

  private val logger = new Logger(getClass.getSimpleName)

  /**
   * Init Presenter
   */
  override def initPresenter(): Unit = {
    logger.debug("Init Presenter")
    panBackground.setBackground(ImageHelper.getBackground())
  }

  def prb_onMouseMoved(event: MouseEvent): Unit = {
    updateProgressBar(event)
  }

}