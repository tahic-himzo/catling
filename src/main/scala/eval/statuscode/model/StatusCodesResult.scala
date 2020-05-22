package eval.statuscode.model

import cats.Show
import cats.syntax.show._

final case class StatusCodesResult(lastInterval: StatusCodes, total: StatusCodes)

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
