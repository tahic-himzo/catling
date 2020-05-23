package com.catling.loadtest

import cats.effect.IO
import fs2.{Chunk, Pipe}
import com.catling.internal.http.TimedResponse

abstract class Evaluator[T] extends Pipe[IO, Chunk[TimedResponse[String]], T] {
  val empty: T
}
