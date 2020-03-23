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
   * @return Map (Light, value)
   */
  override def getLightBulbStates: Map[Lights.Light, Boolean] = {

    case class State(on: Boolean)
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
          light -> answer(light).state.on
        ).toMap

    }

  }

  /**
   * Get room brightness
   * @return Map (Room, value 0 to 1)
   */
  override def getRoomBrightness: Map[Rooms.Room, Double] = {

    //TODO: Implement
    Rooms.ALL_ROOMS.map(room =>
      room -> 1.0
    ).toMap
  }

  /**
   * Toggle state from light bulb
   * @param light Light
   * @param value Some = value / None = toggle
   */
  override def toggleLightBulb(light: Lights.Light, value: Option[Boolean]): Unit = {

    val newState: String = value match {

      case Some(sth) => if(sth) "true" else "false"

      case None =>

        val actualStates: Map[Lights.Light, Boolean] = this.getLightBulbStates
        if(actualStates(light)) "false" else "true"

    }

    val jsonRequestString: String = "{\"on\":" + newState + "}"

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

  //TODO: Implement
}

  /**
   * Set room brightness
   * @param room  Room
   * @param value (0 to 1)
   */
  override def setRoomBrightness(room: Rooms.Room, value: Double): Unit = {

    //TODO: Implement
  }

}
