package de.maxbundscherer.pihomescreen.services.abstracts

abstract class JokeService {

  /**
   * Get joke
   * @return Either Left = Error Message / Right = value
   */
  def getFirstJoke(): Either[String, String]

  /**
   * Get joke
   * @return Either Left = Error Message / Right = value
   */
  def getSecondJoke(): Either[String, String]

}
