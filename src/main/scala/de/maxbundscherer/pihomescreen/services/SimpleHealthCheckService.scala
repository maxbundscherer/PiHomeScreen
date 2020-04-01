package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.HealthCheckService
import de.maxbundscherer.pihomescreen.utils.Configuration

class SimpleHealthCheckService() extends HealthCheckService with Configuration {

  import scala.util.{Try, Success, Failure}
  import scala.concurrent.duration._

  import sttp.client._

  private val sort: Option[String] = None
  private val query = "http language:scala"

  private implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

  /**
   * Do health check
   * @return Either Left = Error Message / Right = All okay
   */
  override def doHealthCheck(): Either[String, Unit] = {

    val url = Config.HealthCheck.targetUrl

    Try {
      basicRequest
        .get(uri"$url")
        .readTimeout(1 seconds)
        .send()
    } match {

      case Failure(failure) => Left(s"Netzwerk-Fehler: ${failure.getLocalizedMessage}")
      case Success(_)       => Right()

    }

  }

}
