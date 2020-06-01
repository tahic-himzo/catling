package com.catling

import cats.NonEmptyParallel
import cats.data.NonEmptyList
import cats.effect.{ContextShift, IO, Timer}
import com.catling.internal.datasource.Transactors
import com.catling.internal.evaluation.Evaluators
import com.catling.internal.evaluation.latency.Latency
import com.catling.internal.evaluation.statuscode.model.StatusCodesResult
import com.catling.internal.execution.{ConstantRpsExecutor, ExecutionStep, Executors}
import com.catling.internal.http.HttpClient
import com.catling.loadtest.{DataSources, Evaluator, LoadTest}
import doobie.syntax.string.toSqlInterpolator
import fs2.Stream
import io.circe.Json

import scala.concurrent.duration._

object SimpleLoadTest {

  def get(httpClient: HttpClient)(batchSize: Int)(
      implicit t:     Timer[IO],
      ec:             ContextShift[IO],
      nep:            NonEmptyParallel[IO]): Stream[IO, (StatusCodesResult, Latency)] = {
    val query = sql"""select id, value from "test-data""""
    val xa    = Transactors.postgres("testdb", "postgres", "")
    val ds    = DataSources.sql[(Int, Int)](query, xa, buffer = 10000).get
    val prep  = new dummy.FromTuple2(batchSize)

    val exec = Executors.from[Json, String](
      NonEmptyList.of(
        ExecutionStep(new ConstantRpsExecutor(httpClient, 10), 10.seconds),
        ExecutionStep(new ConstantRpsExecutor(httpClient, 15), 10.seconds)
      )
    )
    val eval: Evaluator[String, (StatusCodesResult, Latency)] = Evaluators.default[String]
    LoadTest.from(ds, prep, exec, 5.seconds, eval)
  }

}
