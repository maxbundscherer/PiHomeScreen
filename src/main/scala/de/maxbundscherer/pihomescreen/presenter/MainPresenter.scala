package de.maxbundscherer.pihomescreen.presenter

import de.maxbundscherer.pihomescreen.img.ImageHelper
import de.maxbundscherer.pihomescreen.services.{
  SimpleCalendarService,
  SimpleHealthCheckService,
  SimpleHueService,
  SimpleIssLocationService,
  SimpleJokeService,
  SimpleNightModeService,
  SimpleVideoService,
  SimpleWeatherService
}
import de.maxbundscherer.pihomescreen.services.abstracts.{
  CalendarService,
  HealthCheckService,
  IssLocationService,
  JokeService,
  LightService,
  NightModeService,
  VideoService,
  WeatherService
}
import de.maxbundscherer.pihomescreen.utils.CSVUtils.CSVItem
import de.maxbundscherer.pihomescreen.utils.{
  CSVUtils,
  Configuration,
  InitPresenter,
  LightConfiguration,
  ProgressBarSlider,
  TimelineHelper
}
import javafx.scene.media.{ Media, MediaPlayer, MediaView }
import org.apache.logging.log4j.scala.Logging
import scalafx.Includes._
import scalafx.application.Platform

import scala.language.postfixOps
import scalafx.scene.control.{ Alert, Button, ButtonType, Label, ProgressBar, ToggleButton }
import scalafx.scene.image.ImageView
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane
import scalafxml.core.macros.sfxml
import scalafx.geometry.Pos
import scalafx.scene.control.Alert.AlertType

import scala.concurrent.Future
import scala.util.{ Failure, Success }

@sfxml
class MainPresenter(
    //System
    private val imvWarning: ImageView,
    private val panBackground: Pane,
    private val cMediaView: MediaView,
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
    //LayoutPans
    private val panMiddle: Pane,
    private val panLeftArrow: Pane,
    private val panRightArrow: Pane,
    /*
                                                        // Second Pane
     */
    private val secondPane_btnSceneKitchenRead: Button,
    private val secondPane_btnSceneKitchenRelax: Button,
    private val secondPane_btnSceneLivingRoomRead: Button,
    private val secondPane_btnSceneLivingRoomDimmed: Button,
    private val secondPane_btnSceneLivingRoomRelax: Button,
    private val secondPane_btnSceneLivingRoomSky: Button,
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
    private val thirdPane_btnExit: Button,
    /*
                                                        // Fourth Pane
     */
    private val fourthPane_labTop: Label,
    private val fourthPane_labBottom: Label
) extends InitPresenter
    with ProgressBarSlider
    with TimelineHelper
    with LightConfiguration
    with Logging
    with Configuration {

  //TODO: Improve and remove this shit
  import scala.concurrent.ExecutionContext.Implicits.global

  private lazy val lightService: LightService             = new SimpleHueService()
  private lazy val calendarService: CalendarService       = new SimpleCalendarService()
  private lazy val weatherService: WeatherService         = new SimpleWeatherService()
  private lazy val healthCheckService: HealthCheckService = new SimpleHealthCheckService()
  private lazy val jokeService: JokeService               = new SimpleJokeService()
  private lazy val issLocationService: IssLocationService = new SimpleIssLocationService()
  private lazy val csvUtils: CSVUtils                     = new CSVUtils(Config.PhilipsHueReporting.reportFilepath)
  private lazy val videoService: VideoService             = new SimpleVideoService()
  private lazy val nightModeService: NightModeService     = new SimpleNightModeService(calendarService)

  /**
    * State
    */
  private var actualPane: Int           = 0
  private val maxPane: Int              = 3
  private var lastError: Option[String] = None

  /**
    * Maps Lights to ToggleButtons (Global Pane)
    */
  private val lightsMapping: Map[ToggleButton, (Lights.Light, Rooms.Room)] = Map(
    this.tobKitchenTop       -> (Lights.KitchenTop, Rooms.Kitchen),
    this.tobKitchenTable     -> (Lights.KitchenTable, Rooms.Kitchen),
    this.tobKitchenBottom    -> (Lights.KitchenBottom, Rooms.Kitchen),
    this.tobLivingRoomLeft   -> (Lights.LivingRoomLeft, Rooms.LivingRoom),
    this.tobLivingRoomTruss  -> (Lights.LivingRoomTruss, Rooms.LivingRoom),
    this.tobLivingRoomRight  -> (Lights.LivingRoomRight, Rooms.LivingRoom),
    this.tobLivingRoomCouch  -> (Lights.LivingRoomCouch, Rooms.LivingRoom),
    this.tobLivingRoomCloset -> (Lights.LivingRoomCloset, Rooms.LivingRoom),
    this.tobBedroomBack      -> (Lights.BedroomBack, Rooms.Bedroom),
    this.tobBedroomFront     -> (Lights.BedroomFront, Rooms.Bedroom)
  )

  /**
    * Maps Rooms to ProgressBars (Global Pane)
    */
  private val roomsMappingProgressBars: Map[ProgressBar, Rooms.Room] = Map(
    this.prbKitchen    -> Rooms.Kitchen,
    this.prbLivingRoom -> Rooms.LivingRoom,
    this.prbBedroom    -> Rooms.Bedroom
  )

  /**
    * Maps Rooms to ImageViews (Global Pane)
    */
  private val roomsMappingImageViews: Map[ImageView, Rooms.Room] = Map(
    this.imvKitchen    -> Rooms.Kitchen,
    this.imvLivingRoom -> Rooms.LivingRoom,
    this.imvBedroom    -> Rooms.Bedroom
  )

  /**
    * Init Presenter
    */
  override def initPresenter(): Unit = {

    logger.info("Init Presenter")

    val size: Int = 42

    this.tobKitchenTop.setGraphic(
      ImageHelper.getGetLightBulbImageView(lightType = 4, width = size, height = size)
    )
    this.tobKitchenTable.setGraphic(
      ImageHelper.getGetLightBulbImageView(lightType = 1, width = size, height = size)
    )
    this.tobKitchenBottom.setGraphic(
      ImageHelper.getGetLightBulbImageView(lightType = 4, width = size, height = size)
    )

    this.tobLivingRoomLeft.setGraphic(
      ImageHelper.getGetLightBulbImageView(lightType = 6, width = size, height = size)
    )
    this.tobLivingRoomTruss.setGraphic(
      ImageHelper.getGetLightBulbImageView(lightType = 2, width = size, height = size)
    )
    this.tobLivingRoomRight.setGraphic(
      ImageHelper.getGetLightBulbImageView(lightType = 6, width = size, height = size)
    )
    this.tobLivingRoomCouch.setGraphic(
      ImageHelper.getGetLightBulbImageView(lightType = 3, width = size, height = size)
    )
    this.tobLivingRoomCloset.setGraphic(
      ImageHelper.getGetLightBulbImageView(lightType = 6, width = size, height = size)
    )

    this.tobBedroomBack.setGraphic(
      ImageHelper.getGetLightBulbImageView(lightType = 5, width = size, height = size)
    )
    this.tobBedroomFront.setGraphic(
      ImageHelper.getGetLightBulbImageView(lightType = 5, width = size, height = size)
    )

    this.startNewTimeline(
      interval = 10 s,
      repeat = true,
      title = "Light States Timeline",
      handler = () => {
        this.updateLightStates()
      }
    )

    this.startNewTimeline(
      interval = 1 m,
      repeat = true,
      title = "Clock Timeline",
      handler = () => {
        this.updateClock()
      }
    )

    if (Config.NightMode.isEnabled) {
      logger.info("Start Night Mode Service")
      this.startNewTimeline(
        interval = 1 m,
        repeat = true,
        title = "Night Mode Timeline",
        handler = () => {
          this.triggerNightMode()
        }
      )
    }

    if (Config.IssLocation.isEnabled) {
      logger.info("Start Iss Location Service")
      this.startNewTimeline(
        interval = 10 m,
        repeat = true,
        title = "ISS Location Timeline",
        handler = () => {
          this.issLocationCheck()
        }
      )
    }

    if (Config.PhilipsHueReporting.isEnabled) {
      logger.info("Start Hue Reporting")
      this.startNewTimeline(
        interval = 10 m,
        repeat = true,
        title = "Hue Report Timeline",
        handler = () => {
          this.hueReporting(this.csvUtils)
        }
      )
    }

    this.startNewTimeline(
      interval = 15 m,
      repeat = true,
      title = "Background Timeline",
      handler = () => {
        this.updateBackground()
      }
    )

    this.startNewTimeline(
      interval = 15 m,
      repeat = true,
      title = "Weather Timeline",
      handler = () => {
        this.updateWeather()
      }
    )

    this.startNewTimeline(
      interval = 1 m,
      repeat = true,
      title = "Health check Timeline",
      handler = () => {
        this.doHealthCheck()
      }
    )

    this.startNewTimeline(
      interval = 1 h,
      repeat = true,
      title = "Joke Timeline",
      handler = () => {
        this.updateJokes()
      }
    )

    this.updateLightStates()

    this.updateClock()
    this.updateBackground()
    this.updateWeather()
    this.doHealthCheck()
    this.updateJokes()

    logger.info("End init presenter")
  }

  //TODO: Improve cache
  private var lastSeenBulbStates: Option[Map[lightService.Lights.Light, lightService.EntityState]] =
    None
  private var lastSeenRoomStates: Option[Map[lightService.Rooms.Room, lightService.EntityState]] =
    None

  private def lightStyleTranslator(state: Boolean): String =
    if (state) "-fx-background-color: orange" else "-fx-background-color: grey"
  private def roomStyleTranslator(state: Boolean): String =
    if (state) "-fx-accent: orange;" else "-fx-accent: grey;"

  /**
    * Updates states from toggle buttons
    */
  private def updateLightStates(): Unit =
    Future {

      val actualBulbStates
          : Either[String, Map[lightService.Lights.Light, lightService.EntityState]] =
        this.lightService.getLightBulbStates

      actualBulbStates match {

        case Left(error) =>
          logger.error(s"Can not get light bulb states ($error)")

        case Right(actualBulbStates) =>
          this.lastSeenBulbStates = Some(actualBulbStates)

          Platform.runLater(for ((light, lightState) <- actualBulbStates) {

            val targetToggleButton = this.lightsMapping.find(_._2._1.equals(light)).get._1
            targetToggleButton.setStyle(this.lightStyleTranslator(lightState.on))

          })

          val actualRoomStates = this.lightService.getRoomStates(Some(actualBulbStates))

          actualRoomStates match {

            case Left(error) =>
              logger.error(s"Can not get room states ($error)")

            case Right(actualRoomStates) =>
              this.lastSeenRoomStates = Some(actualRoomStates)

              Platform.runLater(for ((room, roomState) <- actualRoomStates) {

                val targetProgressBar = this.roomsMappingProgressBars.find(_._2 == room).get._1

                val newBrightness = if (roomState.brightness <= 0.20) 0.20 else roomState.brightness
                targetProgressBar.setProgress(newBrightness)
                targetProgressBar.setStyle(this.roomStyleTranslator(roomState.on))

              })

          }

      }

    }

  /**
    * Switch pane
    * @param right True = go right / False = go left
    */
  private def switchPane(right: Boolean, flagForceNoChange: Boolean = false): Unit = {

    val newDirection: Int = if (right) 1 else -1
    val result            = (this.actualPane + newDirection) % (this.maxPane + 1)

    this.actualPane =
      if (flagForceNoChange) this.actualPane else if (result < 0) this.maxPane else result

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
    * Updates clock (global)
    */
  private def updateClock(): Unit =
    Future {

      val a = this.calendarService.getHourAndMinuteToString
      val b = this.calendarService.getDateToString

      Platform.runLater(this.lblClock.setText(a))
      Platform.runLater(this.lblDate.setText(b))
    }

  private var _nightModeStateOn = false

  private def _nightModeOn(): Unit = {
    _nightModeStateOn = true
    panMiddle.visible = false
    panLeftArrow.visible = false
    panRightArrow.visible = false
    this.panFirst.setVisible(false)
    this.panSecond.setVisible(false)
    this.panThird.setVisible(false)
    this.panFourth.setVisible(false)
  }

  private def _nightModeOff(): Unit =
    if (_nightModeStateOn) {
      _nightModeStateOn = false
      panMiddle.visible = true
      panLeftArrow.visible = true
      panRightArrow.visible = true
      this.switchPane(right = false, flagForceNoChange = true)
    }

  /**
    * Trigger Night Mode (global)
    */
  private def triggerNightMode(): Unit =
    if (nightModeService.shouldHideScreen)
      this._nightModeOn()
    else
      this._nightModeOff()

  /**
    * Iss location check (global)
    */
  private def issLocationCheck(): Unit =
    this.issLocationService.isIssOverMyHouse match {
      case Left(error) => logger.error(s"Error check iss location ($error)")
      case Right(isOverHouse) =>
        if (isOverHouse) {
          logger.debug("ISS is over my house")
          this.lightService.setAlarmOnBulb(Lights.LivingRoomRight)
        }

    }

  /**
    * Hue Reporting (global)
    */
  private def hueReporting(csvUtils: CSVUtils): Unit = {

    logger.debug("Report hue bulbs now")

    this.lightService.getLightBulbStates match {
      case Left(error) => logger.error(s"Error in getLightBulbStates ($error)")
      case Right(data) =>
        val ans = data map {
            case (bulbId, state) =>
              CSVItem(
                bulbId = bulbId.toString,
                isOn = state.on.toString,
                brightness = state.brightness.toString,
                saturation = state.saturation,
                stateX = state.stateX,
                stateY = state.stateY,
                miredColor = state.miredColor,
                stateColorMode = state.stateColorMode,
                stateReachable = state.stateReachable,
                stateEffect = state.stateEffect,
                stateMode = state.stateMode
              )
          }
        csvUtils.writeToCSVFile(
          data = ans.toVector
        )
    }

  }

  private def stopVideo(): Unit =
    if (this.mediaPlayer.isDefined) {
      this.mediaPlayer.get.stop()
      this.mediaPlayer.get.dispose()
    }

  var showedVideo                      = false
  var mediaPlayer: Option[MediaPlayer] = None

  /**
    * Updates Background (global)
    */
  private def updateBackground(): Unit = {

    if (!showedVideo)
      this.videoService.getLocalRandomVideo match {
        case Failure(exception) =>
          logger.warn(s"No local video found (${exception.getLocalizedMessage})")
          this.videoService.downloadRandomVideoAndConvert()
        case Success(localVideoFilePath) =>
          val m = new Media(
            new java.io.File(localVideoFilePath).toURI.toString
          )

          val p = new MediaPlayer(m)
          p.setAutoPlay(true)
          p.setCycleCount(MediaPlayer.INDEFINITE)
          this.mediaPlayer = Some(p)

          this.cMediaView.setSmooth(true)
          this.cMediaView.mediaPlayer.set(p)

          this.cMediaView.visible = true
          showedVideo = true
          return
      }

    //No Video

    this.stopVideo()
    this.panBackground.setBackground(ImageHelper.getNextBackground())

    this.cMediaView.visible = false
    showedVideo = false

    this.videoService.downloadRandomVideoAndConvert()
  }

  /**
    * Updates weather (global)
    */
  private def updateWeather(): Unit =
    Future {

      val ans: String = this.weatherService.getActualTempInCelsius match {
        case Left(error) =>
          logger.error(s"Can not get weather ($error)")
          "??"
        case Right(data) => data
      }

      Platform.runLater(this.lblWeather.setText(ans + " C°"))
    }

  /**
    * Do health check
    */
  private def doHealthCheck(): Unit =
    Future {
      this.healthCheckService.doHealthCheck() match {

        case Left(error) =>
          this.lastError = Some(error)
          Platform.runLater(this.imvWarning.setVisible(true))

        case Right(_) =>
          Platform.runLater(this.imvWarning.setVisible(false))

      }
    }

  /**
    * Shows error alert
    */
  def show_ErrorAlert(): Unit = {

    val ButtonTypeOne = new ButtonType("Nicht beenden")
    val ButtonTypeTwo = new ButtonType("Beenden")

    val alert = new Alert(AlertType.Warning) {
      title = "Programm beenden"
      headerText = "Möchten Sie das Programm beenden?"
      contentText = lastError.getOrElse("Es ist keine Warnung aufgetreten.")
      buttonTypes = Seq(ButtonTypeOne, ButtonTypeTwo)
    }

    val result = alert.showAndWait()

    result match {
      case Some(ButtonTypeOne) => //Do nothing
      case Some(ButtonTypeTwo) => Platform.exit()
      case _                   => logger.error(s"Can not process $result")
    }

  }

  /**
    * Progress Bar (fake slider) (room brightness)
    * @param event  MouseEvent
    */
  def prb_onMouseClicked(event: MouseEvent): Unit = {

    val newRoomBrightness: Double = this.processProgressBar(event, updateSender = false)

    val prb = event.getSource.asInstanceOf[javafx.scene.control.ProgressBar]

    val room: Rooms.Room = this.roomsMappingProgressBars(prb)

    val temporaryTarget = this.roomsMappingProgressBars.find(_._2.equals(room)).get._1
    temporaryTarget.setStyle(this.roomStyleTranslator(true))

    Future {
      this.lightService.setRoomBrightness(room, newRoomBrightness)
      this.updateLightStates()
    }

    this.nightModeService.reportInteraction()
    this._nightModeOff()
  }

  /**
    * Image View (toggle room)
    * @param event MouseEvent
    */
  def imv_onMouseClicked(event: MouseEvent): Unit = {

    val imv = event.getSource.asInstanceOf[javafx.scene.image.ImageView]

    val room: Rooms.Room = this.roomsMappingImageViews(imv)

    val temporaryNewState: Boolean = this.lastSeenRoomStates match {
      case None        => true
      case Some(cache) => !cache(room).on
    }

    val temporaryTarget = this.roomsMappingProgressBars.find(_._2.equals(room)).get._1
    temporaryTarget.setStyle(this.roomStyleTranslator(temporaryNewState))

    Future {
      this.lightService.toggleRoom(room)
      this.updateLightStates()
    }

    this.nightModeService.reportInteraction()
    this._nightModeOff()
  }

  /**
    * Toggle Button (toggle light)
    * @param event MouseEvent
    */
  def tob_onMouseClicked(event: MouseEvent): Unit = {

    val tob = event.getSource.asInstanceOf[javafx.scene.control.ToggleButton]

    val light: Lights.Light = this.lightsMapping(tob)._1

    val temporaryNewState: Boolean = this.lastSeenBulbStates match {
      case None        => true
      case Some(cache) => !cache(light).on
    }

    val selected = this.lightsMapping.find(_._2._1.equals(light)).get

    val temporaryTargetTB: ToggleButton = selected._1
    temporaryTargetTB.setStyle(this.lightStyleTranslator(temporaryNewState))

    if (temporaryNewState) {
      val temporaryRoom     = selected._2._2
      val temporaryTargetPB = this.roomsMappingProgressBars.find(_._2.equals(temporaryRoom)).get._1
      temporaryTargetPB.setStyle(this.roomStyleTranslator(state = true))
    }

    Future {
      this.lightService.toggleLightBulb(light)
      this.updateLightStates()
    }

    this.nightModeService.reportInteraction()
    this._nightModeOff()
  }

  def panBackground_onMouseClicked(event: MouseEvent): Unit = {
    this.nightModeService.reportInteraction()
    this._nightModeOff()
  }

  /**
    * Click on pane (Arrow left)
    * @param event MouseEvent
    */
  def panArrowLeft_onMouseClicked(event: MouseEvent): Unit = {
    this.switchPane(right = false)
    this.nightModeService.reportInteraction()
    this._nightModeOff()
  }

  /**
    * Click on pane (Arrow right)
    * @param event MouseEvent
    */
  def panArrowRight_onMouseClicked(event: MouseEvent): Unit = {
    this.switchPane(right = true)
    this.nightModeService.reportInteraction()
    this._nightModeOff()
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
  private val sceneMappingButtons: Map[Button, (Scenes.Scene, Rooms.Room)] = Map(
    this.secondPane_btnSceneKitchenRead       -> (Scenes.KitchenRead, Rooms.Kitchen),
    this.secondPane_btnSceneKitchenRelax      -> (Scenes.KitchenRelax, Rooms.Kitchen),
    this.secondPane_btnSceneLivingRoomRead    -> (Scenes.LivingRoomRead, Rooms.LivingRoom),
    this.secondPane_btnSceneLivingRoomDimmed  -> (Scenes.LivingRoomDimmed, Rooms.LivingRoom),
    this.secondPane_btnSceneLivingRoomRelax   -> (Scenes.LivingRoomRelax, Rooms.LivingRoom),
    this.secondPane_btnSceneLivingRoomSky     -> (Scenes.LivingRoomSky, Rooms.LivingRoom),
    this.secondPane_btnSceneBedroomRead       -> (Scenes.BedroomRead, Rooms.Bedroom),
    this.secondPane_btnSceneBedroomNightLight -> (Scenes.BedroomNightLight, Rooms.Bedroom),
    this.secondPane_btnSceneBedroomRelax      -> (Scenes.BedroomRelax, Rooms.Bedroom),
    this.secondPane_btnSceneBedroomRed        -> (Scenes.BedroomRed, Rooms.Bedroom)
  )

  /**
    * Click on scene button
    * @param event MouseEvent
    */
  def secondPane_btn_onMouseClicked(event: MouseEvent): Unit = {

    val btn = event.getSource.asInstanceOf[javafx.scene.control.Button]

    val mapped = this.sceneMappingButtons(btn)

    val scene: Scenes.Scene = mapped._1
    val room: Rooms.Room    = mapped._2

    val temporaryTarget = this.roomsMappingProgressBars.find(_._2.equals(room)).get._1
    temporaryTarget.setStyle(this.roomStyleTranslator(true))

    Future {
      this.lightService.setScene(scene)
      this.updateLightStates()
    }

    this.nightModeService.reportInteraction()
    this._nightModeOff()
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
  private val routineMappingButtons: Map[Button, Routines.Routine] = Map(
    this.thirdPane_btnRoutineWakeUp  -> Routines.WakeUp,
    this.thirdPane_btnRoutineRelax   -> Routines.Relax,
    this.thirdPane_btnRoutineDarkRed -> Routines.DarkRed,
    this.thirdPane_btnRoutineAllOff  -> Routines.AllOff
  )

  /**
    * Click on routine or quick action button
    * @param event MouseEvent
    */
  def thirdPane_btn_onMouseClicked(event: MouseEvent): Unit = {

    val btn = event.getSource.asInstanceOf[javafx.scene.control.Button]

    val routine: Routines.Routine = this.routineMappingButtons(btn)

    //TODO: Implement with future handler
    this.lightService.triggerRoutine(routine)
    this.updateLightStates()

    this.nightModeService.reportInteraction()
    this._nightModeOff()
  }

  /**
    * Click on sleep button
    * @param event MouseEvent
    */
  def thirdPane_btnSleep_onMouseClicked(event: MouseEvent): Unit = {

    this.lightService.triggerRoutine(Routines.Sleep)

    this.startNewTimeline(
      interval = 3 m,
      repeat = false,
      title = "Sleep Routine",
      handler = () => {
        this.lightService.triggerRoutine(Routines.AllOff)
        //TODO: Implement with future handler
        this.updateLightStates()
      }
    )

    //TODO: Implement with future handler
    this.updateLightStates()

    this.nightModeService.reportInteraction()
    this._nightModeOff()
  }

  /**
    * #####################################################################################
    * #####################################################################################
    * ######################################## Fourth Pane ################################
    * #####################################################################################
    * #####################################################################################
    */

  /**
    * Updates jokes (fourthPane)
    */
  private def updateJokes(): Unit =
    Future {

      val firstJoke = this.jokeService.getFirstJoke() match {

        case Left(error) =>
          logger.error(s"Can not get first joke ($error)")
          "Fehler: Kann Witz nicht laden"

        case Right(joke) => joke
      }

      val secondJoke = this.jokeService.getSecondJoke() match {

        case Left(error) =>
          logger.error(s"Can not get second joke ($error)")
          "Fehler: Kann Witz nicht laden"

        case Right(joke) => joke
      }

      Platform.runLater {
        this.fourthPane_labTop.setText(firstJoke)
        this.fourthPane_labBottom.setText(secondJoke)

        //Fix: Java FX Center Bug (Label alignment)
        this.fourthPane_labTop.setAlignment(Pos.BaselineCenter)
        this.fourthPane_labBottom.setAlignment(Pos.BaselineCenter)
      }

    }

}
