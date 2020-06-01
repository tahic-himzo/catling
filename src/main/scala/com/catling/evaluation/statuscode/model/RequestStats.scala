package com.catling.evaluation.statuscode.model

import cats.Show
import cats.syntax.show._

final case class RequestStats(count:      RequestCount, requestPercentage: RequestPercentage)
final case class RequestCount(value:      Long) extends AnyVal
final case class RequestPercentage(value: Double) extends AnyVal

object RequestStats {
  implicit val show: Show[RequestStats] = Show.show {
    case RequestStats(count, requestPercentage) => s"Total: ${count.value} (${requestPercentage.show} %)"
  }
}

object RequestPercentage {
  implicit val show: Show[RequestPercentage] = Show.show {
    case RequestPercentage(value) => "%.3f".format(value * 100)
  }
}
