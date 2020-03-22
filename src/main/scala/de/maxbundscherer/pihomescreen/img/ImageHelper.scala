package de.maxbundscherer.pihomescreen.img

import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Background, BackgroundImage}

object ImageHelper {

  /**
   * Converts image to Background
   * @return Background
   */
  def getBackground(): Background = {

    val res = getClass.getResourceAsStream("backgrounds/background.png")

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