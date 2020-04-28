package de.maxbundscherer.pihomescreen.services.abstracts

import de.maxbundscherer.pihomescreen.utils.LightConfiguration

abstract class LightService extends LightConfiguration {

  /**
   * EntityState (for lights and rooms)
   * @param on Boolean
   * @param brightness Double 0 to 1
   */
  case class EntityState(on: Boolean, brightness: Double)

  case class Cache(
                  bulbStates: Map[Lights.Light, EntityState],
                  roomStates: Map[Rooms.Room, EntityState]
                  ) {

    def updateBulb(bulb: Lights.Light, newState: EntityState): Cache = {
      copy( bulbStates = this.bulbStates + (bulb -> newState) )
    }

    def toggleBulb(bulb: Lights.Light): Cache = {

      val oldState = this.bulbStates(bulb)

      val newState: EntityState = oldState.copy(
        on = !oldState.on
      )

      this.updateBulb(bulb, newState)
    }

    def updateRoom(room: Rooms.Room, newState: EntityState): Cache = {
      copy( roomStates = this.roomStates + (room -> newState) )
    }

    def toggleRoom(room: Rooms.Room): Cache = {

      val oldState = this.roomStates(room)

      val newState: EntityState = oldState.copy(
        on = !oldState.on
      )

      this.updateRoom(room, newState)
    }

  }

  var cache: Option[Cache]

  /**
   * Get light bulbs states
   * @param useCache Use local cache instead of online sync
   * @return Either Left = Error Message / Right = Map (Light, EntityState)
   */
  def getLightBulbStates(useCache: Boolean): Either[String, Map[Lights.Light, EntityState]]

  /**
   * Get room states
   * @param useCache Use local cache instead of online sync
   * @return Either Left = Error Message / Right = Map (Room, EntityState)
   */
  def getRoomStates(useCache: Boolean): Either[String, Map[Rooms.Room, EntityState]]

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

}