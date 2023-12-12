package sbtcurl

import sbt.Logger
import sbtcurl.AnsiLogger.*

import scala.language.implicitConversions

class AnsiLogger(log: Logger) {
  def colorInfo(msg: => String, ansi: String = ANSI_GREEN): Unit =
    log.info(s"$ansi$msg$ANSI_RESET")

  def colorError(msg: => String, ansi: String = ANSI_RED): Unit =
    log.error(s"$ansi$msg$ANSI_RESET")

  def info(msg: String): Unit = log.info(msg)

  def either(either: Either[String, String]): Unit = {
    either match {
      case Left(sth) => colorError(sth)
      case Right(sth) => info(sth)
    }
  }
}

object AnsiLogger {
  private val ANSI_RESET = "\u001B[0m"
  private val ANSI_GREEN = "\u001B[32m"
  private val ANSI_RED = "\u001B[31m"

  implicit def toAnsiLogger(logger: Logger): AnsiLogger = new AnsiLogger(logger)
}