package de.maxbundscherer.pihomescreen.utils

object LocationCalculator {

  case class Location(lat: Double, lon: Double)

  def calculateDistanceInKilometer(myLocation: Location, otherLocation: Location): Double = {

    val lat1: Double = myLocation.lat
    val lat2: Double = otherLocation.lat
    val lon1: Double = myLocation.lon
    val lon2: Double = otherLocation.lon

    val R = 6371 // Radius of the earth
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    var distance = R * c * 1000 // convert to meters
    val height = 0
    distance = Math.pow(distance, 2) + Math.pow(height, 2)
    Math.sqrt(distance) / 1000
  }

}