package de.maxbundscherer.pihomescreen.utils

object CSVUtils {

  case class CSVItem(bulbId: String, isOn: String, brightness: String)

}

class CSVUtils(filePath: String) extends TimeHelper {

  import CSVUtils._

  import com.github.tototoshi.csv.CSVWriter
  import com.github.tototoshi.csv.DefaultCSVFormat
  import better.files.{ File => ScalaFile, _ }
  import java.io.{ File => JFile }

  //Init File
  val file: ScalaFile = ScalaFile.apply(filePath)
  if (file.exists()) file.delete()
  file.createFile()

  def writeToCSVFile(data: Vector[CSVItem]): Unit = {

    implicit object MyFormat extends DefaultCSVFormat {
      override val delimiter = ';'
    }

    val writer = CSVWriter.open(this.file.toJava, append = true)

    val header: List[List[String]] = List(List("timestamp", "bulbId", "isOn", "brightness"))
    val items: List[List[String]] =
      data.map(d => List(Time.getCurrentTimeForReport, d.bulbId, d.isOn, d.brightness)).toList

    writer.writeAll(header ++ items)
    writer.close()
  }

}
