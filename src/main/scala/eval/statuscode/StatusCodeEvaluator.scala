package eval.statuscode

import cats.effect.{Concurrent, IO, Timer}
import cats.implicits._
import eval.Evaluator
import eval.statuscode.model.{RequestCount, StatusCodes, StatusCodesResult}
import fs2.Stream
import http.TimedResponse

import scala.concurrent.duration._

class StatusCodeEvaluator(interval: FiniteDuration)(implicit t: Timer[IO], c: Concurrent[IO])
    extends Evaluator[StatusCodesResult] {

  override val empty: StatusCodesResult =
    StatusCodesResult(StatusCodes(RequestCount(0), Map.empty), StatusCodes(RequestCount(0), Map.empty))

  private var totalCounts: Map[Int, Long] = Map.empty

  override def apply(in: Stream[IO, TimedResponse[String]]): Stream[IO, StatusCodesResult] =
    in.groupWithin(Int.MaxValue, interval)
      .map(
        c => {
          val lastIntervalCounts      = c.toList.groupBy(_.response.code.code).mapValues(_.size.toLong)
          val lastIntervalStatusCodes = StatusCodes.from(lastIntervalCounts)

          totalCounts = totalCounts.combine(lastIntervalCounts)
          val totalStatusCodes = StatusCodes.from(totalCounts)

          StatusCodesResult(lastIntervalStatusCodes, totalStatusCodes)
        }
      )
}
