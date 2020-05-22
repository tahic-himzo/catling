package exec

import cats.effect.{ContextShift, IO, Timer}
import fs2.Stream
import http.{HttpClient, Request, TimedResponse}
import io.circe.Encoder

import scala.concurrent.duration.DurationDouble

class ConstantRpsExecutor[T: Encoder](httpClient: HttpClient, val rps: Int)(implicit t: Timer[IO], ec: ContextShift[IO])
    extends Executor[T, String] {
  override def apply(in: Stream[IO, Request[T]]): Stream[IO, TimedResponse[String]] =
    in.chunkN(rps)
      .metered(1.second)
      .flatMap(c => Stream.chunk[IO, Request[T]](c).parEvalMap(Int.MaxValue)(r => httpClient.post(r)))
}
