package com.catling.internal.evaluation

import cats.NonEmptyParallel
import cats.effect.IO
import cats.syntax.parallel.catsSyntaxTuple2Parallel
import com.catling.internal.evaluation.latency.{Latency, LatencyEvaluator}
import com.catling.internal.evaluation.statuscode.StatusCodeEvaluator
import com.catling.internal.evaluation.statuscode.model.StatusCodesResult
import com.catling.internal.http.TimedResponse
import com.catling.loadtest.Evaluator

object Evaluators {

  def latency[A]: Evaluator[A, Latency] = new LatencyEvaluator

  def responseCodes[A]: Evaluator[A, StatusCodesResult] = new StatusCodeEvaluator

  def default[A](implicit nep: NonEmptyParallel[IO]): Evaluator[A, (StatusCodesResult, Latency)] = eval2(responseCodes, latency)

  def eval2[A, B, C](eval1: Evaluator[A, B], eval2: Evaluator[A, C])(implicit nep: NonEmptyParallel[IO]): Evaluator[A, (B, C)] =
    (in: List[TimedResponse[A]]) => (eval1.apply(in), eval2.apply(in)).parTupled

}
