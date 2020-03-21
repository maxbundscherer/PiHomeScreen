package de.maxbundscherer.pihomescreen.services

class MockHueService extends HueService {

  /**
   * Fake database (hue bridge)
   */
  var fakeStates: Map[Int, Boolean] = Map (
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
   * Get light bulbs states
   *
   * @return
   */
  override def getStates: Map[Int, Boolean] = this.fakeStates

  /**
   * Toggle state from light bulb
   *
   * @param target id
   * @param value  Some = value / None = toggle
   */
  override def toggleState(target: Int, value: Option[Boolean]): Unit = value match {

    case None =>

      this.fakeStates = this.fakeStates + (target -> !this.fakeStates(target))

    case Some(sth) =>

      this.fakeStates = this.fakeStates + (target -> sth)

  }

}