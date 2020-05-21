package http

import java.util.concurrent.TimeUnit

import cats.effect.{IO, Timer}
import io.circe.Encoder
import sttp.client.circe._
import sttp.client.{NothingT, Response, SttpBackend}
import sttp.model.Uri

class HttpClient(implicit sttpBackend: SttpBackend[IO, Nothing, NothingT]) {

  def post[A: Encoder](req: Request[A])(implicit t: Timer[IO]): IO[TimedResponse[String]] = {
    val request = sttp.client.quickRequest.headers(req.headers).post(req.url).body(req.payload)
    for {
      start <- t.clock.monotonic(TimeUnit.MILLISECONDS)
      res   <- request.send()
      end   <- t.clock.monotonic(TimeUnit.MILLISECONDS)
    } yield TimedResponse(res, end - start)
  }
}

final case class Request[T: Encoder](url:   Uri, payload:         T, headers: Map[String, String] = Map.empty)
final case class TimedResponse[T](response: Response[T], durInMs: Long)
