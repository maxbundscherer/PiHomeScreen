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

    object PhilipsHue {

      lazy private val c = config.getConfig("philipshue")

      lazy val bridgeApiUrl: String          = c.getString("bridge-api-url")
      lazy val bridgeApiUsername: String  = c.getString("bridge-api-username")

    }

  }

}