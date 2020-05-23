package com.catling.internal.evaluation.latency

import cats.effect.IO
import fs2.{Chunk, Stream}
import com.catling.internal.http.TimedResponse
import com.catling.loadtest.Evaluator

import scala.concurrent.duration._

object LatencyEvaluator extends Evaluator[Latency] {

  override val empty: Latency = Latency(
    P99(0.milliseconds),
    P95(0.milliseconds),
    P90(0.milliseconds),
    P75(0.milliseconds),
    P50(0.milliseconds)
  )

  override def apply(in: Stream[IO, Chunk[TimedResponse[String]]]): fs2.Stream[IO, Latency] =
    in.map(
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
