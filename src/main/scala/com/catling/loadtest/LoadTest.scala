package com.catling.loadtest

import cats.effect._
import fs2.{Pipe, Stream}
import com.catling.internal.http.Request

import scala.concurrent.duration.FiniteDuration

object LoadTest {

  def from[T, S, U, X, Y](
      ds:           Stream[IO, T],
      prep:         Pipe[IO, T, Request[S]],
      exec:         Executor[S, String],
      evalInterval: FiniteDuration,
      eval:         Evaluator[X])(implicit t: Timer[IO], c: Concurrent[IO]): Stream[IO, X] =
    ds.through(prep).through(exec).groupWithin(Int.MaxValue, evalInterval).through(eval)
}
