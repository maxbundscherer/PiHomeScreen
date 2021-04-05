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

      lazy val bridgeApiUrl: String      = c.getString("bridge-api-url")
      lazy val bridgeApiUsername: String = c.getString("bridge-api-username")

    }

    object PhilipsHueReporting {

      lazy private val c = config.getConfig("philipshue-reporting")

      lazy val isEnabled: Boolean     = c.getBoolean("is-enabled")
      lazy val reportFilepath: String = c.getString("report-filepath")

    }

    object HealthCheck {

      lazy private val c = config.getConfig("healthcheck")

      lazy val targetUrl: String = c.getString("target-url")

    }

    object Pexels {

      lazy private val c = config.getConfig("pexels")

      lazy val pexelsToken: String  = c.getString("pexels-token")
      lazy val localWorkDir: String = c.getString("local-work-dir")

    }

    object Joke {

      lazy private val c = config.getConfig("joke")

      lazy val firstName: String = c.getString("first-name")
      lazy val lastName: String  = c.getString("last-name")

    }

    object IssLocation {

      lazy private val c = config.getConfig("iss-location")

      lazy val myHomeLatitude: Double     = c.getDouble("my-home-latitude")
      lazy val myHomeLongitude: Double    = c.getDouble("my-home-longitude")
      lazy val kilometerThreshold: Double = c.getDouble("kilometer-threshold")
      lazy val isEnabled: Boolean         = c.getBoolean("is-enabled")

    }

    object NightMode {

      lazy private val c = config.getConfig("night-mode")

      lazy val isEnabled: Boolean = c.getBoolean("is-enabled")
      lazy val startHour: Int     = c.getInt("start-hour")
      lazy val stopHour: Int      = c.getInt("stop-hour")
      lazy val inactiveMin: Int   = c.getInt("inactive-min")

    }

  }

}
