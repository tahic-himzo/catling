package com.catling.library.loadtest

import cats.data.NonEmptyList
import cats.effect.{ContextShift, IO, Timer}
import com.catling.library.LoadTest
import com.catling.library.datasource.DataSources
import com.catling.library.evaluation.Evaluator
import com.catling.library.execution.constant.ConstantRpsExecutor
import com.catling.library.execution.{ExecutionStep, Executor, Executors}
import com.catling.library.http.HttpMocks
import com.catling.library.http.model.{Request, TimedResponse}
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sttp.model.Uri.UriContext

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationDouble

class LoadTestSpec extends AnyWordSpec with Matchers with EitherValues {
  "LoadTest" should {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
    implicit val timer: Timer[IO]     = IO.timer(ExecutionContext.global)

    "compose the parts properly" in {
      val httpClient = HttpMocks.get
      val exec1      = new ConstantRpsExecutor[String](httpClient, 10)
      val exec2      = new ConstantRpsExecutor[String](httpClient, 20)
      val ds         = DataSources.const(Request(uri"http://localhost:8080", "dummy")).get
      val executor: Executor[String, String] = Executors.from(
        NonEmptyList.of(
          ExecutionStep(exec1, 5.seconds),
          ExecutionStep(exec2, 5.seconds)
        )
      )
      val evaluator = new Evaluator[String, String] {
        override def apply(v1: List[TimedResponse[String]]): IO[String] = IO.delay("dummy")
      }

      val loadTest = LoadTest.from(ds, executor, evaluator, 5.seconds)
      val output   = loadTest.compile.toList.attempt.unsafeRunSync()
      output.right.value shouldEqual List("dummy", "dummy")
    }
  }

}
