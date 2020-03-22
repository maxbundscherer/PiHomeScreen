package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.WeatherService

class SimpleWeatherService extends WeatherService {

  /**
   * Get acutal temperature in celsius

   * @return Ceslsius
   */
  override def getActualTempInCelsius: Int = -1

}
