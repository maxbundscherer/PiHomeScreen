package de.maxbundscherer.pihomescreen.utils

class Logger(name: String) {

  import java.util.Date
  import java.text.SimpleDateFormat
  import java.util.Calendar

  private def getCalendar = Calendar.getInstance()
  private def getTime: Date = this.getCalendar.getTime

  private val timeFormat = new SimpleDateFormat("dd-M-yyyy HH:mm:ss")

  private def echo(prefix: String, msg: String): Unit = println(s"[$prefix - ${this.timeFormat.format(getTime)}]\t" + this.name + ":\t'" + msg + "'")

  def error(msg: String): Unit = this.echo("Error", msg)
  def warn(msg: String): Unit = this.echo("Warn", msg)
  def info(msg: String): Unit = this.echo("Info", msg)
  def debug(msg: String): Unit = this.echo("Debug", msg)

}