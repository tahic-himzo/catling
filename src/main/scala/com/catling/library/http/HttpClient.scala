package com.catling.library.http

import cats.effect.{IO, Timer}
import com.catling.library.http.model.{Request, TimedResponse}
import io.circe.Encoder

trait HttpClient {
  def post[A: Encoder](req: Request[A])(implicit t: Timer[IO]): IO[TimedResponse[String]]
}
