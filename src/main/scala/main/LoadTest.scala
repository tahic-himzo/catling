package main

import cats.effect._
import eval.Evaluator
import exec.Executor
import fs2.{Pipe, Stream}
import http.Request

object LoadTest {

  def from[T, S, U, X, Y](
      ds:    Stream[IO, T],
      prep:  Pipe[IO, T, Request[S]],
      exec:  Executor[S, String],
      eval1: Evaluator[X],
      eval2: Evaluator[Y]): Stream[IO, (X, Y)] =
    ds.through(prep).through(exec).through(in => in.through(eval1).zipAll(in.through(eval2))(eval1.empty, eval2.empty))
}
