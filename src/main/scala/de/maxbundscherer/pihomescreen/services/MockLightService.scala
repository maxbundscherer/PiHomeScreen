package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.LightService

class MockLightService extends LightService {

  /**
   * Fake database Map(lightId, value)
   */
  var fakeLightStates: Map[Int, Boolean] = Map (
    7 -> true,
    8 -> false,
    2 -> true,

    5 -> true,
    11 -> false,
    6 -> true,
    4 -> false,
    10 -> true,

    9 -> false,
    1 -> true
  )

  /**
   * Fake database Map(roomId, value)
   */
  var fakeRoomStates: Map[Int, Double] = Map (
    0 -> 0.4,
    1 -> 0.6,
    2 -> 1
  )

  /**
   * Get light bulbs states
   * @return Map (lightId, value)
   */
  override def getLightBulbStates: Map[Int, Boolean] = this.fakeLightStates

  /**
   * Get room brightness
   * @return Map (roomId, value)
   */
  override def getRoomBrightness: Map[Int, Double] = this.fakeRoomStates

  /**
   * Toggle state from light bulb
   * @param lightId Id from light
   * @param value Some = value / None = toggle
   */
  override def toggleLightBulb(lightId: Int, value: Option[Boolean]): Unit = value match {

    case None =>

      this.fakeLightStates = this.fakeLightStates + (lightId -> !this.fakeLightStates(lightId))

    case Some(sth) =>

      this.fakeLightStates = this.fakeLightStates + (lightId -> sth)

  }

  /**
   * Toggle room
   * @param roomId Id from room
   * @param value  Some = value / None = toggle
   */
  override def toggleRoom(roomId: Int, value: Option[Boolean]): Unit = value match {

    case None =>

      val oldValue: Double = this.getRoomBrightness(roomId)
      if(oldValue > 0) this.setRoomBrightness(roomId, 0)
      else this.setRoomBrightness(roomId, 1)

    case Some(sth) =>

      if(sth) this.setRoomBrightness(roomId, 1)
      else this.setRoomBrightness(roomId, 0)

  }

  /**
   * Set room brightness
   * @param roomId Id from room
   * @param value  (0 to 1)
   */
  override def setRoomBrightness(roomId: Int, value: Double): Unit = {

    this.fakeRoomStates = this.fakeRoomStates + (roomId -> value)

    if(value > 0) {

      roomId match {
        case 0 => this.fakeLightStates = this.fakeLightStates ++ Map(7 -> true, 8 -> true, 2 -> true)
        case 1 => this.fakeLightStates = this.fakeLightStates ++ Map(5 -> true, 11 -> true, 6 -> true, 4 -> true, 10 -> true)
        case 2 => this.fakeLightStates = this.fakeLightStates ++ Map(9 -> true, 1 -> true)
      }

    } else {

      roomId match {
        case 0 => this.fakeLightStates = this.fakeLightStates ++ Map(7 -> false, 8 -> false, 2 -> false)
        case 1 => this.fakeLightStates = this.fakeLightStates ++ Map(5 -> false, 11 -> false, 6 -> false, 4 -> false, 10 -> false)
        case 2 => this.fakeLightStates = this.fakeLightStates ++ Map(9 -> false, 1 -> false)
      }

    }

  }

}