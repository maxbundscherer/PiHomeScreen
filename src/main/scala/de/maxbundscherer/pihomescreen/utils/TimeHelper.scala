package de.maxbundscherer.pihomescreen.utils

trait TimeHelper {

  object Time {

    def getCurrentTimeForReport: String = {

      import java.util.Date
      import java.text.SimpleDateFormat

      val df   = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
      val date = new Date()

      df.format(date)

    }

  }

}
