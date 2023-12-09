package sbtcurl

import sbt._
import sbt.Keys._
import sbtcurl.Curl.AnsiLogger

import scala.io.Source
import scala.sys.process._

object CurlPlugin extends AutoPlugin {
  override def trigger = allRequirements

  object autoImport {
    lazy val curl = inputKey[Unit]("Execute curl command")
    lazy val curlTestScript = settingKey[Option[File]]("curl-test-script")
    lazy val curlTest = taskKey[Unit]("Execute curl test script")
  }

  import autoImport._


  lazy val curlSettings: Seq[Def.Setting[_]] = Seq(
    curl := {
      val input = Def.spaceDelimited().parsed.mkString(" ")
      implicit val log: AnsiLogger = streams.value.log
      log.info(Curl(input))
    },
  )

  lazy val curlTestSettings: Seq[Def.Setting[_]] = Seq(
    curlTestScript := {
      val base = (LocalRootProject / baseDirectory).value
      val projectDir = base / "project"
      val files = List(
        base / "curl-script",
        base / "curl-test",
        base / "curl.test",
        projectDir / "curl-script",
        projectDir / "curl-test",
        projectDir / "curl.test",
      )
      files.find(_.isFile)
    },
    curlTest := {
      val someFile = curlTestScript.value
      implicit val log: AnsiLogger = streams.value.log
      if (someFile.isDefined) {
        Curl(someFile.get).foreach(log.info)
      } else {
        log.colorError("No any curl script file found!")
      }
    }
  )

  override lazy val globalSettings: Seq[Def.Setting[_]] = curlSettings

  override lazy val projectSettings: Seq[Def.Setting[_]] = curlTestSettings

}

object Curl {
  private val ANSI_RESET = "\u001B[0m"

  private val ANSI_GREEN = "\u001B[32m"

  private val ANSI_RED = "\u001B[31m"

  implicit class AnsiLogger(log: Logger) {
    def colorInfo(msg: => String, ansi: String = ANSI_GREEN): Unit =
      log.info(s"$ansi$msg$ANSI_RESET")

    def colorError(msg: => String, ansi: String = ANSI_RED): Unit =
      log.error(s"$ansi$msg$ANSI_RESET")

    def info(msg: String): Unit = log.info(msg)
  }

  def apply(cmd: String)(implicit logger: AnsiLogger): String = {
    val curl = cmd match {
      case s if s.startsWith("curl") => cmd
      case _ => s"curl $cmd"
    }
    logger.colorInfo(curl)
    curl.!!
  }

  def apply(file: File)(implicit logger: AnsiLogger): Seq[String] = {
    val source = Source.fromFile(file)
    try {
      val (list, _) = source
        .getLines()
        .filter(line => line.nonEmpty && !line.startsWith("#"))
        .foldRight[(List[String], List[String])](List(), List()) { case (line, (list1, list2)) =>
          val _line = line.trim.stripSuffix("\\")
          if (_line.startsWith("curl")) {
            val s = (_line :: list2).mkString(" ")
            (s :: list1) -> List()
          } else {
            list1 -> (_line :: list2)
          }
        }
      list.map(Curl.apply)
    } finally {
      source.close()
    }
  }
}
