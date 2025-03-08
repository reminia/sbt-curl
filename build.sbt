version := "0.1.2"
organization := "me.yceel"

val publishSettings = Seq(
  publishTo := Some(Resolver.url("Github Package Registry", url("https://maven.pkg.github.com/reminia/sbt-curl"))(Resolver.ivyStylePatterns)),
  publishMavenStyle := false,
  credentials += Credentials(
    "GitHub Package Registry",
    "maven.pkg.github.com",
    System.getenv("GITHUB_REPOSITORY_OWNER"),
    System.getenv("GITHUB_TOKEN")
  ),
  Test / packageDoc / publishArtifact := false,
  Test / packageSrc / publishArtifact := false,
  Test / packageBin / publishArtifact := false,
  Compile / packageDoc / publishArtifact := false
)

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-curl",
    description := "sbt plugin for running curl commands",
    sbtPlugin := true,
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    scriptedBufferLog := false,
    Test / test := {
      scripted.toTask("").value
    }
  )
  .settings(publishSettings *)
