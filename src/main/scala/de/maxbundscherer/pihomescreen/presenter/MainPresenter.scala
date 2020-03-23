package de.maxbundscherer.pihomescreen.presenter

import de.maxbundscherer.pihomescreen.img.ImageHelper
import de.maxbundscherer.pihomescreen.services.{SimpleCalendarService, SimpleHueService, SimpleWeatherService}
import de.maxbundscherer.pihomescreen.services.abstracts.{CalendarService, LightService, WeatherService}
import de.maxbundscherer.pihomescreen.utils.{InitPresenter, LightConfiguration, Logger, ProgressBarSlider, TimelineHelper}

import scalafx.Includes._
import scala.language.postfixOps
import scalafx.scene.control.{Label, ProgressBar, ToggleButton}
import scalafx.scene.image.ImageView
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

                      //ToggleButtons (bulbs)
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

                      //Fake sliders (rooms)
                      private val prbKitchen: ProgressBar,
                      private val prbLivingRoom: ProgressBar,
                      private val prbBedroom: ProgressBar,

                      //Image views (rooms)
                      private val imvKitchen: ImageView,
                      private val imvLivingRoom: ImageView,
                      private val imvBedroom: ImageView,

                   ) extends InitPresenter with ProgressBarSlider with TimelineHelper with LightConfiguration {

  private val logger: Logger                    = new Logger(getClass.getSimpleName)

  private val lightService: LightService        = new SimpleHueService()
  private val calendarService: CalendarService  = new SimpleCalendarService()
  private val weatherService: WeatherService    = new SimpleWeatherService()

  /**
   * Maps Lights to ToggleButtons
   */
  private val lightsMapping: Map[ToggleButton, Lights.Light] = Map (

    this.tobKitchenTop    -> Lights.KitchenTop,
    this.tobKitchenTable  -> Lights.KitchenTable,
    this.tobKitchenBottom -> Lights.KitchenBottom,

    this.tobLivingRoomLeft    -> Lights.LivingRoomLeft,
    this.tobLivingRoomTruss   -> Lights.LivingRoomTruss,
    this.tobLivingRoomRight   -> Lights.LivingRoomRight,
    this.tobLivingRoomCouch   -> Lights.LivingRoomCouch,
    this.tobLivingRoomCloset  -> Lights.LivingRoomCloset,

    this.tobBedroomBack   -> Lights.BedroomBack,
    this.tobBedroomFront  -> Lights.BedroomFront,

  )

  /**
   * Maps Rooms to ProgressBars
   */
  private val roomsMappingProgressBars: Map[ProgressBar, Rooms.Room] = Map (

    this.prbKitchen    -> Rooms.Kitchen,
    this.prbLivingRoom -> Rooms.LivingRoom,
    this.prbBedroom    -> Rooms.Bedroom,

  )

  /**
   * Maps Rooms to ProgressBars
   */
  private val roomsMappingImageViews: Map[ImageView, Rooms.Room] = Map (

    this.imvKitchen    -> Rooms.Kitchen,
    this.imvLivingRoom -> Rooms.LivingRoom,
    this.imvBedroom    -> Rooms.Bedroom,

  )


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

    this.startNewTimeline(interval = 1 m, repeat = true, title = "Clock Timeline", handler = () => {
      this.updateClock()
    })

    this.startNewTimeline(interval = 15 m, repeat = true, title = "Weather Timeline", handler = () => {
      this.updateWeather()
    })

    this.startNewTimeline(interval = 10 s, repeat = true, title = "Light Timeline", handler = () => {
      this.updateLightStates()
    })

    this.updateClock()
    this.updateWeather()
    this.updateLightStates()

    logger.info("End init presenter")
  }

  /**
   * Updates states from toggle buttons
   */
  private def updateLightStates(): Unit = {

    def lightStyleTranslator(state: Boolean): String = if(state) "-fx-background-color: orange" else "-fx-background-color: grey"
    def roomStyleTranslator(state: Boolean): String = if(state) "-fx-accent: orange" else "-fx-accent: grey"

    val actualBulbStates = this.lightService.getLightBulbStates

    for ( (light, lightState) <- actualBulbStates) {

      val targetToggleButton = this.lightsMapping.find(_._2 == light).get._1
      targetToggleButton.setStyle(lightStyleTranslator(lightState.on))

    }

    for ( (room, roomState) <- this.lightService.getRoomStates(Some(actualBulbStates))) {

      val targetProgressBar = this.roomsMappingProgressBars.find(_._2 == room).get._1
      targetProgressBar.setProgress(roomState.brightness)
      targetProgressBar.setStyle(roomStyleTranslator(roomState.on))

    }

  }

  /**
   * Updates clock
   */
  private def updateClock(): Unit = {
    this.lblClock.setText(this.calendarService.getHourAndMinuteToString)
    this.lblDate.setText(this.calendarService.getDateToString)
  }

  /**
   * Updates weather
   */
  private def updateWeather(): Unit = {
    this.lblWeather.setText(this.weatherService.getActualTempInCelsius + " C°")
  }

  /**
   * Progress Bar (fake slider) (room brightness)
   * @param event  MouseEvent
   */
  //TODO: Now it is onMouseClicked
  def prb_onMouseMoved(event: MouseEvent): Unit = {

    val newRoomBrightness: Double = this.processProgressBar(event, updateSender = false)

    val prb = event.getSource.asInstanceOf[javafx.scene.control.ProgressBar]

    val room: Rooms.Room = this.roomsMappingProgressBars(prb)

    this.lightService.setRoomBrightness(room, newRoomBrightness)
    this.updateLightStates()
  }

  /**
   * Image View (toggle room)
   * @param event MouseEvent
   */
  //TODO: Now it is onMouseClicked
  def imv_onMouseMoved(event: MouseEvent): Unit = {

    val imv = event.getSource.asInstanceOf[javafx.scene.image.ImageView]

    val room: Rooms.Room = this.roomsMappingImageViews(imv)

    this.lightService.toggleRoom(room)
    this.updateLightStates()
  }

  /**
   * Toggle Button (toggle light)
   * @param event MouseEvent
   */
  //TODO: Now it is onMouseClicked
  def tob_onMouseMoved(event: MouseEvent): Unit = {

    val tob = event.getSource.asInstanceOf[javafx.scene.control.ToggleButton]

    val light: Lights.Light = this.lightsMapping(tob)

    this.lightService.toggleLightBulb(light)
    this.updateLightStates()
  }

}