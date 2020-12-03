package de.maxbundscherer.pihomescreen.utils

object CSVUtils {

  case class CSVItem(bulbId: String, isOn: String, brightness: String)

}

class CSVUtils extends TimeHelper {

  import CSVUtils._

  import com.github.tototoshi.csv.CSVWriter
  import com.github.tototoshi.csv.DefaultCSVFormat
  import better.files.{ File => ScalaFile, _ }
  import java.io.{ File => JFile }

  def writeToCSVFile(data: Vector[CSVItem], filePath: String): Unit = {

    val f: ScalaFile = ScalaFile.apply(filePath)

    //Append to file
    //if (f.exists()) f.delete()

    f.createFileIfNotExists()

    implicit object MyFormat extends DefaultCSVFormat {
      override val delimiter = ';'
    }

    val writer = CSVWriter.open(f.toJava, append = true)

    val header: List[List[String]] = List(List("timestamp", "bulbId", "isOn", "brightness"))
    val items: List[List[String]] =
      data.map(d => List(Time.getCurrentTimeForReport, d.bulbId, d.isOn, d.brightness)).toList

    writer.writeAll(header ++ items)
    writer.close()
  }

}
