package eval

import cats.effect.{Concurrent, IO, Timer}
import eval.latency.{Latency, LatencyEvaluator}
import eval.statuscode.StatusCodeEvaluator
import eval.statuscode.model.StatusCodesResult
import fs2.Pipe
import http.TimedResponse

import scala.concurrent.duration._

object Evaluators {

  def latency(interval: FiniteDuration)(implicit t: Timer[IO], c: Concurrent[IO]): Pipe[IO, TimedResponse[String], Latency] =
    new LatencyEvaluator(interval).pipe

  def responseCodes(
      interval: FiniteDuration)(implicit t: Timer[IO], c: Concurrent[IO]): Pipe[IO, TimedResponse[String], StatusCodesResult] =
    new StatusCodeEvaluator(interval).pipe

}
