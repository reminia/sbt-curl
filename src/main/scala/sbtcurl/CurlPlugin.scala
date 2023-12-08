package sbtcurl

import sbt._
import sbt.Keys._

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
      implicit val log: Logger = streams.value.log
      log.info(Curl(input))
    },
  )

  lazy val curlTestSettings: Seq[Def.Setting[_]] = Seq(
    curlTestScript := {
      val base = (LocalRootProject / baseDirectory).value
      val projectDir = base / "project"
      val files = List(
        base / "curl.script",
        base / "curl-test",
        base / "curl.test",
        projectDir / "curl.script",
        projectDir / "curl-test",
        projectDir / "curl.test",
      )
      files.find(_.isFile)
    },
    curlTest := {
      val someFile = curlTestScript.value
      implicit val log: Logger = streams.value.log
      if (someFile.isDefined) {
        Curl(someFile.get).foreach(println)
      } else {
        log.error("No any curl script file found!")
      }
    }
  )

  override lazy val globalSettings: Seq[Def.Setting[_]] = curlSettings

  override lazy val projectSettings: Seq[Def.Setting[_]] = curlTestSettings

}

object Curl {
  def apply(cmd: String)(implicit logger: Logger): String = {
    var curl = cmd
    if(!cmd.startsWith("curl")) {
      curl = s"curl $cmd"
    }
    logger.info(curl)
    curl.!!
  }

  def apply(file: File)(implicit logger: Logger): Seq[String] = {
    val source = Source.fromFile(file)
    try {
      val (list, _) = source
        .getLines()
        // remove empty and comment line
        .filter(line => line.nonEmpty && !line.startsWith("#"))
        .foldRight[(List[String], List[String])](List(), List()) { case (line, (list1, list2)) =>
          val _line = line.dropWhile(_.isWhitespace)
          if (_line.startsWith("curl")) {
            val s = (_line :: list2).mkString(System.lineSeparator())
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
