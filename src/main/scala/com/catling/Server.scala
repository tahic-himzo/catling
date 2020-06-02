package com.catling

import cats.effect.{ConcurrentEffect, ExitCode, IO, Timer}
import org.http4s.HttpApp
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext

object Server {

  def run(httpApp:               HttpApp[IO], ec: ExecutionContext)(
      implicit concurrentEffect: ConcurrentEffect[IO],
      timer:                     Timer[IO]): IO[ExitCode] =
    BlazeServerBuilder
      .apply(ec)
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
