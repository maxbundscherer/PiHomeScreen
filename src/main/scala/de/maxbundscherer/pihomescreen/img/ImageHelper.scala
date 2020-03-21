package de.maxbundscherer.pihomescreen.img

import scalafx.scene.image.Image
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

}