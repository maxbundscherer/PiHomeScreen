package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.LightService
import de.maxbundscherer.pihomescreen.utils.{Configuration, JsonWebclient, Logger}

class SimpleHueService extends LightService with JsonWebclient with Configuration {

  import io.circe.Decoder
  import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  private val logger: Logger = new Logger(getClass.getSimpleName)

  /**
   * Get light bulbs states
   * @return Map (lightId, value)
   */
  override def getLightBulbStates: Map[Int, Boolean] = {

    case class State(on: Boolean)
    case class HueLight(state: State)

    Webclient.getRequestToJson(
      decoder = Decoder[Map[Int, HueLight]],
      url     = s"${Config.PhilipsHue.bridgeApiUrl}${Config.PhilipsHue.bridgeApiUsername}/" + "lights"
    ) match {

      case None         =>

        logger.error("No answer from webclient")
        Map.empty

      case Some(answer) =>

        answer.map { case (lightId, hueLight) => lightId -> hueLight.state.on }

    }

  }

  /**
   * Get room brightness
   * @return Map (roomId, value 0 to 1)
   */
  override def getRoomBrightness: Map[Int, Double] = {

    //TODO: Implement
    Map(0 -> 1, 1 -> 1, 2 -> 1)
  }

  /**
   * Toggle state from light bulb
   * @param lightId Id from light
   * @param value   Some = value / None = toggle
   */
  override def toggleLightBulb(lightId: Int, value: Option[Boolean]): Unit = {

    //TODO: Implement
  }

  /**
   * Toggle room
   * @param roomId Id from room
   * @param value  Some = value / None = toggle
   */
  override def toggleRoom(roomId: Int, value: Option[Boolean]): Unit = {

    //TODO: Implement
  }

  /**
   * Set room brightness
   * @param roomId Id from room
   * @param value  (0 to 1)
   */
  override def setRoomBrightness(roomId: Int, value: Double): Unit = (

    //TODO: Implement
  )

}
