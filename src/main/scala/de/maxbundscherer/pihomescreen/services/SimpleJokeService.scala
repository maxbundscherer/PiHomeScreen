package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.JokeService
import de.maxbundscherer.pihomescreen.utils.{Configuration, JSONWebclient}

class SimpleJokeService() extends JokeService with JSONWebclient with Configuration {

  import io.circe.Decoder
  import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  /**
   * Get joke
   * @return Either Left = Error Message / Right = value
   */
  override def getFirstJoke(): Either[String, String] = {

    case class Value(joke: String)
    case class Wrapper(value: Value)

    Webclient.getRequestToJson(
      decoder = Decoder[Wrapper],
      url = s"http://api.icndb.com/jokes/random?firstName=${Config.Joke.firstName}&lastName=${Config.Joke.lastName}"
    ) match {

      case Left(error) => Left(error)
      case Right(data) => Right(data.value.joke)

    }

  }

  /**
   * Get joke
   * @return Either Left = Error Message / Right = value
   */
  override def getSecondJoke(): Either[String, String] = {

    case class Wrapper(message: String)

    Webclient.getRequestToJson(
      decoder = Decoder[Wrapper],
      url = "https://api.whatdoestrumpthink.com/api/v1/quotes/random"
    ) match {

      case Left(error) => Left(error)
      case Right(data) => Right("Trump Thinks: " + data.message)

    }

  }

}
