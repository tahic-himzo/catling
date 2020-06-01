package com.catling.evaluation

import cats.NonEmptyParallel
import cats.effect.IO
import cats.syntax.parallel.catsSyntaxTuple2Parallel
import com.catling.evaluation.latency.{Latency, LatencyEvaluator}
import com.catling.evaluation.statuscode.StatusCodeEvaluator
import com.catling.evaluation.statuscode.model.StatusCodesResult
import com.catling.http.model.TimedResponse

object Evaluators {

  def latency[A]: Evaluator[A, Latency] = new LatencyEvaluator

  def responseCodes[A]: Evaluator[A, StatusCodesResult] = new StatusCodeEvaluator

  def default[A](implicit nep: NonEmptyParallel[IO]): Evaluator[A, (StatusCodesResult, Latency)] = combine(responseCodes, latency)

  def combine[A, B, C](eval1: Evaluator[A, B], eval2: Evaluator[A, C])(implicit nep: NonEmptyParallel[IO]): Evaluator[A, (B, C)] =
    (in: List[TimedResponse[A]]) => (eval1.apply(in), eval2.apply(in)).parTupled

}

trait Evaluator[A, B] extends (List[TimedResponse[A]] => IO[B])
