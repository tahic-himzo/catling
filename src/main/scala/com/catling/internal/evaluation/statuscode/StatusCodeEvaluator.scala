package com.catling.internal.evaluation.statuscode

import cats.effect.IO
import cats.implicits._
import fs2.{Chunk, Stream}
import com.catling.internal.evaluation.statuscode.model.{RequestCount, StatusCodes, StatusCodesResult}
import com.catling.internal.http.TimedResponse
import com.catling.loadtest.Evaluator

class StatusCodeEvaluator extends Evaluator[StatusCodesResult] {

  override val empty: StatusCodesResult =
    StatusCodesResult(StatusCodes(RequestCount(0), Map.empty), StatusCodes(RequestCount(0), Map.empty))

  private var totalCounts: Map[Int, Long] = Map.empty

  override def apply(in: Stream[IO, Chunk[TimedResponse[String]]]): Stream[IO, StatusCodesResult] =
    in.map(
      c => {
        val lastIntervalCounts      = c.toList.groupBy(_.response.code.code).mapValues(_.size.toLong)
        val lastIntervalStatusCodes = StatusCodes.from(lastIntervalCounts)

        totalCounts = totalCounts.combine(lastIntervalCounts)
        val totalStatusCodes = StatusCodes.from(totalCounts)

        StatusCodesResult(lastIntervalStatusCodes, totalStatusCodes)
      }
    )
}
