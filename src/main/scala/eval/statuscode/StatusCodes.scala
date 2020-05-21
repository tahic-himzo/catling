package eval.statuscode

import cats.Show
import cats.syntax.show.toShow
import sttp.model.StatusCode

final case class StatusCodesResult(lastInterval: StatusCodes, total: StatusCodes)
final case class StatusCodes(total:              RequestCount, resultCodes: Map[StatusCode, RequestStats])
final case class RequestCount(value:             Long) extends AnyVal
final case class RequestPercentage(value:        Double) extends AnyVal
final case class RequestStats(count:             RequestCount, requestPercentage: RequestPercentage)

object RequestStats {
  implicit val show: Show[RequestStats] = Show.show {
    case RequestStats(count, requestPercentage) => s"Total: $count (${requestPercentage.show} %)"
  }
}

object RequestPercentage {
  implicit val show: Show[RequestPercentage] = Show.show {
    case RequestPercentage(value) => "%.3f".format(value * 100)
  }
}

object StatusCodesResult {
  implicit val show: Show[StatusCodesResult] = Show.show {
    case StatusCodesResult(lastInterval, total) =>
      s"""
        |Last Interval:
        |${lastInterval.show}
        |
        |Total:
        |${total.show}
        |""".stripMargin
  }
}

object StatusCodes {

  def from(counts: Map[Int, Long]): StatusCodes = {
    val total = counts.values.sum
    val resultCodes = counts.map {
      case (code, count) => (StatusCode(code), RequestStats(RequestCount(count), RequestPercentage(count / total.toDouble)))
    }
    StatusCodes(RequestCount(total), resultCodes)
  }

  implicit val show: Show[StatusCodes] = Show.show {
    case StatusCodes(total, resultCodes) =>
      s"""
        |Status Report: Status Codes
        |-------------------------
        |Total Requests: ${total.value}
        |Status Code Distribution:
        |${resultCodes.mapValues(_.show).mkString("\n")}
        |""".stripMargin
  }
}
