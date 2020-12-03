package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.LightService
import de.maxbundscherer.pihomescreen.utils.{ Configuration, JSONWebclient }

import org.apache.logging.log4j.scala.Logging

class SimpleHueService extends LightService with JSONWebclient with Configuration with Logging {

  import io.circe.Decoder
  import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  private val targetUrl: String =
    s"${Config.PhilipsHue.bridgeApiUrl}${Config.PhilipsHue.bridgeApiUsername}/"

  /**
    * Get light bulbs states
    * @return Either Left = Error Message / Right = Map (Light, EntityState)
    */
  override def getLightBulbStates: Either[String, Map[Lights.Light, EntityState]] = {

    case class State(
        on: Boolean,
        bri: Int,
        sat: Option[Int],
        xy: Option[Vector[Float]],
        ct: Option[Int],
        colormode: Option[String],
        mode: Option[String],
        effect: Option[String],
        reachable: Option[Boolean]
    )
    case class HueLight(state: State)

    Webclient.getRequestToJson(
      decoder = Decoder[Map[Int, HueLight]],
      url = this.targetUrl + "lights"
    ) match {

      case Left(error) => Left(error)

      case Right(data) =>
        Right(
          Lights.ALL_LIGHTS.map { light =>
            val stateXYTuple: Option[(Float, Float)] = data(light).state.xy match {
              case Some(value) => if (value.size == 2) Some((value(0), value(1))) else None
              case None        => None
            }
            light -> EntityState(
              on = data(light).state.on,
              brightness = data(light).state.bri / 255.0, //Norm to 0 to 1
              saturation = data(light).state.sat,
              stateX = stateXYTuple match {
                case Some(value) => Some(value._1)
                case None        => None
              },
              stateY = stateXYTuple match {
                case Some(value) => Some(value._2)
                case None        => None
              },
              miredColor = data(light).state.ct,
              stateColorMode = data(light).state.colormode,
              stateReachable = data(light).state.reachable,
              stateEffect = data(light).state.effect,
              stateMode = data(light).state.mode
            )
          }.toMap
        )

    }

  }

  /**
    * Get room brightness
    * @param actualBulbStates Some = Cached light states / None = Calls getLightBulbStates
    * @return Either Left = Error Message / Right = Map (Room, EntityState)
    */
  override def getRoomStates(
      actualBulbStates: Option[Map[Lights.Light, EntityState]]
  ): Either[String, Map[Rooms.Room, EntityState]] = {

    //Get from cache or refresh
    val bulbStates: Either[String, Map[Lights.Light, EntityState]] = actualBulbStates match {
      case Some(sth) => Right(sth)
      case None      => this.getLightBulbStates
    }

    bulbStates match {

      case Left(error) => Left(error)

      case Right(data) =>
        val roomWithLightBulbStates: Map[Rooms.Room, Vector[EntityState]] =
          Rooms.ALL_ROOMS.map(room => room -> room.map(light => data(light))).toMap

        Right(roomWithLightBulbStates.map {
          case (room, lightBulbStates) =>
            room -> EntityState(
              on = lightBulbStates.exists(_.on == true), //One bulb on is enough
              brightness =
                lightBulbStates.map(_.brightness).max, //Take brightness from max bright bulb
              saturation = None,
              stateX = None,
              stateY = None,
              miredColor = None,
              stateColorMode = None,
              stateReachable = None,
              stateEffect = None,
              stateMode = None
            )

        })

    }

  }

  /**
    * Toggle state from light bulb
    * @param light Light
    * @param newState Some = Set light bulb to value / None = toggle light bulb
    * @param newBrightness 0 to 1 Some = Set light bulb to value / None = Dont change it
    */
  def toggleLightBulb(
      light: Lights.Light,
      newState: Option[Boolean] = None,
      newBrightness: Option[Double] = None
  ): Unit = {

    val stateString =
      if (
        newState.getOrElse(
          this.getLightBulbStates match {
            case Left(error) =>
              logger.error(s"Can not get state from $light ($error)")
              false
            case Right(data) => !data(light).on
          }
        )
      ) "true"
      else "false"

    val brightnessString =
      if (newBrightness.isDefined) ", \"bri\":" + (newBrightness.get * 255).toInt else ""

    val jsonRequestString = "{\"on\":" + stateString + brightnessString + "}"

    val ans: Either[String, Option[Nothing]] = Webclient.putRequestToJSON(
      decoder = None,
      rawBody = jsonRequestString,
      url = this.targetUrl + s"lights/$light/state"
    )

    if (ans.isLeft) logger.error(s"Can not toggle light bulb (${ans.left.get})")

  }

  /**
    * Toggle room
    * @param room  Room
    * @param value Some = value / None = toggle
    */
  override def toggleRoom(room: Rooms.Room, value: Option[Boolean]): Unit = {

    val newState: Boolean = value.getOrElse(
      this.getRoomStates(None) match {
        case Left(error) =>
          logger.error(s"Can not get state from $room ($error)")
          false
        case Right(data) => !data(room).on
      }
    )

    //TODO: Toggle all lights synchronized
    room.foreach(light => this.toggleLightBulb(light, newState = Some(newState)))
  }

  /**
    * Set room brightness
    * @param room  Room
    * @param value (0 to 1)
    */
  override def setRoomBrightness(room: Rooms.Room, value: Double): Unit = {

    val newState = if (value == 0) false else true

    //TODO: Toggle all lights synchronized
    room.foreach(light =>
      this.toggleLightBulb(light, newState = Some(newState), newBrightness = Some(value))
    )
  }

  /**
    * Set scene
    * @param scene Scene
    */
  override def setScene(scene: (Rooms.GroupId, String)): Unit = {

    val jsonRequestString = "{\"scene\": \"" + scene._2 + "\"}"

    val ans: Either[String, Option[Nothing]] = Webclient.putRequestToJSON(
      decoder = None,
      rawBody = jsonRequestString,
      url = this.targetUrl + s"groups/${scene._1}/action"
    )

    if (ans.isLeft) logger.error(s"Can not set scene (${ans.left.get})")

  }

  /**
    * Trigger routine
    * @param routine Routine
    */
  override def triggerRoutine(
      routine: (Vector[(Rooms.GroupId, String)], Vector[Rooms.Room])
  ): Unit = {

    //TODO: Toggle all lights synchronized

    val scenes: Vector[Scenes.Scene]    = routine._1
    val turnOfRooms: Vector[Rooms.Room] = routine._2

    scenes.foreach(scene => this.setScene(scene))
    turnOfRooms.foreach(room => this.toggleRoom(room = room, value = Some(false)))
  }

  /**
    * Set long alarm on room
    * @param light Light
    */
  override def setAlarmOnBulb(light: Lights.Light): Unit = {

    val jsonRequestString = "{\"alert\": \"lselect\"}"

    val ans: Either[String, Option[Nothing]] = Webclient.putRequestToJSON(
      decoder = None,
      rawBody = jsonRequestString,
      url = this.targetUrl + s"lights/${light}/state"
    )

    if (ans.isLeft) logger.error(s"Can not set alarm (${ans.left.get})")

  }
}
