package de.maxbundscherer.pihomescreen.services.abstracts

import scala.concurrent.Future

abstract class VideoService {

  import scala.util.Try

  protected val targetUrl: String =
    s"https://api.pexels.com/videos/popular?per_page=100&min_width=1024&min_height=600&min_duration=$MIN_DURATION_S"

  protected val MIN_DURATION_S: Int = 15 * 60

  case class RandomVideo(url: String, id: Long)

  protected def getRandomVideo: Try[RandomVideo]

  protected def generateFilePath(videoId: Long): String

  protected def isAlreadyDownloaded(filePath: String): Boolean

  def downloadRandomVideoAndConvert(): Future[Unit]

  def getLocalRandomVideo: Try[String]

}
