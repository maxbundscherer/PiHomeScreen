# Pi Home Screen
Raspberry PI Home Screen

Author: Maximilian Bundscherer

## Requirements

- Raspberry Pi with touchscreen (1024x600 recommend)
- [Liberica JDK 13.x.x for ARM](https://bell-sw.com/pages/java-13.0.1/) - don't works with Oracle jdk (no javaFX included)

## Let's get started (local run for development)

- Run with ``sbt run``

## Let's get started (run on target)

- Generate jar file with ``sbt assembly``
- Copy jar-file from ``./target/scala-2.13/PiHomeScreen-assembly-X.Y.jar`` to raspberry
- Run on target with ``java -jar PiHomeScreen-assembly-X.Y.jar`` (use jdk from above)