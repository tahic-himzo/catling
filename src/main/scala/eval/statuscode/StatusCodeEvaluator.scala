package eval.statuscode

import cats.effect.{Concurrent, IO, Timer}
import cats.implicits._
import eval.statuscode.model.{StatusCodes, StatusCodesResult}
import fs2.Pipe
import http.TimedResponse

import scala.concurrent.duration._

class StatusCodeEvaluator(interval: FiniteDuration)(implicit t: Timer[IO], c: Concurrent[IO]) {

  private var totalCounts: Map[Int, Long] = Map.empty

  val pipe: Pipe[IO, TimedResponse[String], StatusCodesResult] = in =>
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
