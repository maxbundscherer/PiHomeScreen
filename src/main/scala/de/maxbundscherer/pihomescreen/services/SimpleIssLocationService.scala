package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.IssLocationService
import de.maxbundscherer.pihomescreen.utils.{Configuration, JSONWebclient, LocationCalculator}

class SimpleIssLocationService extends IssLocationService with JSONWebclient with Configuration {

  import io.circe.Decoder
  import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  override def isIssOverMyHouse: Either[String, Boolean] = {

    case class IssPositionWrapper(latitude: Float, longitude: Float)
    case class Wrapper(iss_position: IssPositionWrapper)

    Webclient.getRequestToJson(
      decoder = Decoder[Wrapper],
      url = "http://api.open-notify.org/iss-now.json"
    ) match {

      case Left(error) => Left(error)
      case Right(issPosition) =>

        val distanceInKilometer: Double = LocationCalculator.calculateDistanceInKilometer(
          myLocation = LocationCalculator.Location(lat = Config.IssLocation.myHomeLatitude, lon = Config.IssLocation.myHomeLongitude),
          otherLocation = LocationCalculator.Location(lat = issPosition.iss_position.latitude, lon = issPosition.iss_position.longitude)
        )

        val correctedDistanceInKilometer = if(distanceInKilometer > 0) distanceInKilometer else distanceInKilometer * -1

        Right(correctedDistanceInKilometer < Config.IssLocation.kilometerThreshold)
    }

  }

}
