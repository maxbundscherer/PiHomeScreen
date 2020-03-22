package de.maxbundscherer.pihomescreen.services.abstracts

abstract class LightService {

  /**
   * Get light bulbs states
   * @return Map (lightId, value)
   */
  def getLightBulbStates: Map[Int, Boolean]

  /**
   * Get room brightness
   * @return Map (roomId, value)
   */
  def getRoomBrightness: Map[Int, Double]

  /**
   * Toggle state from light bulb
   * @param lightId Id from light
   * @param value Some = value / None = toggle
   */
  def toggleLightBulb(lightId: Int, value: Option[Boolean] = None)

  /**
   * Toggle room
   * @param roomId Id from room
   * @param value Some = value / None = toggle
   */
  def toggleRoom(roomId: Int, value: Option[Boolean] = None)

  /**
   * Set room brightness
   * @param roomId Id from room
   * @param value (0 to 1)
   */
  def setRoomBrightness(roomId: Int, value: Double)

}