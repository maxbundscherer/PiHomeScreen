package de.maxbundscherer.pihomescreen.presenter

import de.maxbundscherer.pihomescreen.img.ImageHelper
import de.maxbundscherer.pihomescreen.services.{SimpleCalendarService, SimpleHueService, SimpleWeatherService}
import de.maxbundscherer.pihomescreen.services.abstracts.{CalendarService, LightService, WeatherService}
import de.maxbundscherer.pihomescreen.utils.{InitPresenter, LightConfiguration, Logger, ProgressBarSlider, TimelineHelper}

import scalafx.Includes._
import scalafx.application.Platform
import scala.language.postfixOps
import scalafx.scene.control.{Button, Label, ProgressBar, ToggleButton}
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

                      //Pans
                      private val panFirst: Pane,
                      private val panSecond: Pane,
                      private val panThird: Pane,
                      private val panFourth: Pane,

                      /*
                      // Second Pane
                       */
                      private val secondPane_btnSceneKitchenRead: Button,
                      private val secondPane_btnSceneKitchenRelax: Button,

                      private val secondPane_btnSceneLivingRoomRead: Button,
                      private val secondPane_btnSceneLivingRoomDimmed: Button,
                      private val secondPane_btnSceneLivingRoomRelax: Button,
                      private val secondPane_btnSceneLivingRoomDarkRed: Button,

                      private val secondPane_btnSceneBedroomRead: Button,
                      private val secondPane_btnSceneBedroomRelax: Button,
                      private val secondPane_btnSceneBedroomNightLight: Button,
                      private val secondPane_btnSceneBedroomRed: Button,

                      /*
                      // Third Pane
                       */
                      private val thirdPane_btnRoutineWakeUp: Button,
                      private val thirdPane_btnRoutineRelax: Button,
                      private val thirdPane_btnRoutineDarkRed: Button,
                      private val thirdPane_btnSleep: Button,
                      private val thirdPane_btnRoutineAllOff: Button,
                      private val thirdPane_btnExit: Button

                   ) extends InitPresenter with ProgressBarSlider with TimelineHelper with LightConfiguration {

  private val logger: Logger                    = new Logger(getClass.getSimpleName)

  private val lightService: LightService        = new SimpleHueService()
  private val calendarService: CalendarService  = new SimpleCalendarService()
  private val weatherService: WeatherService    = new SimpleWeatherService()

  /**
   * Actual pane (firstPane=0) and maxPane
   */
  private var actualPane: Int                   = 0
  private val maxPane: Int                      = 2 //TODO: Add third pane (3) - already in fxml

  /**
   * Maps Lights to ToggleButtons (Global Pane)
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
   * Maps Rooms to ProgressBars (Global Pane)
   */
  private val roomsMappingProgressBars: Map[ProgressBar, Rooms.Room] = Map (

    this.prbKitchen    -> Rooms.Kitchen,
    this.prbLivingRoom -> Rooms.LivingRoom,
    this.prbBedroom    -> Rooms.Bedroom,

  )

  /**
   * Maps Rooms to ImageViews (Global Pane)
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

    this.startNewTimeline(interval = 15 m, repeat = true, title = "Background Timeline", handler = () => {
      this.updateBackground()
    })

    this.startNewTimeline(interval = 15 m, repeat = true, title = "Weather Timeline", handler = () => {
      this.updateWeather()
    })

    this.startNewTimeline(interval = 10 s, repeat = true, title = "Light Timeline", handler = () => {
      this.updateLightStates()
    })

    this.updateClock()
    this.updateBackground()
    this.updateWeather()
    this.updateLightStates()

    logger.info("End init presenter")
  }

  /**
   * Updates states from toggle buttons
   */
  private def updateLightStates(): Unit = {

    def lightStyleTranslator(state: Boolean): String = if(state) "-fx-background-color: orange" else "-fx-background-color: grey"
    def roomStyleTranslator(state: Boolean): String = if(state) "-fx-accent: orange;" else "-fx-accent: grey;"

    val actualBulbStates: Either[String, Map[lightService.Lights.Light, lightService.EntityState]] = this.lightService.getLightBulbStates

    actualBulbStates match {

      case Left(error) =>

        logger.error(s"Can not get light bulb states $error")

      case Right(actualBulbStates) =>

        for ( (light, lightState) <- actualBulbStates) {

          val targetToggleButton = this.lightsMapping.find(_._2 == light).get._1
          targetToggleButton.setStyle(lightStyleTranslator(lightState.on))

        }

        val actualRoomStates = this.lightService.getRoomStates(Some(actualBulbStates))

        actualRoomStates match {

          case Left(error) =>

            logger.error(s"Can not get room states $error")

          case Right(actualRoomStates) =>

            for ( (room, roomState) <- actualRoomStates) {

              val targetProgressBar = this.roomsMappingProgressBars.find(_._2 == room).get._1

              val newBrightness = if(roomState.brightness <= 0.20) 0.20 else roomState.brightness
              targetProgressBar.setProgress(newBrightness)
              targetProgressBar.setStyle(roomStyleTranslator(roomState.on))

            }

        }

    }

  }

  /**
   * Switch pane
   * @param right True = go right / False = go left
   */
  private def switchPane(right: Boolean): Unit = {

    val newDirection: Int = if(right) 1 else -1
    val result = this.actualPane + newDirection

    this.actualPane = if(result < 0) this.maxPane else result % (this.maxPane + 1)

    this.actualPane match {

      case 0 =>

        this.panFirst.setVisible(true)
        this.panSecond.setVisible(false)
        this.panThird.setVisible(false)
        this.panFourth.setVisible(false)

      case 1 =>

        this.panFirst.setVisible(false)
        this.panSecond.setVisible(true)
        this.panThird.setVisible(false)
        this.panFourth.setVisible(false)

      case 2 =>

        this.panFirst.setVisible(false)
        this.panSecond.setVisible(false)
        this.panThird.setVisible(true)
        this.panFourth.setVisible(false)

      case _ =>

        this.panFirst.setVisible(false)
        this.panSecond.setVisible(false)
        this.panThird.setVisible(false)
        this.panFourth.setVisible(true)

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
   * Updates Background
   */
  private def updateBackground(): Unit = {

    this.panBackground.setBackground(ImageHelper.getNextBackground())
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
  def prb_onMouseClicked(event: MouseEvent): Unit = {

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
  def imv_onMouseClicked(event: MouseEvent): Unit = {

    val imv = event.getSource.asInstanceOf[javafx.scene.image.ImageView]

    val room: Rooms.Room = this.roomsMappingImageViews(imv)

    this.lightService.toggleRoom(room)
    this.updateLightStates()
  }

  /**
   * Toggle Button (toggle light)
   * @param event MouseEvent
   */
  def tob_onMouseClicked(event: MouseEvent): Unit = {

    val tob = event.getSource.asInstanceOf[javafx.scene.control.ToggleButton]

    val light: Lights.Light = this.lightsMapping(tob)

    this.lightService.toggleLightBulb(light)
    this.updateLightStates()
  }

  /**
   * Click on pane (Arrow left)
   * @param event MouseEvent
   */
  def panArrowLeft_onMouseClicked(event: MouseEvent): Unit = {

    this.switchPane(right = false)
  }

  /**
   * Click on pane (Arrow right)
   * @param event MouseEvent
   */
  def panArrowRight_onMouseClicked(event: MouseEvent): Unit = {

    this.switchPane(right = true)
  }

  /**
   * #####################################################################################
   * #####################################################################################
   * ######################################## Second Pane ################################
   * #####################################################################################
   * #####################################################################################
   */

  /**
   * Maps Scenes to Buttons (Second Pane)
   */
  private val sceneMappingButtons: Map[Button, Scenes.Scene] = Map (

    this.secondPane_btnSceneKitchenRead -> Scenes.KitchenRead,
    this.secondPane_btnSceneKitchenRelax -> Scenes.KitchenRelax,

    this.secondPane_btnSceneLivingRoomRead -> Scenes.LivingRoomRead,
    this.secondPane_btnSceneLivingRoomDimmed -> Scenes.LivingRoomDimmed,
    this.secondPane_btnSceneLivingRoomRelax -> Scenes.LivingRoomRelax,
    this.secondPane_btnSceneLivingRoomDarkRed -> Scenes.LivingRoomDarkRed,

    this.secondPane_btnSceneBedroomRead -> Scenes.BedroomRead,
    this.secondPane_btnSceneBedroomNightLight -> Scenes.BedroomNightLight,
    this.secondPane_btnSceneBedroomRelax -> Scenes.BedroomRelax,
    this.secondPane_btnSceneBedroomRed -> Scenes.BedroomRed,

  )

  /**
   * Click on scene button
   * @param event MouseEvent
   */
  def secondPane_btn_onMouseClicked(event: MouseEvent): Unit = {

    val btn = event.getSource.asInstanceOf[javafx.scene.control.Button]

    val scene: Scenes.Scene = this.sceneMappingButtons(btn)

    this.lightService.setScene(scene)
    this.updateLightStates()
  }

  /**
   * #####################################################################################
   * #####################################################################################
   * ######################################## Third Pane #################################
   * #####################################################################################
   * #####################################################################################
   */

  /**
   * Maps Routines to Buttons (Third Pane)
   */
  private val routineMappingButtons: Map[Button, Routines.Routine] = Map (

    this.thirdPane_btnRoutineWakeUp -> Routines.WakeUp,
    this.thirdPane_btnRoutineRelax -> Routines.Relax,
    this.thirdPane_btnRoutineDarkRed -> Routines.DarkRed,
    this.thirdPane_btnRoutineAllOff -> Routines.AllOff,

  )

  /**
   * Click on routine or quick action button
   * @param event MouseEvent
   */
  def thirdPane_btn_onMouseClicked(event: MouseEvent): Unit = {

    val btn = event.getSource.asInstanceOf[javafx.scene.control.Button]

    val routine: Routines.Routine = this.routineMappingButtons(btn)

    this.lightService.triggerRoutine(routine)
    this.updateLightStates()
  }

  /**
   * Click on sleep button
   * @param event MouseEvent
   */
  def thirdPane_btnSleep_onMouseClicked(event: MouseEvent): Unit = {

    this.lightService.triggerRoutine( Routines.Sleep )

    this.startNewTimeline(interval = 3 m, repeat = false, title = "Sleep Routine", handler = () => {
      this.lightService.triggerRoutine( Routines.AllOff )
      this.updateLightStates()
    })

    this.updateLightStates()
  }

  /**
   * Click on exit button
   * @param event MouseEvent
   */
  def thirdPane_btnExit_onMouseClicked(event: MouseEvent): Unit = {

    Platform.exit()
  }

}