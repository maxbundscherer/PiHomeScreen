package de.maxbundscherer.pihomescreen.utils

class Logger(name: String) {

  import java.util.Date
  import java.text.SimpleDateFormat
  import java.util.Calendar

  private def getCalendar = Calendar.getInstance()
  private def getTime: Date = this.getCalendar.getTime

  private val timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

  //TODO: Implement async logger
  private def echo(postfix: String, msg: String): Unit = println(s"[${this.timeFormat.format(this.getTime)} - $postfix]\t" + this.name + ":\t'" + msg + "'")

  def error(msg: String): Unit = this.echo("ERROR", msg)
  def warn(msg: String): Unit = this.echo("WARN", msg)
  def info(msg: String): Unit = this.echo("INFO", msg)
  def debug(msg: String): Unit = this.echo("DEBUG", msg)

}