package com.catling.library

import cats.effect.{Concurrent, IO, Timer}
import com.catling.library.evaluation.Evaluator
import com.catling.library.execution.Executor
import com.catling.library.http.model.Request
import fs2.Stream

import scala.concurrent.duration._

object LoadTest {

  def from[A, B, C, D](
      source:             Stream[IO, Request[B]],
      executor:           Executor[B, C],
      evaluator:          Evaluator[C, D],
      evaluationInterval: FiniteDuration = 5.seconds)(implicit t: Timer[IO], c: Concurrent[IO]): Stream[IO, D] =
    source.through(executor).groupWithin(Int.MaxValue, evaluationInterval).evalMap(c => evaluator(c.toList))
}
