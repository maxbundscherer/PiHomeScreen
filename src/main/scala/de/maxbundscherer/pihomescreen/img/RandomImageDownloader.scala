package de.maxbundscherer.pihomescreen.img

import de.maxbundscherer.pihomescreen.utils.{ Configuration, JSONWebclient }
import org.apache.logging.log4j.scala.Logging

import scala.util.{ Failure, Success, Try }

trait RandomImageDownloader extends Configuration with JSONWebclient with Logging {

  import io.circe.Decoder
  import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  private val targetUrl: String =
    s"https://api.pexels.com/v1/curated?per_page=100"

  def getRandomImage: Try[String] =
    Try {

      case class JsonModelPhotoSrc(
          landscape: Option[String]
      )

      case class JsonModelPhoto(
          id: Long,
          src: Option[JsonModelPhotoSrc],
          width: Int,
          height: Int
      )

      case class JsonModel(photos: Vector[JsonModelPhoto])

      val data = Webclient
        .getCachedRequestToJson(
          decoder = Decoder[JsonModel],
          url = this.targetUrl,
          headerParams = Map(
            "Authorization" -> Config.BackgroundVideoFilePaths.pexelsToken
          )
        )
        .right
        .get

      val selection: Vector[String] =
        data.photos
          .filter(_.src.isDefined)
          .map(_.src.get)
          .flatMap(_.landscape)

      val sUrl: String = selection(scala.util.Random.nextInt(selection.size))

      sUrl
    }

}
