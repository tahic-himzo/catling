package com.catling.library.execution.constant

import cats.effect.{ContextShift, IO, Timer}
import com.catling.library.execution.Executor
import com.catling.library.http.HttpClient
import com.catling.library.http.model.{Request, TimedResponse}
import fs2.Stream
import io.circe.Encoder

import scala.concurrent.duration.DurationDouble

class ConstantRpsExecutor[T: Encoder](httpClient: HttpClient, val rps: Int)(implicit t: Timer[IO], ec: ContextShift[IO])
    extends Executor[T, String] {
  override def apply(in: Stream[IO, Request[T]]): Stream[IO, TimedResponse[String]] =
    in.chunkN(rps)
      .metered(1.second)
      .flatMap(c => Stream.chunk[IO, Request[T]](c).parEvalMap(Int.MaxValue)(r => httpClient.post(r)))
}
