package de.maxbundscherer.pihomescreen.services.abstracts

abstract class HealthCheckService {

  /**
   * Do health check
   * @return Either Left = Error Message / Right = All okay
   */
  def doHealthCheck(): Either[String, Nothing]

}