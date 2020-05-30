package com.gatling.evaluation

import com.catling.internal.evaluation.latency._
import com.catling.internal.http.TimedResponse
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sttp.client.Response

import scala.concurrent.duration.DurationDouble

class LatencyEvaluatorSpec extends AnyWordSpec with Matchers with EitherValues {

  "LatencyEvaluator" should {
    "return correct percentiles" in {
      val latencyEvaluator = new LatencyEvaluator[String]
      val input            = List.range(1, 101).map(getDummyResponse(_))
      val expectedOutput   = Latency(P99(99.millis), P95(95.millis), P90(90.millis), P75(75.millis), P50(50.millis))
      val output           = latencyEvaluator.apply(input).attempt.unsafeRunSync()
      output.right.value shouldEqual expectedOutput
    }
  }

  def getDummyResponse(durInMs: Long): TimedResponse[String] = TimedResponse(Response.ok("dummy"), durInMs)
}
