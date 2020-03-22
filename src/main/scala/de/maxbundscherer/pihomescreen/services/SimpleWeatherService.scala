package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.WeatherService
import de.maxbundscherer.pihomescreen.utils.Configuration

class SimpleWeatherService extends WeatherService with Configuration {

  import scala.concurrent.duration._
  import com.snowplowanalytics.weather.providers.openweather.CreateOWM
  import cats.{Eval, Id}
  import cats.effect.IO

  val client = CreateOWM[IO].create("api.openweathermap.org", Config.OpenWeatherMap.apiKey, timeout = 1.seconds, ssl = true)

  /**
   * Get actual temperature in celsius

   * @return Celsius
   */
  override def getActualTempInCelsius: String = client.currentById(Config.OpenWeatherMap.cityId).toString()

}
