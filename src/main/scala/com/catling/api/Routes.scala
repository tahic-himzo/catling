package com.catling.api

import cats.effect.IO
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.{HttpApp, HttpRoutes}

class Routes() extends Http4sDsl[IO] {

  val service: HttpApp[IO] = HttpRoutes
    .of[IO] {
      case POST -> Root / "load-tests" =>
        Ok()
    }
    .orNotFound
}
