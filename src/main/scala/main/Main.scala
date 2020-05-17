package main

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.syntax.flatMap._
import http.HttpClient
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.{NothingT, SttpBackend}

object Main extends IOApp {

  val sttpBackendResource: Resource[IO, SttpBackend[IO, Nothing, NothingT]] =
    Resource.make(AsyncHttpClientCatsBackend[IO]())(r => r.close)

  def run(args: List[String]): IO[ExitCode] = sttpBackendResource.use { implicit backend =>
    val httpClient = new HttpClient
    val dummyTest  = DummyLoadTest.get(httpClient)(100)
    dummyTest.compile.drain >> IO.delay(println("fin")).as(ExitCode.Success)
  }
}
