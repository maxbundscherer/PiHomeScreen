package de.maxbundscherer.pihomescreen.services

abstract class HueService {

  /**
   * Get light bulbs states
   * @return
   */
  def getStates: Map[Int, Boolean]

  /**
   * Toggle state from light bulb
   * @param target id
   * @param value Some = value / None = toggle
   */
  def toggleState(target: Int, value: Option[Boolean] = None)

}