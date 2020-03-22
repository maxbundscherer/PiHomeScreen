package de.maxbundscherer.pihomescreen.presenter

import de.maxbundscherer.pihomescreen.img.ImageHelper
import de.maxbundscherer.pihomescreen.services.{MockLightService, SimpleCalendarService, SimpleWeatherService}
import de.maxbundscherer.pihomescreen.services.abstracts.{CalendarService, LightService, WeatherService}
import de.maxbundscherer.pihomescreen.utils.{InitPresenter, Logger, ProgressBarSlider, TimelineHelper}

import scalafx.Includes._
import scala.language.postfixOps
import scalafx.scene.control.{Label, ProgressBar, ToggleButton}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml

@sfxml
class MainPresenter(

                      //System
                      private val panBackground: Pane,
                      private val lblClock: Label,
                      private val lblDate: Label,
                      private val lblWeather: Label,

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

                      //Fake sliders
                      private val prbKitchen: ProgressBar,
                      private val prbLivingRoom: ProgressBar,
                      private val prbBedroom: ProgressBar,

                   ) extends InitPresenter with ProgressBarSlider with TimelineHelper {

  private val logger: Logger                    = new Logger(getClass.getSimpleName)

  private val lightService: LightService        = new MockLightService()
  private val calendarService: CalendarService  = new SimpleCalendarService()
  private val weatherService: WeatherService    = new SimpleWeatherService()

  /**
   * Init Presenter
   */
  override def initPresenter(): Unit = {

    logger.info("Init Presenter")

    this.panBackground.setBackground(ImageHelper.getBackground())

    val size: Int = 42
    
    this.tobKitchenTop.setGraphic(ImageHelper.getGetLightBulbImageView(lightType = 4, width = size, height = size))
    this.tobKitchenTable.setGraphic(ImageHelper.getGetLightBulbImageView(lightType = 1, width = size, height = size))
    this.tobKitchenBottom.setGraphic(ImageHelper.getGetLightBulbImageView(lightType = 4, width = size, height = size))

    this.tobLivingRoomLeft.setGraphic(ImageHelper.getGetLightBulbImageView(lightType = 6, width = size, height = size))
    this.tobLivingRoomTruss.setGraphic(ImageHelper.getGetLightBulbImageView(lightType = 2, width = size, height = size))
    this.tobLivingRoomRight.setGraphic(ImageHelper.getGetLightBulbImageView(lightType = 6, width = size, height = size))
    this.tobLivingRoomCouch.setGraphic(ImageHelper.getGetLightBulbImageView(lightType = 3, width = size, height = size))
    this.tobLivingRoomCloset.setGraphic(ImageHelper.getGetLightBulbImageView(lightType = 6, width = size, height = size))

    this.tobBedroomBack.setGraphic(ImageHelper.getGetLightBulbImageView(lightType = 5, width = size, height = size))
    this.tobBedroomFront.setGraphic(ImageHelper.getGetLightBulbImageView(lightType = 5, width = size, height = size))

    //TODO: Improve first time & duration
    this.startNewTimeline(interval = 1 m, repeat = true, title = "Clock Timeline", handler = () => {
      this.lblClock.setText(this.calendarService.getHourAndMinuteToString)
      this.lblDate.setText(this.calendarService.getDateToString)
    })

    //TODO: Improve first time & duration
    this.startNewTimeline(interval = 1 m, repeat = true, title = "Weather Timeline", handler = () => {
      this.lblWeather.setText(this.weatherService.getActualTempInCelsius + " C°")
    })

    this.updateLightStates()

    logger.info("End init presenter")
  }

  /**
   * Updates states from toggle buttons
   */
  def updateLightStates(): Unit = {

    def styleTranslator(state: Boolean): String = if(state) "-fx-background-color: yellow" else "-fx-background-color: grey"

    for ( (lightId, newState) <- this.lightService.getLightBulbStates) {

      lightId match {

        case 7    => this.tobKitchenTop.setStyle(styleTranslator(newState))
        case 8    => this.tobKitchenTable.setStyle(styleTranslator(newState))
        case 2    => this.tobKitchenBottom.setStyle(styleTranslator(newState))

        case 5    => this.tobLivingRoomLeft.setStyle(styleTranslator(newState))
        case 11   => this.tobLivingRoomTruss.setStyle(styleTranslator(newState))
        case 6    => this.tobLivingRoomRight.setStyle(styleTranslator(newState))
        case 4    => this.tobLivingRoomCouch.setStyle(styleTranslator(newState))
        case 10   => this.tobLivingRoomCloset.setStyle(styleTranslator(newState))

        case 9    => this.tobBedroomBack.setStyle(styleTranslator(newState))
        case 1    => this.tobBedroomFront.setStyle(styleTranslator(newState))

        case _    => logger.error(s"Light not found (id=$lightId)")

      }

    }

    for ( (roomId, newValue) <- this.lightService.getRoomBrightness) {

      roomId match {

        case 0 => this.prbKitchen.setProgress(newValue)
        case 1 => this.prbLivingRoom.setProgress(newValue)
        case 2 => this.prbBedroom.setProgress(newValue)

        case _ => logger.error(s"Room not found (id=$roomId)")

      }

    }

  }

  /**
   * Progress Bar (fake slider) (room brightness)
   * @param event  MouseEvent (updates slider / userData = roomId)
   */
  def prb_onMouseMoved(event: MouseEvent): Unit = {

    val newRoomBrightness: Double = this.updateProgressBar(event)

    val prb = event.getSource.asInstanceOf[javafx.scene.control.ProgressBar]

    val roomId: Int = prb.getUserData.toString.toInt

    this.lightService.setRoomBrightness(roomId, newRoomBrightness)
    this.updateLightStates()
  }

  /**
   * Image View (toggle room)
   * @param event MouseEvent (userData = roomId)
   */
  def imv_onMouseMoved(event: MouseEvent): Unit = {

    val imv = event.getSource.asInstanceOf[javafx.scene.image.ImageView]

    val roomId: Int = imv.getUserData.toString.toInt

    this.lightService.toggleRoom(roomId)
    this.updateLightStates()
  }

  /**
   * Toggle Button (toggle light)
   * @param event MouseEvent (userData = lightId)
   */
  def tob_onMouseMoved(event: MouseEvent): Unit = {

    val tob = event.getSource.asInstanceOf[javafx.scene.control.ToggleButton]

    val lightId: Int = tob.getUserData.toString.toInt

    this.lightService.toggleLightBulb(lightId)
    this.updateLightStates()
  }

}