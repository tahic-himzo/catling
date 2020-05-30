package com.gatling.execution

import java.util.concurrent.atomic.AtomicInteger

import cats.effect.{ContextShift, IO, Timer}
import cats.syntax.flatMap._
import com.catling.internal.execution.ConstantRpsExecutor
import com.catling.internal.http.{HttpClient, Request, TimedResponse}
import io.circe.Encoder
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import sttp.client.Response
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

    def getFastHttpClient = new HttpClient with WithCounter {
      override def post[A: Encoder](req: Request[A])(implicit t: Timer[IO]): IO[TimedResponse[String]] =
        IO.delay(counter.incrementAndGet()) >> IO.pure(TimedResponse(Response.ok("dummy"), 10))
    }

    /*def slowHttpClient: HttpClient = new HttpClient {
      val counter = new AtomicInteger(0)
      override def post[A: Encoder](req: Request[A])(implicit t: Timer[IO]): IO[TimedResponse[String]] =
        IO.sleep(2.seconds) >> IO.delay(counter.incrementAndGet()) >> IO.pure(TimedResponse(Response.ok("dummy"), 10))
    }*/

    "send x requests in 1 second if RPS is set to low x" in {
      val fastHttpClient      = getFastHttpClient
      val constantRpsExecutor = new ConstantRpsExecutor[String](fastHttpClient, 10)
      val input               = fs2.Stream[IO, Request[String]](Request(uri"http://localhost:8080", "dummy")).repeat
      constantRpsExecutor.apply(input).interruptAfter(2.seconds).compile.drain.unsafeRunSync()
      fastHttpClient.counter.get() shouldEqual 10
    }

    "send 5*x requests in 5 seconds if RPS is set to low x" in {
      val fastHttpClient      = getFastHttpClient
      val constantRpsExecutor = new ConstantRpsExecutor[String](fastHttpClient, 10)
      val input               = fs2.Stream[IO, Request[String]](Request(uri"http://localhost:8080", "dummy")).repeat
      constantRpsExecutor.apply(input).interruptAfter(6.seconds).compile.drain.unsafeRunSync()
      fastHttpClient.counter.get() shouldEqual 50
    }

    "send x requests in 1 second if RPS is set to high x" in {
      val fastHttpClient      = getFastHttpClient
      val constantRpsExecutor = new ConstantRpsExecutor[String](fastHttpClient, 1000)
      val input               = fs2.Stream[IO, Request[String]](Request(uri"http://localhost:8080", "dummy")).repeat
      constantRpsExecutor.apply(input).interruptAfter(2.seconds).compile.drain.unsafeRunSync()
      fastHttpClient.counter.get() shouldEqual 1000
    }

    "send 5*x requests in 5 seconds if RPS is set to high x" in {
      val fastHttpClient      = getFastHttpClient
      val constantRpsExecutor = new ConstantRpsExecutor[String](fastHttpClient, 1000)
      val input               = fs2.Stream[IO, Request[String]](Request(uri"http://localhost:8080", "dummy")).repeat
      constantRpsExecutor.apply(input).interruptAfter(6.seconds).compile.drain.unsafeRunSync()
      fastHttpClient.counter.get() shouldEqual 5000
    }
  }
}
