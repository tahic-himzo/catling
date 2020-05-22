package eval

import cats.effect.{Concurrent, IO, Timer}
import eval.latency.{Latency, LatencyEvaluator}
import eval.statuscode.StatusCodeEvaluator
import eval.statuscode.model.StatusCodesResult
import fs2.Pipe
import http.TimedResponse

import scala.concurrent.duration._

abstract class Evaluator[T] extends Pipe[IO, TimedResponse[String], T] {
  val empty: T
}

object Evaluator {

  def latency(interval: FiniteDuration)(implicit t: Timer[IO], c: Concurrent[IO]): Evaluator[Latency] =
    new LatencyEvaluator(interval)

  def responseCodes(interval: FiniteDuration)(implicit t: Timer[IO], c: Concurrent[IO]): Evaluator[StatusCodesResult] =
    new StatusCodeEvaluator(interval)

}
