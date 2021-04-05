package de.maxbundscherer.pihomescreen.img

object ImageHelper extends RandomImageDownloader {

  import scala.util.{ Failure, Success }
  import scalafx.scene.image.{ Image, ImageView }
  import scalafx.scene.layout.{ Background, BackgroundImage }
  import sys.process._

  /**
    * Helper shuffle background image
    * @param size Number of background images (e.g. 4 means = 0,1,2,3)
    */
  class ImageShuffler(size: Int) {

    private val random = scala.util.Random

    private var randoms: Vector[Int] = Vector()
    private var counter: Int         = 0

    private def mix: Unit = this.randoms = this.random.shuffle((0 until this.size).toVector)

    def getNext: Int = {

      if (this.counter == 0)
        //First call / Mix new range
        this.mix

      val returnElement: Int = this.randoms(counter)

      this.counter = this.counter + 1

      if (this.counter == this.size)
        //Finish - Mix new range
        this.counter = 0

      returnElement
    }

  }

  //TODO: Set up image size auto
  private val imageShuffler = new ImageShuffler(size = 27)

  var randomImageShown = false

  /**
    * Converts image to Background
    * @return Background
    */
  def getNextBackground(): Background = {

    if (!randomImageShown)
      RandomImage.getRandomImage match {
        case Failure(exception) =>
          logger.warn(s"Error get random image (${exception.getLocalizedMessage})")
        case Success(imageData) =>
          val localFilePath = Config.Pexels.localWorkDir + s"background-${imageData.id}.png"

          if (!RandomImage.isAlreadyDownloaded(localFilePath))
            RandomImage.downloadImageAndConvert(localFilePath, imageData.url) match {
              case Failure(exception) =>
                logger.warn(s"Error download random image (${exception.getLocalizedMessage})")
              case Success(_) =>
                randomImageShown = true
                return new Background(
                  Array(
                    new BackgroundImage(
                      new Image(new java.io.FileInputStream(localFilePath)),
                      null,
                      null,
                      null,
                      null
                    )
                  )
                )
            }
          else {
            randomImageShown = true
            return new Background(
              Array(
                new BackgroundImage(
                  new Image(new java.io.FileInputStream(localFilePath)),
                  null,
                  null,
                  null,
                  null
                )
              )
            )
          }

      }

    val number = this.imageShuffler.getNext

    val res = getClass.getResourceAsStream(s"backgrounds/background$number.png")

    if (res == null) throw new RuntimeException("Background not found")

    val img = new Image(res)

    randomImageShown = false
    new Background(Array(new BackgroundImage(img, null, null, null, null)))
  }

  /**
    * Get image for lightbulb
    * @param lightType LightType
    * @return ImageView
    */
  def getGetLightBulbImageView(lightType: Int, width: Int, height: Int): ImageView = {

    val res = getClass.getResourceAsStream(s"lightbulbs/$lightType.png")

    if (res == null) throw new RuntimeException("LightType not found")

    new ImageView(new Image(res, width, height, false, false))
  }

}
