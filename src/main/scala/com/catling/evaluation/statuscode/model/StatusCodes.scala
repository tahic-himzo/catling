package com.catling.evaluation.statuscode.model

import cats.Show
import cats.syntax.show.toShow
import sttp.model.StatusCode

final case class StatusCodes(total: RequestCount, resultCodes: Map[StatusCode, RequestStats])

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
