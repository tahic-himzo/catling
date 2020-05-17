package main

import cats.effect._
import eval.Status
import fs2.{Pipe, Stream}

object LoadTest {

  def from[T, S, U](
      ds:   Stream[IO, T],
      prep: Pipe[IO, T, S],
      exec: Pipe[IO, S, U],
      eval: Pipe[IO, U, Status]): Stream[IO, Status] =
    ds.through(prep).through(exec).through(eval)
}
