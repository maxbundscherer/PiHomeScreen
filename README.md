# Pi Home Screen
Raspberry Pi Home Screen

Author: Maximilian Bundscherer

## Overview

- Control your [philips hue](https://www2.meethue.com/) light bulbs
- Show clock (time and date)
- Show weather info (data from [openWeatherMap](https://openweathermap.org/))

## Requirements

- Raspberry Pi with touchscreen (1024x600 recommend)
- [Liberica JDK 13.x.x for ARM](https://bell-sw.com/pages/java-13.0.1/) - don't works with Oracle jdk (no javaFX included)

## Let's get started (local run for development)

- Edit config (see section below)
- Run with ``sbt run``

## Let's get started (run on target)

- Edit config (see section below)
- Generate jar file with ``sbt generate-jar``
- Copy jar-file from ``./target/scala-2.13/PiHomeScreen-assembly-X.Y.jar`` to raspberry
- Run on target with ``java -jar PiHomeScreen-assembly-X.Y.jar`` (use jdk from above)

# Config

- Edit config file under ``./src/main/resources/application.conf``
- Add [philips hue](https://www2.meethue.com/) bridge secrets and your [openWeatherMap](https://openweathermap.org/) config.

```
pihomescreen {

  openweathermap {

    api-key = ""
    city-id = 0

  }

}
```