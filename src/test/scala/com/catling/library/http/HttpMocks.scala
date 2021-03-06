package com.catling.library.http

import cats.effect.{IO, Timer}
import cats.syntax.flatMap._
import com.catling.library.execution.WithCounter
import com.catling.library.http.model.{Request, TimedResponse}
import io.circe.Encoder
import sttp.client.Response

object HttpMocks {

  def get: HttpClient with WithCounter = new HttpClient with WithCounter {
    override def post[A: Encoder](req: Request[A])(implicit t: Timer[IO]): IO[TimedResponse[String]] =
      IO.delay(counter.incrementAndGet()) >> IO.pure(TimedResponse(Response.ok("dummy"), 10))
  }
}
