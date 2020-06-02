package com.catling

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.catling.api.Routes
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.{NothingT, SttpBackend}

import scala.concurrent.ExecutionContext

object Main extends IOApp {

  val sttpBackendResource: Resource[IO, SttpBackend[IO, Nothing, NothingT]] =
    Resource.make(AsyncHttpClientCatsBackend[IO]())(r => r.close)

  def run(args: List[String]): IO[ExitCode] = sttpBackendResource.use { implicit backend =>
    println(backend)
    val httpApp = new Routes().service
    Server.run(httpApp, ExecutionContext.global).as(ExitCode.Success)
  }
}
