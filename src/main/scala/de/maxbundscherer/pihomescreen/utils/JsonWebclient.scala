package de.maxbundscherer.pihomescreen.utils

trait JsonWebclient {

  object Webclient {

    import io.circe.Decoder
    import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

    import sttp.client._

    private val logger: Logger                    = new Logger(getClass.getSimpleName)

    private val sort: Option[String] = None
    private val query = "http language:scala"

    private implicit val backend = HttpURLConnectionBackend()

    /**
     * Convert response (body) to response
     * @param decoder Decoder (from response)
     * @param rsp Body from Response
     * @tparam ResponseType classOf Response
     * @return Response (Some = ok / None = failure)
     */
    private def convertResponse[ResponseType](decoder: Decoder[ResponseType],
                                              rsp: Either[String, String]
                                             ): Option[ResponseType] = {

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

      val req = basicRequest
        .get(uri"$url")
        .headers(headerParams)
        .send()

      this.convertResponse[ResponseType](decoder, req.body)
    }

    /**
     * Send post request and convert response to json model
     * @param decoder Decoder (from response)
     * @param url Endpoint (e.g. http://example.org)
     * @param headerParams Map (key -> value)
     * @param body Map (key -> value)
     * @param rawBody If set = use rawBody instead of body
     * @tparam ResponseType classOf Response
     * @return Response (Some = ok / None = failure)
     */
    def postRequestToJSON[ResponseType](    decoder: Decoder[ResponseType],
                                            url: String,
                                            headerParams: Map[String, String] = Map.empty,
                                            body: Map[String, String] = Map.empty,
                                            rawBody: Option[String] = None
                                         ): Option[ResponseType] = {

      val req = if(rawBody.isEmpty) {

        basicRequest
          .post(uri"$url")
          .headers(headerParams)
          .body(body)
          .send()
      }
      else {

        basicRequest
          .post(uri"$url")
          .headers(headerParams)
          .body(rawBody.get)
          .send()
      }

      this.convertResponse[ResponseType](decoder, req.body)
    }

  }

}
