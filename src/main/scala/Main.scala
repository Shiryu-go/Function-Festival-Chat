import com.comcast.ip4s.Literals.{ipv4, port}
import com.comcast.ip4s.{ipv4, port}
import fs2.*
import zio.*
import zio.interop.catz.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.ember.server.*
import org.http4s.server.Router
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame

object Main extends ZIOAppDefault {

  type MyIO[A] = RIO[Any, A]

  object dsl extends Http4sDsl[MyIO]
  import dsl.*

  val echoPipe: Pipe[MyIO, WebSocketFrame, WebSocketFrame] =
    _.collect {
      case WebSocketFrame.Text(msg, _) => WebSocketFrame.Text(s"Echo: $msg")
    }

  def chatRoutes(wsBuilder: WebSocketBuilder2[MyIO]): HttpRoutes[MyIO] =
    HttpRoutes.of[MyIO] {
      case GET -> Root / "chat" =>
        wsBuilder.build(echoPipe)
    }

  override val run: ZIO[Any, Throwable, ExitCode] = for {
    runtime <- ZIO.runtime[Any]
    _ <- EmberServerBuilder
      .default[MyIO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpWebSocketApp(wsBuilder =>
        Router("/" -> chatRoutes(wsBuilder)).orNotFound
      )
      .build
      .useForever
  } yield ExitCode.success
}
