package sbtcurl

import sbt.*
import sbt.Keys.*

import scala.io.Source
import scala.sys.process.*

object CurlPlugin extends AutoPlugin {
  override def trigger = allRequirements

  object autoImport {
    lazy val curl = inputKey[Unit]("Execute curl command")
    lazy val curlTestScript = settingKey[Option[File]]("curl-test-script")
    lazy val curlTest = taskKey[Unit]("Execute curl test script")
  }

  import autoImport.*

  lazy val curlSettings: Seq[Def.Setting[_]] = Seq(
    curl := {
      val input = Def.spaceDelimited().parsed.mkString(" ")
      implicit val log: AnsiLogger = streams.value.log
      log.either(Curl(input))
    },
  )

  lazy val curlTestSettings: Seq[Def.Setting[_]] = Seq(
    curlTestScript := {
      val base = baseDirectory.value
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
        Curl(someFile.get).foreach(log.either)
      } else {
        log.colorError("No any curl script file found!")
      }
    }
  )

  override lazy val globalSettings: Seq[Def.Setting[_]] = curlSettings

  override lazy val projectSettings: Seq[Def.Setting[_]] = curlTestSettings

}

object Curl {

  def apply(cmd: String)(implicit logger: AnsiLogger): Either[String, String] = {
    val curl = cmd match {
      case s if s.startsWith("curl") => cmd
      case _ => s"curl $cmd"
    }
    logger.colorInfo(curl)
    CommandParser.parse(curl) match {
      case Right(list) => Right(Process(list).!!)
      case Left(msg) => Left(msg)
    }
  }

  def apply(file: File)(implicit logger: AnsiLogger): Seq[Either[String, String]] = {
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
