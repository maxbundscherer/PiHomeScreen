package de.maxbundscherer.pihomescreen.services.abstracts

abstract class WeatherService {

  /**
   * Get acutal temperature in celsius
   * @return Ceslsius
   */
  def getActualTempInCelsius: String

}
