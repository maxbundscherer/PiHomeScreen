package de.maxbundscherer.pihomescreen.img

import de.maxbundscherer.pihomescreen.utils.{ Configuration, JSONWebclient }
import org.apache.logging.log4j.scala.Logging

trait RandomImageDownloader extends Configuration with JSONWebclient with Logging {

  object RandomImage {

    import java.nio.file.{ Files, Paths }
    import sys.process._
    import scala.util.Try

    import io.circe.Decoder
    import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

    private val targetUrl: String =
      s"https://api.pexels.com/v1/curated?per_page=100"

    case class RandomImage(url: String, id: Long)

    def getRandomImage: Try[RandomImage] =
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
              "Authorization" -> Config.Pexels.pexelsToken
            )
          )
          .right
          .get

        val selection: Vector[JsonModelPhoto] =
          data.photos
            .filter(_.src.isDefined)
            .filter(_.src.get.landscape.isDefined)

        val t: JsonModelPhoto = selection(scala.util.Random.nextInt(selection.size))

        RandomImage(url = t.src.get.landscape.get, id = t.id)
      }

    def isAlreadyDownloaded(filePath: String): Boolean =
      Files.exists(Paths.get(filePath))

    def downloadImage(filePath: String, url: String): Try[Unit] =
      Try {

        val cmd =
          s"curl $url --max-time 2 --output $filePath --silent"

        //logger.debug(s"Run cmd ($cmd)")

        cmd !

      }

  }

}
