package de.maxbundscherer.pihomescreen.utils

import org.apache.logging.log4j.scala.Logging

object CSVUtils {

  case class CSVItem(
      bulbId: String,
      isOn: String,
      brightness: String,
      saturation: Option[Int],
      stateX: Option[Float],
      stateY: Option[Float],
      miredColor: Option[Int],
      stateColorMode: Option[String],
      stateReachable: Option[Boolean],
      stateEffect: Option[String],
      stateMode: Option[String]
  )

}

class CSVUtils(filePath: String) extends TimeHelper with Logging {

  import CSVUtils._

  import com.github.tototoshi.csv.CSVWriter
  import com.github.tototoshi.csv.DefaultCSVFormat
  import better.files.{ File => ScalaFile, _ }
  import java.io.{ File => JFile }

  //Init File
  val file: ScalaFile                = ScalaFile.apply(filePath)
  var isFileAlreadyPrepared: Boolean = false;
  if (file.exists) {
    logger.debug(s"Remove file ($filePath)")
    file.delete()
  }

  def writeToCSVFile(data: Vector[CSVItem]): Unit = {

    implicit object MyFormat extends DefaultCSVFormat {
      override val delimiter = ';'
    }

    val writer = CSVWriter.open(file.toJava, append = true)

    val header: List[List[String]] = List(
      List(
        "timestamp",
        "bulbId",
        "isOn",
        "brightness",
        "saturation",
        "stateX",
        "stateY",
        "miredColor",
        "stateColorMode",
        "stateReachable",
        "stateEffect",
        "stateMode"
      )
    )
    val items: List[List[String]] =
      data
        .map(d =>
          List(
            Time.getCurrentTimeForReport,
            d.bulbId,
            d.isOn,
            d.brightness,
            d.saturation match {
              case Some(value) => value.toString
              case None        => "no-data"
            },
            d.stateX match {
              case Some(value) => value.toString
              case None        => "no-data"
            },
            d.stateY match {
              case Some(value) => value.toString
              case None        => "no-data"
            },
            d.miredColor match {
              case Some(value) => value.toString
              case None        => "no-data"
            },
            d.stateColorMode match {
              case Some(value) => value
              case None        => "no-data"
            },
            d.stateReachable match {
              case Some(value) => value.toString
              case None        => "no-data"
            },
            d.stateEffect match {
              case Some(value) => value
              case None        => "no-data"
            },
            d.stateMode match {
              case Some(value) => value
              case None        => "no-data"
            }
          )
        )
        .toList

    val ans = if (!isFileAlreadyPrepared) {
      this.isFileAlreadyPrepared = true
      header ++ items
    } else items

    writer.writeAll(ans)
    writer.close()
  }

}
