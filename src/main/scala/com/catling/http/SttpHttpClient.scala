package com.catling.http

import java.util.concurrent.TimeUnit

import cats.effect.{IO, Timer}
import com.catling.http.model.{Request, TimedResponse}
import io.circe.Encoder
import sttp.client.circe._
import sttp.client.{NothingT, SttpBackend}

class SttpHttpClient(implicit sttpBackend: SttpBackend[IO, Nothing, NothingT]) extends HttpClient {

  def post[A: Encoder](req: Request[A])(implicit t: Timer[IO]): IO[TimedResponse[String]] = {
    val request = sttp.client.quickRequest.headers(req.headers).post(req.url).body(req.payload)
    for {
      start <- t.clock.monotonic(TimeUnit.MILLISECONDS)
      res   <- request.send()
      end   <- t.clock.monotonic(TimeUnit.MILLISECONDS)
    } yield TimedResponse(res, end - start)
  }
}
