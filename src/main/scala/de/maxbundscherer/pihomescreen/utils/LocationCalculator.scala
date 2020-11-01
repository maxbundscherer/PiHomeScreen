package de.maxbundscherer.pihomescreen.utils

object LocationCalculator {

  case class Location(lat: Double, lon: Double)

  def calculateDistanceInKilometer(myLocation: Location, otherLocation: Location): Double = {

    val latDis = Math.toRadians(myLocation.lat - otherLocation.lat)
    val lngDis = Math.toRadians(myLocation.lon - otherLocation.lon)

    val sinLat = Math.sin(latDis / 2)
    val sinLng = Math.sin(lngDis / 2)

    val a = sinLat * sinLat + (Math.cos(Math.toRadians(myLocation.lat)) * Math.cos(Math.toRadians(otherLocation.lat)) * sinLng * sinLng)

    2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

  }

}