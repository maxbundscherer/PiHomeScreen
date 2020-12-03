package de.maxbundscherer.pihomescreen.utils

object CSVUtils {

  case class CSVItem(bulbId: String, bulbName: String, brightness: String)

}

class CSVUtils {

  import CSVUtils._

  import com.github.tototoshi.csv.CSVWriter
  import com.github.tototoshi.csv.DefaultCSVFormat
  import better.files.{File => ScalaFile, _}
  import java.io.{File => JFile}


  def writeToCSVFile(data: Vector[CSVItem], filePath: String): Unit = {

    val f: ScalaFile = ScalaFile.apply(filePath)

    if(f.exists()) f.delete()

    f.createFile()

    implicit object MyFormat extends DefaultCSVFormat {
      override val delimiter = ';'
    }

    val writer = CSVWriter.open(f.toJava, append = true)

    val header: List[List[String]] = List(List("bulbId", "bulbName", "brightness"))
    val items: List[List[String]] = data.map(d => List(d.bulbId, d.bulbName, d.brightness)).toList

    writer.writeAll(header ++ items)
    writer.close()
  }

}