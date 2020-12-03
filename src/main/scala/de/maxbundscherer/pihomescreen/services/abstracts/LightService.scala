package de.maxbundscherer.pihomescreen.services.abstracts

import de.maxbundscherer.pihomescreen.utils.LightConfiguration

abstract class LightService extends LightConfiguration {

  /**
    * EntityState (for lights and rooms)
    * @param on Boolean
    * @param brightness Double 0 to 1
    */
  case class EntityState(on: Boolean, brightness: Double, saturation: String)

  /**
    * Get light bulbs states
    * @return Either Left = Error Message / Right = Map (Light, EntityState)
    */
  def getLightBulbStates: Either[String, Map[Lights.Light, EntityState]]

  /**
    * Get room brightness
    * @param actualBulbStates Some = Cached light states / None = Calls getLightBulbStates
    * @return Either Left = Error Message / Right = Map (Room, EntityState)
    */
  def getRoomStates(
      actualBulbStates: Option[Map[Lights.Light, EntityState]]
  ): Either[String, Map[Rooms.Room, EntityState]]

  /**
    * Toggle state from light bulb
    * @param light Light
    * @param newState Some = Set light bulb to value / None = toggle light bulb
    * @param newBrightness 0 to 1 Some = Set light bulb to value / None = Dont change it
    */
  //TODO: Remove toggle (use front-end cache)
  def toggleLightBulb(
      light: Lights.Light,
      newState: Option[Boolean] = None,
      newBrightness: Option[Double] = None
  )

  /**
    * Toggle room
    * @param room Room
    * @param value Some = value / None = toggle
    */
  //TODO: Remove toggle (use front-end cache)
  def toggleRoom(room: Rooms.Room, value: Option[Boolean] = None)

  /**
    * Set room brightness
    * @param room Room
    * @param value (0 to 1)
    */
  def setRoomBrightness(room: Rooms.Room, value: Double)

  /**
    * Set scene
    * @param scene Scene
    */
  def setScene(scene: Scenes.Scene)

  /**
    * Trigger routine
    * @param routine Routine
    */
  def triggerRoutine(routine: Routines.Routine)

  /**
    * Set long alarm on room
    * @param light Light
    */
  def setAlarmOnBulb(light: Lights.Light)

}
