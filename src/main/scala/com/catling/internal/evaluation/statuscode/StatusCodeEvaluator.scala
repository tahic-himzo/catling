package com.catling.internal.evaluation.statuscode

import cats.effect.IO
import cats.implicits._
import com.catling.internal.evaluation.statuscode.model.{StatusCodes, StatusCodesResult}
import com.catling.internal.http.TimedResponse
import com.catling.loadtest.Evaluator

class StatusCodeEvaluator[A] extends Evaluator[A, StatusCodesResult] {
  private var totalCounts: Map[Int, Long] = Map.empty

  override def apply(in: List[TimedResponse[A]]): IO[StatusCodesResult] = {

    val lastIntervalCounts      = in.groupBy(_.response.code.code).mapValues(_.size.toLong)
    val lastIntervalStatusCodes = StatusCodes.from(lastIntervalCounts)

    totalCounts = totalCounts.combine(lastIntervalCounts)
    val totalStatusCodes = StatusCodes.from(totalCounts)

    IO.pure(StatusCodesResult(lastIntervalStatusCodes, totalStatusCodes))
  }

}
