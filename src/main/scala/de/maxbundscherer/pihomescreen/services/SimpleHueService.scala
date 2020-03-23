package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.LightService
import de.maxbundscherer.pihomescreen.utils.{Configuration, JsonWebclient, Logger}

class SimpleHueService extends LightService with JsonWebclient with Configuration {

  import io.circe.Decoder
  import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  private val logger: Logger = new Logger(getClass.getSimpleName)

  private val targetUrl: String = s"${Config.PhilipsHue.bridgeApiUrl}${Config.PhilipsHue.bridgeApiUsername}/"

  /**
   * Get light bulbs states
   * @return Map (Light, EntityState)
   */
  override def getLightBulbStates: Map[Lights.Light, EntityState] = {

    case class State(on: Boolean, bri: Int)
    case class HueLight(state: State)

    Webclient.getRequestToJson(
      decoder = Decoder[Map[Int, HueLight]],
      url     = this.targetUrl + "lights"
    ) match {

      case None         =>

        logger.error("No answer from webclient")
        Map.empty

      case Some(answer) =>

        Lights.ALL_LIGHTS.map(light =>
          light -> EntityState(
            on = answer(light).state.on,
            brightness = answer(light).state.bri / 255.0 //Norm to 0 to 1
          )
        ).toMap

    }

  }

  /**
   * Get room brightness
   * @param actualBulbStates Some = Cached light states / None = Calls getLightBulbStates
   * @return Map (Room, EntityState)
   */
  override def getRoomStates(actualBulbStates: Option[Map[Lights.Light, EntityState]]): Map[Rooms.Room, EntityState] = {

    val bulbStates: Map[Lights.Light, EntityState] = actualBulbStates.getOrElse(this.getLightBulbStates)

    val roomWithLightBulbStates: Map[Rooms.Room, Vector[EntityState]] = Rooms.ALL_ROOMS.map(room =>

      room -> room.map(light => bulbStates(light))

    ).toMap

    roomWithLightBulbStates.map { case (room, lightBulbStates) =>

      room -> EntityState(
        on          = lightBulbStates.exists(_.on == true), //One bulb on is enough
        brightness  = lightBulbStates.map(_.brightness).max //Take brightness from max bright bulb
      )

    }

  }

  /**
   * Toggle state from light bulb
   * @param light Light
   * @param newState Some = Set light bulb to value / None = toggle light bulb
   * @param newBrightness 0 to 1 Some = Set light bulb to value / None = Dont change it
   */
  def toggleLightBulb(light: Lights.Light, newState: Option[Boolean] = None, newBrightness: Option[Double] = None) = {

    val stateString      = if(newState.getOrElse(!this.getLightBulbStates(light).on)) "true" else "false"
    val brightnessString = if(newBrightness.isDefined) ", \"bri\":" + (newBrightness.get * 255).toInt else ""

    val jsonRequestString = "{\"on\":" + stateString + brightnessString  + "}"

    Webclient.putRequestToJSON(
      decoder = None,
      rawBody = jsonRequestString,
      url     = this.targetUrl + s"lights/$light/state"
    )

  }

  /**
   * Toggle room
   * @param room  Room
   * @param value Some = value / None = toggle
   */
override def toggleRoom(room: Rooms.Room, value: Option[Boolean]): Unit = {

  val newState: Boolean = value match {

    case Some(sth) => sth

    case None =>

      val actualRoomsStates: Map[Rooms.Room, EntityState] = this.getRoomStates(None)
      !actualRoomsStates(room).on

  }

  room.foreach(light => this.toggleLightBulb(light, newState = Some(newState)))
}

  /**
   * Set room brightness
   * @param room  Room
   * @param value (0 to 1)
   */
  override def setRoomBrightness(room: Rooms.Room, value: Double): Unit = {

    val newState = if(value == 0) false else true

    room.foreach(light => this.toggleLightBulb(light, newState = Some(newState), newBrightness = Some(value)))
  }

}
