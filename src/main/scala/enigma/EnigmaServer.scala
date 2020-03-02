package enigma
import akka.event.Logging
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{StatusCodes}
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object EnigmaServerImpl extends EnigmaServer with App

trait EnigmaServer {
  val host = "localhost"
  val port = 8080
  implicit val system = ActorSystem("enigma")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  lazy val logger = Logging(system, classOf[EnigmaServer])

  implicit val requestFormat = jsonFormat4(EncryptionRequest)

  def encrypt(encryptionRequest: EncryptionRequest): Future[String] = Future {
    EnigmaMachine.Instance.acceptRequest(encryptionRequest)
  }

  val route = path("encrypt") {
    post {
      entity(as[EncryptionRequest]) { req =>
        onComplete(encrypt(req)) {
          _ match {
            case Success(encrypted) =>
              logger.info(
                s"Encrypted input: ${req.text} with RotorSettings: [${req.right},${req.center},${req.left}]"
              )
              complete(StatusCodes.OK, encrypted)
            case Failure(_) =>
              logger.error("Failed to encrypt the input.")
              complete(StatusCodes.InternalServerError, "Failed to encrypt.")
          }
        }
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, host, port).onComplete {
    case Success(_) => println("Server online at http:localhost:8080/")
    case Failure(_) => println("Error")
  }
}
