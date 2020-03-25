package de.maxbundscherer.pihomescreen.services.abstracts

import de.maxbundscherer.pihomescreen.utils.LightConfiguration

abstract class LightService extends LightConfiguration {

  //TODO: Improve error handling with webclient with Either

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
   * @param actualBulbStates Some = Cached light states / None = Calls getLightBulbStates
   * @return Map (Room, EntityState)
   */
  def getRoomStates(actualBulbStates: Option[Map[Lights.Light, EntityState]]): Map[Rooms.Room, EntityState]

  /**
   * Toggle state from light bulb
   * @param light Light
   * @param newState Some = Set light bulb to value / None = toggle light bulb
   * @param newBrightness 0 to 1 Some = Set light bulb to value / None = Dont change it
   */
  def toggleLightBulb(light: Lights.Light, newState: Option[Boolean] = None, newBrightness: Option[Double] = None)

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