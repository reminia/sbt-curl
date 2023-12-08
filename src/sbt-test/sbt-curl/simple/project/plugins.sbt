sys.props.get("plugin.version") match {
  case Some(v) => addSbtPlugin("me.yceel" % "sbt-curl" % v)
  case _ => sys.error("The system property plugin.version for sbt-curl is not set")
}