package main

import cats.effect._
import fs2.{Pipe, Stream}

object LoadTest {

  def from[T, S, U, X](ds: Stream[IO, T], prep: Pipe[IO, T, S], exec: Pipe[IO, S, U], eval: Pipe[IO, U, X]): Stream[IO, X] =
    ds.through(prep).through(exec).through(eval)
}
