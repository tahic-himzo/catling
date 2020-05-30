package com.catling.loadtest

import cats.effect._
import com.catling.internal.http.Request
import fs2.Stream

import scala.concurrent.duration.FiniteDuration

object LoadTest {

  def from[A, B, C, D](
      ds:           Stream[IO, A],
      prep:         DataPreparator[A, Request[B]],
      exec:         Executor[B, C],
      evalInterval: FiniteDuration,
      eval:         Evaluator[C, D])(implicit t: Timer[IO], c: Concurrent[IO]): Stream[IO, D] =
    ds.through(prep).through(exec).groupWithin(Int.MaxValue, evalInterval).evalMap(c => eval(c.toList))
}
