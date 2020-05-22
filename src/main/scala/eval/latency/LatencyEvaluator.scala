package eval.latency

import cats.effect.{Concurrent, IO, Timer}
import eval.Evaluator
import fs2.Stream
import http.TimedResponse

import scala.concurrent.duration._

class LatencyEvaluator(interval: FiniteDuration)(implicit t: Timer[IO], c: Concurrent[IO]) extends Evaluator[Latency] {

  override val empty: Latency = Latency(
    P99(0.milliseconds),
    P95(0.milliseconds),
    P90(0.milliseconds),
    P75(0.milliseconds),
    P50(0.milliseconds)
  )

  override def apply(in: Stream[IO, TimedResponse[String]]): fs2.Stream[IO, Latency] =
    in.groupWithin(Int.MaxValue, interval)
      .map(
        c => {
          val sorted = c.toArray.map(_.durInMs).sorted
          val p99    = P99(getPercentile(sorted, 99))
          val p95    = P95(getPercentile(sorted, 95))
          val p90    = P90(getPercentile(sorted, 90))
          val p75    = P75(getPercentile(sorted, 75))
          val p50    = P50(getPercentile(sorted, 50))
          Latency(p99, p95, p90, p75, p50)
        }
      )

  private def getPercentile(sortedLatencies: Array[Long], p: Int): FiniteDuration =
    math.ceil((sortedLatencies.length - 1) * (p / 100.0)).toInt.milliseconds

}
