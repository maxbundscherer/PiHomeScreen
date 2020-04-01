package de.maxbundscherer.pihomescreen.services.abstracts

abstract class WeatherService {

  /**
   * Get acutal temperature in celsius
   * @return Either Left = Error / Right = Celsius
   */
  def getActualTempInCelsius: Either[String, String]

}
