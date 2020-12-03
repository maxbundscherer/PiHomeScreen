package de.maxbundscherer.pihomescreen.utils

trait CSVWriter {

  object CSV {

    case class CSVItem(bulbId: String, bulbName: String, brightness: String)

    def writeToCSVFile(data: Vector[CSVItem]): Unit = ???

  }

}
