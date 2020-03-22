package de.maxbundscherer.pihomescreen.utils

import com.typesafe.config.ConfigFactory

trait Configuration {

  object Config {

    lazy private val config = ConfigFactory.load().getConfig("pihomescreen")

    object OpenWeatherMap {

      lazy private val c = config.getConfig("openweathermap")

      lazy val apiKey: String = c.getString("api-key")
      lazy val cityId: Int    = c.getInt("city-id")

    }

  }

}