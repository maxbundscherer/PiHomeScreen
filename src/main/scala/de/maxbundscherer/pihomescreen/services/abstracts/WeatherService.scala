package de.maxbundscherer.pihomescreen.services.abstracts

abstract class WeatherService {

  //TODO: Improve error handling with webclient with Either

  /**
   * Get acutal temperature in celsius
   * @return Ceslsius
   */
  def getActualTempInCelsius: String

}
