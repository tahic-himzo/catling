package com.catling.evaluation.latency

import cats.Show

import scala.concurrent.duration._

case class Latency(p99: P99, p95: P95, p90: P90, p75: P75, p50: P50)

object Latency {
  implicit val show: Show[Latency] = Show.show {
    case Latency(p99, p95, p90, p75, p50) =>
      s"""
        |Latency:
        |----------------
        |p99: ${p99.value}
        |p95: ${p95.value}
        |p90: ${p90.value}
        |p75: ${p75.value}
        |p50: ${p50.value}
        |----------------
        |""".stripMargin
  }
}

case class P99(value: FiniteDuration) extends AnyVal
case class P95(value: FiniteDuration) extends AnyVal
case class P90(value: FiniteDuration) extends AnyVal
case class P75(value: FiniteDuration) extends AnyVal
case class P50(value: FiniteDuration) extends AnyVal
