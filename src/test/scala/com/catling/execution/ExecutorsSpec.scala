package com.catling.execution

import cats.data.NonEmptyList
import cats.effect.{ContextShift, IO, Timer}
import com.catling.execution.constant.ConstantRpsExecutor
import com.catling.execution.{ExecutionStep, Executors}
import com.catling.http.model.Request
import http.HttpMocks
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sttp.model.Uri.UriContext

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationDouble

class ExecutorsSpec extends AnyWordSpec with Matchers {
  "Executors#from" should {
    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
    implicit val timer: Timer[IO]     = IO.timer(ExecutionContext.global)

    "chain executor sequentially" in {
      val httpClient = HttpMocks.get
      val exec1      = new ConstantRpsExecutor[String](httpClient, 10)
      val exec2      = new ConstantRpsExecutor[String](httpClient, 20)
      val exec3      = new ConstantRpsExecutor[String](httpClient, 40)
      val exec4      = new ConstantRpsExecutor[String](httpClient, 80)
      val input      = fs2.Stream[IO, Request[String]](Request(uri"http://localhost:8080", "dummy")).repeat

      val executor = Executors.from(
        NonEmptyList.of(
          ExecutionStep(exec1, 2.seconds),
          ExecutionStep(exec2, 4.seconds),
          ExecutionStep(exec3, 4.seconds),
          ExecutionStep(exec4, 4.seconds)
        )
      )
      executor.apply(input).interruptAfter(14.seconds).compile.drain.unsafeRunSync()
      httpClient.counter.get() shouldEqual 580
    }
  }
}
