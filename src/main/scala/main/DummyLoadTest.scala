package main

import cats.data.NonEmptyList
import cats.effect._
import ds.DataSources
import eval.Evaluators
import eval.statuscode.model.StatusCodesResult
import exec._
import fs2.Stream
import http.{HttpClient, Request}
import io.circe.Json
import prep.DataPreps

import scala.concurrent.duration.DurationInt

object DummyLoadTest {

  def get(httpClient: HttpClient)(
      batchSize:      Int)(implicit timer: Timer[IO], ec: ContextShift[IO]): Stream[IO, StatusCodesResult] = {
    val ds   = DataSources.const(DummyRequest.get(batchSize))
    val prep = DataPreps.passThrough[Request[Json]]

    val exec = Executor.fromAlt[Json, String](
      NonEmptyList.of(
        ExecutionStep(new ConstantRpsExecutor(httpClient, 10), 10.seconds),
        ExecutionStep(new ConstantRpsExecutor(httpClient, 15), 10.seconds)
      )
    )
    val eval = Evaluators.responseCodes(interval = 5.seconds)
    LoadTest.from(ds, prep, exec, eval)
  }

}
