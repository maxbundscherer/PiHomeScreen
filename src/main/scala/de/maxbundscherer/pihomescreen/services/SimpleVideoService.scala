package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.VideoService
import de.maxbundscherer.pihomescreen.utils.{ Configuration, JSONWebclient }

import org.apache.logging.log4j.scala.Logging
import scala.concurrent.Future

class SimpleVideoService extends VideoService with JSONWebclient with Configuration with Logging {

  import scala.concurrent.ExecutionContext.Implicits.global

  import scala.util.{ Failure, Success, Try }

  import sys.process._
  import java.net.URL
  import java.io.File

  import io.circe.Decoder
  import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  private val targetUrl: String =
    s"https://api.pexels.com/videos/popular?per_page=100&min_width=1024&min_height=600&min_duration=$MIN_DURATION_S"

  private var isDownloading: Boolean = false

  /**
    * Get actual temperature in celsius
    *
    * @return Either Left = Error / Right = FileName
    */
  def downloadNextVideoFile: Future[Unit] =
    Future {

      if (!isDownloading) {

        isDownloading = true

        case class JsonVideoFile(
            id: Long,
            quality: String,
            file_type: String,
            width: Option[Int],
            height: Option[Int],
            link: String
        )

        case class JsonModelVideo(video_files: Vector[JsonVideoFile])

        case class JsonModel(videos: Vector[JsonModelVideo])

        logger.debug("Start download video")

        Webclient.getRequestToJson(
          decoder = Decoder[JsonModel],
          url = this.targetUrl,
          headerParams = Map(
            "Authorization" -> Config.BackgroundVideoFilePaths.pexelsToken
          )
        ) match {

          case Left(error) =>
            isDownloading = false
            this.rmWorkingFiles()
            logger.error(error)

          case Right(data) =>
            Try {

              val selection = data.videos
                .flatMap(_.video_files)
                .filter(_.quality.equals("sd"))

              val sUrl: String = selection(scala.util.Random.nextInt(selection.size)).link

              new URL(sUrl) #> new File("downloadVideo.mp4") !

              logger.debug("Stop download video / Start conv now")

              //NON OMX on mac os!
              s"ffmpeg -i downloadVideo.mp4 -loglevel error -vf fps=23,scale=1024:600 -c:v h264_omx $TARGET_FILENAME" !

            } match {
              case Failure(exception) =>
                isDownloading = false
                this.rmWorkingFiles()
                logger.error(exception.getLocalizedMessage)

              case Success(_) =>
                isDownloading = false
                logger.debug(s"Fin convert video ($TARGET_FILENAME)")
            }

        }

      } else
        logger.debug("Skip download video (is already downloading)")

    }

  override def rmWorkingFiles(): Unit =
    Try {
      if (!isDownloading)
        s"rm downloadVideo.mp4 $TARGET_FILENAME" !!
    }

  override def isVideoReady: Boolean = {

    if (isDownloading)
      return false

    import java.nio.file.{ Files, Paths }
    import java.nio.file.{ Files, Paths }

    Files.exists(Paths.get(TARGET_FILENAME))
  }
}
