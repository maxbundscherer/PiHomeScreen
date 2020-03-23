package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.WeatherService
import de.maxbundscherer.pihomescreen.utils.{Configuration, Logger}

class SimpleWeatherService extends WeatherService with Configuration {

  import scala.concurrent.duration._
  import com.snowplowanalytics.weather.providers.openweather.CreateOWM
  import cats.{Eval, Id}
  import cats.effect.IO

  private val logger: Logger                    = new Logger(getClass.getSimpleName)

  private val client = CreateOWM[IO].create("api.openweathermap.org", Config.OpenWeatherMap.apiKey, timeout = 1.seconds, ssl = true)

  /**
   * Get actual temperature in celsius
   * @return Celsius
   */
  override def getActualTempInCelsius: String = {

    val ans = client.currentById(Config.OpenWeatherMap.cityId)

    ans.unsafeRunSync() match {

      case Left(left) =>

        logger.error(left.getLocalizedMessage)
        "?"

      case Right(right) =>

        (right.main.temp.bigDecimal.floatValue() - 273.15).toInt.toString

    }

  }

}
