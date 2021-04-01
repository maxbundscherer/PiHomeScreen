package de.maxbundscherer.pihomescreen.services.abstracts

import scala.concurrent.Future

abstract class VideoService {

  val MIN_DURATION_S: Int     = 15 * 60
  val TARGET_FILENAME: String = "backgroundVideo.mp4"

  def downloadNextVideoFile: Future[Unit]

  def rmWorkingFiles(): Unit

  def isVideoReady: Boolean

}
