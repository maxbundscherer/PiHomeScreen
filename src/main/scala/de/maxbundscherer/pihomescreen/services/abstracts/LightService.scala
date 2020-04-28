package de.maxbundscherer.pihomescreen.services.abstracts

import de.maxbundscherer.pihomescreen.utils.LightConfiguration

abstract class LightService extends LightConfiguration {

  /**
   * EntityState (for lights and rooms)
   * @param on Boolean
   * @param brightness Double 0 to 1
   */
  case class EntityState(on: Boolean, brightness: Double)

  object Cache {

    def buildCache: Option[Cache] = {

      val newBulbStates = getLightBulbStates(useCache = false)
      val newRoomStates = getRoomStates(useCache = false)

      if(newBulbStates.isRight && newRoomStates.isRight) {

        Some(Cache(
          bulbStates = newBulbStates.right.get,
          roomStates = newRoomStates.right.get
        ))

      } else None

    }

  }

  case class Cache(
                  bulbStates: Map[Lights.Light, EntityState],
                  roomStates: Map[Rooms.Room, EntityState]
                  ) {

    def setBulb(bulb: Lights.Light, newOnState: Boolean): Cache = {

      val oldState: EntityState = this.bulbStates(bulb)
      val newState: EntityState = oldState.copy(on = newOnState)

      copy(bulbStates = this.bulbStates + (bulb -> newState))
    }

    def setRoom(room: Rooms.Room, newOnState: Boolean): Cache = {

      val oldState: EntityState = this.roomStates(room)
      val newState: EntityState = oldState.copy(on = newOnState)

      copy(roomStates = this.roomStates + (room -> newState))
    }

  }

  var cache: Option[Cache] = Cache.buildCache

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