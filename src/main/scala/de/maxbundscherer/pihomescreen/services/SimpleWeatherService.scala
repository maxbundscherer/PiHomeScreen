package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.WeatherService
import de.maxbundscherer.pihomescreen.utils.{Configuration, JSONWebclient}

class SimpleWeatherService extends WeatherService with JSONWebclient with Configuration {

  import io.circe.Decoder
  import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  private val targetUrl: String = s"https://api.openweathermap.org/data/2.5/weather?id=${Config.OpenWeatherMap.cityId}&appid=${Config.OpenWeatherMap.apiKey}"

  /**
   * Get actual temperature in celsius
   * @return Either Left = Error / Right = Celsius
   */
  override def getActualTempInCelsius: Either[String, String] = {

    case class Main(temp: Double)
    case class WeatherModel(main: Main)

    Webclient.getRequestToJson(
      decoder = Decoder[WeatherModel],
      url     = this.targetUrl
    ) match {

      case Left(error) => Left(error)

      case Right(data) =>

        Right((data.main.temp - 273.15).toInt.toString)

    }

  }

}
