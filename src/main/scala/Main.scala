import com.comcast.ip4s.{ipv4, port}
import zio._
import zio.interop.catz._
import fs2.Stream
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.websocket.WebSocketFrame
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.Logger
import org.http4s.implicits._

object Main extends ZIOAppDefault {

  private type F[A] = ZIO[Scope, Throwable, A]
  private object dsl extends Http4sDsl[F]; import dsl._

  // ---------------------------------------------------------------------
  // ① Builder を引数で受け取る
  private def routes(hub: Hub[String])(wsb: WebSocketBuilder2[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "chat" =>
        for {
          dq  <- hub.subscribe
          in   = (stream: Stream[F, WebSocketFrame]) =>
            stream.collect { case WebSocketFrame.Text(t, _) => t }
              .evalMap(msg => hub.publish(msg).unit)
          out  = Stream.repeatEval(dq.take).map(WebSocketFrame.Text(_))
          resp <- wsb.build(out, in)          // ここでレスポンスを組み立てる
        } yield resp
    }
  override def run: ZIO[ZIOAppArgs & Scope, Throwable, Unit] =
    ZIO.scoped {                              // ★ Scope を開く
      for {
        hub <- Hub.unbounded[String]
        _   <- EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          // ② Builder を渡せるエントリーポイント
          .withHttpWebSocketApp { wsb =>
            val app = Router("/" -> routes(hub)(wsb)).orNotFound
            Logger.httpApp(true, true)(app)
          }
          .build
          .useForever                  // Resource が Scope を要求
      } yield ()
    }
}
