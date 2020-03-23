package de.maxbundscherer.pihomescreen.services.abstracts

import de.maxbundscherer.pihomescreen.utils.LightConfiguration

abstract class LightService extends LightConfiguration {

  /**
   * EntityState (for lights and rooms)
   * @param on Boolean
   * @param brightness Double 0 to 1
   */
  case class EntityState(on: Boolean, brightness: Double)

  /**
   * Get light bulbs states
   * @return Map (Light, EntityState)
   */
  def getLightBulbStates: Map[Lights.Light, EntityState]

  /**
   * Get room brightness
   * @return Map (Room, EntityState)
   */
  def getRoomStates: Map[Rooms.Room, EntityState]

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