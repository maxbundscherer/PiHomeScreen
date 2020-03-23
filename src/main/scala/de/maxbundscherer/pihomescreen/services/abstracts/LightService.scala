package de.maxbundscherer.pihomescreen.services.abstracts

import de.maxbundscherer.pihomescreen.utils.LightConfiguration

abstract class LightService extends LightConfiguration {

  /**
   * Get light bulbs states
   * @return Map (Light, value)
   */
  def getLightBulbStates: Map[Lights.Light, Boolean]

  /**
   * Get room brightness
   * @return Map (Room, value 0 to 1)
   */
  def getRoomBrightness: Map[Rooms.Room, Double]

  /**
   * Toggle state from light bulb
   * @param light Light
   * @param value Some = value / None = toggle
   */
  def toggleLightBulb(light: Lights.Light, value: Option[Boolean] = None)

  /**
   * Toggle room
   * @param room Room
   * @param value Some = value / None = toggle
   */
  def toggleRoom(room: Rooms.Room, value: Option[Boolean] = None)

  /**
   * Set room brightness
   * @param room Room
   * @param value (0 to 1)
   */
  def setRoomBrightness(room: Rooms.Room, value: Double)

}