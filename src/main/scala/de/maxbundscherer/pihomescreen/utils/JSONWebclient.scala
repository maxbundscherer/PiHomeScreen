package de.maxbundscherer.pihomescreen.utils

trait JSONWebclient {

  import org.apache.logging.log4j.scala.Logging

  object Webclient extends TimeHelper with Logging {

    import scala.util.{ Failure, Success, Try }

    import io.circe.Decoder
    import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

    import sttp.client._

    private val sort: Option[String] = None
    private val query                = "http language:scala"

    private implicit val backend: SttpBackend[Identity, Nothing, NothingT] =
      HttpURLConnectionBackend()

    /**
      * Blocks calls (not to much calls to bridge)
      */
    private def blockedWait(): Unit =
      //TODO: Remove this function and implement synchronized toggle and single click ui
      Thread.sleep(100)

    /**
      * Convert response (body) to response
      * @param decoder Decoder (from response)
      * @param rsp Body from Response
      * @tparam ResponseType classOf Response
      * @return Either Left = Error message / Right = response
      */
    private def convertResponse[ResponseType](
        rsp: Either[String, String]
    )(implicit decoder: Decoder[ResponseType]): Either[String, ResponseType] =
      rsp match {

        case Left(error) =>
          Left(s"Network $error")

        case Right(body) =>
          decode[ResponseType](body) match {

            case Left(error) =>
              Left(s"JSON ${error.getLocalizedMessage}")

            case Right(model) => Right(model)

          }

      }

    /**
      * Send get request and convert response to json model
      * @param decoder Decoder (from response)
      * @param url Endpoint (e.g. http://example.org)
      * @param headerParams Map (key -> value)
      * @tparam ResponseType classOf Response
      * @return Either Left = Error message / Right = response
      */
    def getRequestToJson[ResponseType](
        decoder: Decoder[ResponseType],
        url: String,
        headerParams: Map[String, String] = Map.empty
    ): Either[String, ResponseType] = {

      //TODO: Remove this function and implement synchronized toggle and single click ui
      this.blockedWait()

      Try {
        basicRequest
          .get(uri"$url")
          .headers(headerParams)
          .send()
      } match {

        case Failure(exception) =>
          Left(exception.getLocalizedMessage)

        case Success(res) =>
          this.convertResponse[ResponseType](res.body)(decoder)

      }

    }

    var resetDayCache: Int             = Time.internalGetCurrentDay
    var requestCache: Map[String, Any] = Map.empty

    //Cached for one day
    def getCachedRequestToJson[ResponseType](
        decoder: Decoder[ResponseType],
        url: String,
        headerParams: Map[String, String] = Map.empty
    ): Either[String, ResponseType] = {

      //Invalid cache
      if (resetDayCache != Time.internalGetCurrentDay) {
        logger.info("Clean request cache")
        resetDayCache = Time.internalGetCurrentDay
        requestCache = Map.empty
      }

      requestCache.get(url) match {
        case Some(value) =>
          //logger.debug("Return cached")
          value.asInstanceOf[Either[String, ResponseType]]

        case None =>
          val ans: Either[String, ResponseType] = this.getRequestToJson(decoder, url, headerParams)

          requestCache = requestCache + (url -> ans)
          //logger.debug("Return new")
          ans
      }

    }

    /**
      * Send put request and convert response to json model
      * @param decoder Decoder (from response) (Some = response / None = no response)
      * @param url Endpoint (e.g. http://example.org)
      * @param headerParams Map (key -> value)
      * @param rawBody Body
      * @tparam ResponseType classOf Response
      * @return Either Left = Error Message / Right = Option (Some = response (with decoder) / None = no response (no decoder))
      */
    def putRequestToJSON[ResponseType](
        decoder: Option[Decoder[ResponseType]],
        url: String,
        headerParams: Map[String, String] = Map.empty,
        rawBody: String
    ): Either[String, Option[ResponseType]] = {

      //TODO: Remove this function and implement synchronized toggle and single click ui
      this.blockedWait()

      Try {
        basicRequest
          .put(uri"$url")
          .headers(headerParams)
          .body(rawBody)
          .send()
      } match {

        case Failure(exception) =>
          Left(exception.getLocalizedMessage)

        case Success(res) =>
          decoder match {

            case None =>
              if (res.body.isLeft)
                Left("Request failed (not 200)")
              else
                Right(None)

            case Some(sthDecoder) =>
              this.convertResponse[ResponseType](res.body)(sthDecoder) match {
                case Left(left)   => Left(left)
                case Right(right) => Right(Some(right))
              }

          }

      }

    }

  }

}
