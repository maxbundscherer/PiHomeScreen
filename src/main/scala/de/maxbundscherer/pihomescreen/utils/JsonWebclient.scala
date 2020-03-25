package de.maxbundscherer.pihomescreen.utils

trait JsonWebclient {

  object Webclient {

    import scala.util.{Failure, Success, Try}

    import io.circe.Decoder
    import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

    import sttp.client._

    private val logger: Logger                    = new Logger(getClass.getSimpleName)

    private val sort: Option[String] = None
    private val query = "http language:scala"

    private implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

    /**
     * Blocks calls (not to much calls to bridge)
     */
    private def blockedWait(): Unit = {

      //TODO: Remove this function and implement synchronized toggle and single click ui
      Thread.sleep(100)
    }

    /**
     * Convert response (body) to response
     * @param decoder Decoder (from response)
     * @param rsp Body from Response
     * @tparam ResponseType classOf Response
     * @return Response (Some = ok / None = failure)
     */
    private def convertResponse[ResponseType](rsp: Either[String, String]
                                             )(implicit decoder: Decoder[ResponseType]): Option[ResponseType] = {

      rsp match {

        case Left(error) =>

          logger.error(s"Network $error")
          None

        case Right(body) =>

          decode[ResponseType](body) match {

            case Left(error) =>

              logger.error(s"JSON ${error.getLocalizedMessage}")
              None

            case Right(model) => Some(model)

          }

      }

    }

    /**
     * Send get request and convert response to json model
     * @param decoder Decoder (from response)
     * @param url Endpoint (e.g. http://example.org)
     * @param headerParams Map (key -> value)
     * @tparam ResponseType classOf Response
     * @return Response (Some = ok / None = failure)
     */
    def getRequestToJson[ResponseType]( decoder: Decoder[ResponseType],
                                        url: String,
                                        headerParams: Map[String, String] = Map.empty
                                         ): Option[ResponseType] = {

      //TODO: Remove this function and implement synchronized toggle and single click ui
      this.blockedWait()

      Try {
        basicRequest
          .get(uri"$url")
          .headers(headerParams)
          .send()
      } match {

        case Failure(exception) =>
          logger.error(exception.getLocalizedMessage)
          None

        case Success(res) =>
          this.convertResponse[ResponseType](res.body)(decoder)

      }

    }

    /**
     * Send put request and convert response to json model
     * @param decoder Decoder (from response) (Some = response / None = no response)
     * @param url Endpoint (e.g. http://example.org)
     * @param headerParams Map (key -> value)
     * @param rawBody Body
     * @tparam ResponseType classOf Response
     * @return Response (Some = ok / None = failure)
     */
    def putRequestToJSON[ResponseType](    decoder: Option[Decoder[ResponseType]],
                                            url: String,
                                            headerParams: Map[String, String] = Map.empty,
                                            rawBody: String
                                         ): Option[ResponseType] = {

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

          logger.error(exception.getLocalizedMessage)
          None

        case Success(res) =>

          decoder match {

            case None =>

              if(res.body.isLeft) logger.error("Request failed (not 200)")
              None

            case Some(sthDecoder) =>
              this.convertResponse[ResponseType](res.body)(sthDecoder)

          }


      }

    }

  }

}
