package com.catling.internal.evaluation.latency

import cats.effect.IO
import com.catling.internal.http.TimedResponse
import com.catling.loadtest.Evaluator

import scala.concurrent.duration._

class LatencyEvaluator[A] extends Evaluator[A, Latency] {

  override def apply(in: List[TimedResponse[A]]): IO[Latency] = {
    val sorted = in.map(_.durInMs).toArray.sorted
    val p99    = P99(getPercentile(sorted, 99))
    val p95    = P95(getPercentile(sorted, 95))
    val p90    = P90(getPercentile(sorted, 90))
    val p75    = P75(getPercentile(sorted, 75))
    val p50    = P50(getPercentile(sorted, 50))
    IO.pure(Latency(p99, p95, p90, p75, p50))
  }

  private def getPercentile(sortedLatencies: Array[Long], p: Int): FiniteDuration =
    math.ceil((sortedLatencies.length - 1) * (p / 100.0)).toInt.milliseconds

}
