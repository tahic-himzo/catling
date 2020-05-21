package main

import cats.data.NonEmptyList
import cats.effect._
import ds.DataSources
import eval.Evaluators
import eval.statuscode.StatusCodesResult
import exec._
import fs2.Stream
import http.{HttpClient, Request, TimedResponse}
import io.circe.Json
import prep.DataPreps

import scala.concurrent.duration.DurationInt

object DummyLoadTest {

  def get(httpClient: HttpClient)(
      batchSize:      Int)(implicit timer: Timer[IO], ec: ContextShift[IO]): Stream[IO, StatusCodesResult] = {
    val ds   = DataSources.const(DummyRequest.get(batchSize))
    val prep = DataPreps.passThrough[Request[Json]]

    val execFactory = new ExecutorFactory(httpClient)
    val exec = execFactory.from[Request[Json], TimedResponse[String]](
      NonEmptyList.of(
        ExecutionStep(execFactory.constantRPS(10), 10.seconds)
        //ExecutionStep(execFactory.constantRPS(20), 10.seconds),
        //ExecutionStep(execFactory.constantRPS(30), 10.seconds)
      )
    )
    val eval = Evaluators.responseCodes(5.seconds)
    LoadTest.from(ds, prep, exec, eval)
  }

}
