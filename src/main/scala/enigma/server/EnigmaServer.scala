package enigma.server

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import enigma.{EncryptionRequest, EnigmaMachine}
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object EnigmaServerImpl extends EnigmaServer with App

trait EnigmaServer extends CorsSupport{
  val host = "192.168.86.68"
  val port = 8080
  implicit val system = ActorSystem("enigma")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  lazy val logger = Logging(system, classOf[EnigmaServer])

  implicit val requestFormat = jsonFormat4(EncryptionRequest)

  def encrypt(encryptionRequest: EncryptionRequest): Future[String] = Future {
    EnigmaMachine.Instance.acceptRequest(encryptionRequest)
  }

  val route =
    post {
      path("encrypt") {
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

  val bindingFuture = Http().bindAndHandle(corsHandler(route), host, port).onComplete {
    case Success(_) => println("Server online at http:localhost:8080/")
    case Failure(_) => println("Error")
  }
}
