package de.maxbundscherer.pihomescreen.utils

class Logger(name: String) {

  private def echo(prefix: String, msg: String): Unit = println(s"[$prefix]\t" + this.name + ":\t'" + msg + "'")

  def error(msg: String): Unit = this.echo("Error", msg)
  def warn(msg: String): Unit = this.echo("Warn", msg)
  def info(msg: String): Unit = this.echo("Info", msg)
  def debug(msg: String): Unit = this.echo("Debug", msg)

}