package com.catling

import cats.data.NonEmptyList
import cats.effect.{ContextShift, IO, Timer}
import com.catling.internal.evaluation.Evaluators
import com.catling.internal.evaluation.latency.Latency
import com.catling.internal.evaluation.statuscode.model.StatusCodesResult
import com.catling.internal.execution.{ConstantRpsExecutor, ExecutionStep, Executors}
import com.catling.internal.http.HttpClient
import com.catling.loadtest.{DataSources, LoadTest}
import fs2.Stream
import io.circe.Json

import scala.concurrent.duration._

object SimpleLoadTest {

  def get(httpClient: HttpClient)(
      batchSize:      Int)(implicit t: Timer[IO], ec: ContextShift[IO]): Stream[IO, (StatusCodesResult, Latency)] = {
    val ds   = DataSources.csv("test.csv", hasHeader = true)
    val prep = new dummy.FromCsv(batchSize)

    val exec = Executors.from[Json, String](
      NonEmptyList.of(
        ExecutionStep(new ConstantRpsExecutor(httpClient, 10), 10.seconds),
        ExecutionStep(new ConstantRpsExecutor(httpClient, 15), 10.seconds)
      )
    )
    val eval = Evaluators.default
    LoadTest.from(ds, prep, exec, 5.seconds, eval)
  }

}
