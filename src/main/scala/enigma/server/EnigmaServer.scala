package enigma.server

import java.security.{KeyStore, SecureRandom}

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.stream.ActorMaterializer
import enigma.{EncryptionRequest, EnigmaMachine}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object EnigmaServerImpl extends EnigmaServer with App

trait EnigmaServer extends CorsSupport {
  val host = "0.0.0.0"
  val port = 8080
  implicit val system = ActorSystem("enigma")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  lazy val logger = Logging(system, classOf[EnigmaServer])

  implicit val requestFormat = jsonFormat4(EncryptionRequest)

  val https: HttpsConnectionContext = {
    val password = "password".toCharArray
    val context = SSLContext.getInstance("TLSv1.2")
    val keyStore = KeyStore.getInstance("jks")
    val keyStoreResource = "mykeystore.jks"
    val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    val trustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    keyStore.load(
      getClass.getClassLoader.getResourceAsStream(keyStoreResource),
      password
    )
    keyManagerFactory.init(keyStore, password)
    trustManagerFactory.init(keyStore)
    context.init(
      keyManagerFactory.getKeyManagers,
      trustManagerFactory.getTrustManagers,
      new SecureRandom()
    )
    val sslParams = context.getDefaultSSLParameters
    sslParams.setEndpointIdentificationAlgorithm("HTTPS")
    ConnectionContext.https(
      context,
      sslParameters = Some(sslParams),
      enabledProtocols = Some(List("TLSv1.2", "TLSv1.1", "TLSv1"))
    )
  }

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
                  s"Encrypted input: ${req.text} with RotorSettings: [${req.left},${req.center},${req.right}]. Output: $encrypted"
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

  Http().setDefaultClientHttpsContext(https)
  val bindingFuture =
    Http()
      .bindAndHandle(corsHandler(route), host, port, connectionContext = https)
      .onComplete {
        case Success(_) => println("Server online at https:localhost:8080/")
        case Failure(_) => println("Error")
      }
}
