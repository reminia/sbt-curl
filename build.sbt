version := "0.1.0-SNAPSHOT"
organization := "me.yceel"

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-curl"
  )
