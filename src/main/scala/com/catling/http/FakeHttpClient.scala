package com.catling.http

import java.util.concurrent.TimeUnit

import cats.effect.{IO, Timer}
import com.catling.http.model.{Request, TimedResponse}
import io.circe.Encoder
import sttp.client.Response

class FakeHttpClient extends HttpClient {

  def post[A: Encoder](req: Request[A])(implicit t: Timer[IO]): IO[TimedResponse[String]] =
    for {
      start <- t.clock.monotonic(TimeUnit.MILLISECONDS)
      end   <- t.clock.monotonic(TimeUnit.MILLISECONDS)
    } yield TimedResponse(Response.ok("dummy"), end - start)
}
