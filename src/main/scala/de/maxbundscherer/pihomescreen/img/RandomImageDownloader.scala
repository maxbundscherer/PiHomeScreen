package de.maxbundscherer.pihomescreen.img

import de.maxbundscherer.pihomescreen.utils.{ Configuration, JSONWebclient }
import org.apache.logging.log4j.scala.Logging

import scala.util.{ Failure, Success }

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

    def downloadImageAndConvert(filePath: String, url: String): Try[Unit] =
      Try {
        val agent =
          "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.79 Safari/537.36"

        val modFilepath = filePath.replace(".png", ".jpeg")

        Webclient.downloadFileGetRequest(modFilepath, url, Map("User-Agent" -> agent)) match {
          case Failure(exception) =>
            logger.error("Can not download pic " + exception.getLocalizedMessage)

          case Success(_) =>
            s"convert $modFilepath $filePath" !!

            s"rm $modFilepath" !!

        }

      }

  }

}
