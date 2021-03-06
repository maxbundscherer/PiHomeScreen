package de.maxbundscherer.pihomescreen.img

import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Background, BackgroundImage}

object ImageHelper {

  /**
   * Helper shuffle background image
   * @param size Number of background images (e.g. 4 means = 0,1,2,3)
   */
  class ImageShuffler(size: Int) {

    private val random = scala.util.Random

    private var randoms: Vector[Int] = Vector()
    private var counter: Int         = 0

    private def mix: Unit = this.randoms = this.random.shuffle( (0 until this.size).toVector )

    def getNext: Int = {

      if(this.counter == 0) {
        //First call / Mix new range
        this.mix
      }

      val returnElement: Int = this.randoms(counter)

      this.counter = this.counter + 1

      if(this.counter == this.size) {
        //Finish - Mix new range
        this.counter = 0
      }

      returnElement
    }

  }

  //TODO: Set up image size auto
  private val imageShuffler = new ImageShuffler(size = 27)

  /**
   * Converts image to Background
   * @return Background
   */
  def getNextBackground(): Background = {

    val number = this.imageShuffler.getNext

    val res = getClass.getResourceAsStream(s"backgrounds/background$number.png")

    if(res == null) throw new RuntimeException("Background not found")

    val img = new Image(res)
    new Background(Array(new BackgroundImage(img, null, null, null, null)))
  }

  /**
   * Get image for lightbulb
   * @param lightType LightType
   * @return ImageView
   */
  def getGetLightBulbImageView(lightType: Int, width: Int, height: Int): ImageView = {

    val res = getClass.getResourceAsStream(s"lightbulbs/$lightType.png")

    if(res == null) throw new RuntimeException("LightType not found")

    new ImageView(new Image(res, width, height, false, false))
  }

}