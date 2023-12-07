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

  import autoImport.*

  lazy val curlSettings: Seq[Def.Setting[_]] = Seq(
    curl := {
      val input = Def.spaceDelimited().parsed.mkString(" ")
      println(Curl(input))
    },
  )

  lazy val curlTestSettings: Seq[Def.Setting[_]] = Seq(
    curlTestScript := {
      val projectDir = (LocalRootProject / baseDirectory).value / "project"
      val files = List(
        projectDir / "curl.script",
        projectDir / "curl-test",
        projectDir / "curl.test",
      )
      files.find(_.isFile)
    },
    curlTest := {
      val someFile = curlTestScript.value
      if (someFile.isDefined) {
        Curl(someFile.get).foreach(println)
      }
    }
  )

}

object Curl {
  def apply(cmd: String): String = {
    cmd.!!
  }

  def apply(file: File): Seq[String] = {
    val source = Source.fromFile(file)
    try {
      val lines = source
        .getLines()
        .filter(_.nonEmpty)
      val (list1, _) = lines.foldRight[(List[String], List[String])](List(), List()) { case (line, (list1, list2)) =>
        if (line.dropWhile(_.isWhitespace).startsWith("curl")) {
          val s = (list1 :: list2).mkString(System.lineSeparator())
          (s :: list1) -> List()
        } else {
          list1 -> (line :: list2)
        }
      }
      list1.map(cmd => Curl(cmd))
    } finally {
      source.close()
    }
  }
}
