package exec

import java.util.concurrent.TimeUnit

import cats.data.NonEmptyList
import cats.effect.{Concurrent, ContextShift, IO, Timer}
import cats.syntax.flatMap._
import fs2.{Pipe, Stream}
import http.HttpClient
import io.circe.Encoder
import sttp.client.Response
import sttp.model.Uri

import scala.concurrent.duration.DurationDouble

class ExecutorFactory(httpClient: HttpClient, url: Uri, additionalHeaders: Map[String, String] = Map.empty) {

  def constantRPS[T: Encoder](rps: Int)(implicit t: Timer[IO], ec: ContextShift[IO]): Pipe[IO, T, Response[String]] =
    in =>
      in.chunkN(rps)
        .metered(1.second)
        .flatMap(
          c =>
            Stream
              .chunk[IO, T](c)
              .parEvalMap(Int.MaxValue)(
                r =>
                  IO.delay(println(t.clock.realTime(TimeUnit.SECONDS).unsafeRunSync())) >>
                  httpClient.post(url, r, additionalHeaders)
              )
        )

  def from[T: Encoder, S](pipes: NonEmptyList[ExecutionStep[T, S]])(implicit t: Timer[IO], c: Concurrent[IO]): Pipe[IO, T, S] =
    in => {
      val initStream = in.through(pipes.head.step).interruptAfter(pipes.head.duration)
      val (finalStream, _) = pipes.tail.foldLeft((initStream, pipes.head.duration)) {
        case ((stream, offset), ExecutionStep(pipe, dur)) =>
          val out = stream.merge(in.delayBy(offset).through(pipe).interruptAfter(offset + dur))
          (out, offset + dur)
      }
      finalStream
    }
}
