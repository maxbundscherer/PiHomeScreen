package de.maxbundscherer.pihomescreen.services

import de.maxbundscherer.pihomescreen.services.abstracts.VideoService
import de.maxbundscherer.pihomescreen.utils.{ Configuration, JSONWebclient }
import org.apache.logging.log4j.scala.Logging

import java.io.File
import java.net.URL

class SimpleVideoService extends VideoService with JSONWebclient with Configuration with Logging {

  import scala.concurrent.ExecutionContext.Implicits.global

  import scala.util.{ Failure, Success, Try }
  import scala.concurrent.Future
  import java.nio.file.{ Files, Paths }
  import sys.process._

  import io.circe.Decoder
  import io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  protected def getRandomVideo: Try[RandomVideo] =
    Try {

      case class JsonVideoFile(
          id: Long,
          quality: String,
          file_type: String,
          width: Option[Int],
          height: Option[Int],
          link: String
      )

      case class JsonModelVideo(video_files: Vector[JsonVideoFile], duration: Int)

      case class JsonModel(videos: Vector[JsonModelVideo])

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

      val selection: Vector[JsonVideoFile] = data.videos
        .filter(_.duration >= MIN_DURATION_S)
        .flatMap(_.video_files)
        .filter(_.quality.equals("sd"))

      val t: JsonVideoFile = selection(scala.util.Random.nextInt((selection.size)))

      RandomVideo(url = t.link, id = t.id)
    }

  protected def generateFilePath(videoId: Long): String =
    Config.Pexels.localWorkDir + s"background-$videoId.mp4"

  protected def isAlreadyDownloaded(filePath: String): Boolean =
    Files.exists(Paths.get(filePath))

  var isProcNow                        = false
  var lastProcFilename: Option[String] = None

  def downloadRandomVideoAndConvert(): Future[Unit] =
    Future {

      if (!isProcNow) {

        isProcNow = true

        this.getRandomVideo match {
          case Failure(exception) =>
            isProcNow = false
            lastProcFilename = None
            logger.warn(s"Failed to get random video (${exception.getLocalizedMessage})")
          case Success(randomVideo) =>
            val filePath = generateFilePath(randomVideo.id)

            if (!this.isAlreadyDownloaded(filePath)) {

              logger.debug("Start download random video")

              val downloadFilePath = "/tmp/downloadVideo.mp4"

              Try {

                new URL(randomVideo.url) #> new File(downloadFilePath) !!

              } match {
                case Failure(exception) =>
                  isProcNow = false
                  lastProcFilename = None
                  logger.warn(s"Download video error (${exception.getLocalizedMessage})")
                case Success(_) =>
                  logger.debug("Stop download video / Start conv now")

                  Try {
                    //NON OMX on mac os!
                    s"ffmpeg -i $downloadFilePath -loglevel error -vf fps=24,scale=1024:600 -c:v h264_omx $filePath" !!

                  } match {
                    case Failure(exception) =>
                      isProcNow = false
                      lastProcFilename = None
                      logger.warn(s"Unknown error (${exception.getLocalizedMessage})")
                    case Success(_) =>
                      isProcNow = false
                      lastProcFilename = Some(filePath)
                      logger.debug("Stop converting video")
                  }

              }

            } else {
              isProcNow = false
              lastProcFilename = None
              logger.debug("Skip download video (already downloaded)")
            }

        }

      } else
        logger.debug("Skip proc videos (is already processing)")

    }

  private def getListOfFiles(dir: String): Vector[java.io.File] = {
    // Thank you https://alvinalexander.com/scala/how-to-list-files-in-directory-filter-names-scala/
    val d = new java.io.File(dir)
    if (d.exists && d.isDirectory)
      d.listFiles.filter(_.isFile).toVector
    else
      Vector[java.io.File]()
  }

  def getLocalRandomVideo(): Try[String] =
    Try {

      if (lastProcFilename.isDefined) {
        val c = lastProcFilename.get
        lastProcFilename = None
        return Try(c)
      }

      val fileNames = getListOfFiles(Config.Pexels.localWorkDir)
        .map(_.getAbsolutePath)
        .filter(_.contains(".mp4"))
        .filter(_.contains("background"))

      if (fileNames.nonEmpty)
        fileNames(scala.util.Random.nextInt(fileNames.size))
      else throw new Exception("No files")

    }

}
