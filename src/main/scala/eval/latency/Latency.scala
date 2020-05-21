package eval.latency

import scala.concurrent.duration._

case class Latency(p99: P99, p95: P95, p90: P90, p75: P75, p50: P50)

case class P99(value: FiniteDuration) extends AnyVal
case class P95(value: FiniteDuration) extends AnyVal
case class P90(value: FiniteDuration) extends AnyVal
case class P75(value: FiniteDuration) extends AnyVal
case class P50(value: FiniteDuration) extends AnyVal
