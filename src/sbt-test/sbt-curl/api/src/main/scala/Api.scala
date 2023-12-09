import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives

import java.util.UUID

object Api extends Directives {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("api")

    val router = concat(
      path("ping") {
        get {
          complete(200, "I'm up!")
        }
      },
      path("uuid") {
        post {
          val uuid = UUID.randomUUID()
          complete(200, uuid.toString)
        }
      }
    )

    val port = sys.props.getOrElse("http.port", "8080").toInt
    Http().newServerAt("0.0.0.0", port).bind(router)
  }
}