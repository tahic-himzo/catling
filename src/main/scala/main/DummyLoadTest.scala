package main

import cats.data.NonEmptyList
import cats.effect._
import ds.DataSources
import eval.{Evaluators, Status}
import exec.{ExecutionStep, ExecutorFactory}
import fs2.Stream
import http.HttpClient
import io.circe.Json
import prep.DataPreps
import sttp.client._
import sttp.model.Uri

import scala.concurrent.duration.DurationInt

object DummyLoadTest {

  def get(httpClient: HttpClient)(batchSize: Int)(implicit timer: Timer[IO], ec: ContextShift[IO]): Stream[IO, Status] = {
    val ds   = DataSources.const(dummyRequest(batchSize))
    val prep = DataPreps.passThrough[Json]

    val execFactory = new ExecutorFactory(httpClient, url)
    val exec = execFactory.from[Json, Response[String]](
      NonEmptyList.of(
        ExecutionStep(execFactory.constantRPS(1), 10.seconds),
        ExecutionStep(execFactory.constantRPS(2), 10.seconds),
        ExecutionStep(execFactory.constantRPS(3), 10.seconds)
      )
    )
    def eval = Evaluators.response
    LoadTest.from(ds, prep, exec, eval)
  }

  private def dummyRequest(batchSize: Int): Json = {
    val items = List
      .range(0, batchSize)
      .map(i => Json.obj(("id", Json.fromString(i.toString)), ("some-value", Json.fromInt(i))))
    Json.obj(("items", Json.fromValues(items)))
  }

  private val url: Uri = uri"http://localhost:8080/dummy"
}
