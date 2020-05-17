package exec

import cats.effect.IO
import fs2.Pipe

import scala.concurrent.duration.FiniteDuration

case class ExecutionStep[T, S](step: Pipe[IO, T, S], duration: FiniteDuration)
