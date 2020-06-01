package com.gatling.execution

import java.util.concurrent.atomic.AtomicInteger

import cats.effect.{ContextShift, IO, Timer}
import com.catling.execution.constant.ConstantRpsExecutor
import com.catling.http.model.Request
import http.HttpMocks
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sttp.model.Uri.UriContext

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationDouble

trait WithCounter {
  val counter = new AtomicInteger(0)
}

class ConstantRpsExecutorSpec extends AnyWordSpec with Matchers {
  "ConstantRpsExecutor" should {

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
    implicit val timer: Timer[IO]     = IO.timer(ExecutionContext.global)

    "send x requests in 1 second if RPS is set to low x" in {
      val httpClient          = HttpMocks.get
      val constantRpsExecutor = new ConstantRpsExecutor[String](httpClient, 10)
      val input               = fs2.Stream[IO, Request[String]](Request(uri"http://localhost:8080", "dummy")).repeat
      constantRpsExecutor.apply(input).interruptAfter(2.seconds).compile.drain.unsafeRunSync()
      httpClient.counter.get() shouldEqual 10
    }

    "send 5*x requests in 5 seconds if RPS is set to low x" in {
      val httpClient          = HttpMocks.get
      val constantRpsExecutor = new ConstantRpsExecutor[String](httpClient, 10)
      val input               = fs2.Stream[IO, Request[String]](Request(uri"http://localhost:8080", "dummy")).repeat
      constantRpsExecutor.apply(input).interruptAfter(6.seconds).compile.drain.unsafeRunSync()
      httpClient.counter.get() shouldEqual 50
    }

    "send x requests in 1 second if RPS is set to high x" in {
      val httpClient          = HttpMocks.get
      val constantRpsExecutor = new ConstantRpsExecutor[String](httpClient, 1000)
      val input               = fs2.Stream[IO, Request[String]](Request(uri"http://localhost:8080", "dummy")).repeat
      constantRpsExecutor.apply(input).interruptAfter(2.seconds).compile.drain.unsafeRunSync()
      httpClient.counter.get() shouldEqual 1000
    }

    "send 5*x requests in 5 seconds if RPS is set to high x" in {
      val httpClient          = HttpMocks.get
      val constantRpsExecutor = new ConstantRpsExecutor[String](httpClient, 1000)
      val input               = fs2.Stream[IO, Request[String]](Request(uri"http://localhost:8080", "dummy")).repeat
      constantRpsExecutor.apply(input).interruptAfter(6.seconds).compile.drain.unsafeRunSync()
      httpClient.counter.get() shouldEqual 5000
    }
  }
}
