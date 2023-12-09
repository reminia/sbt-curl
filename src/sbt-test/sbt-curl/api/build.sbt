val AkkaHttpVersion = "10.5.3"

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    resolvers += "Akka repo".at("https://repo.akka.io/maven"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream" % "2.7.0"
    )
  )
