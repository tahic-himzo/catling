package com.catling

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.syntax.show._
import cats.syntax.flatMap._
import com.catling.internal.http.HttpClient
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.{NothingT, SttpBackend}

object Main extends IOApp {

  val sttpBackendResource: Resource[IO, SttpBackend[IO, Nothing, NothingT]] =
    Resource.make(AsyncHttpClientCatsBackend[IO]())(r => r.close)

  def run(args: List[String]): IO[ExitCode] = sttpBackendResource.use { implicit backend =>
    val httpClient = new HttpClient
    val dummyTest  = SimpleLoadTest.get(httpClient)(10)
    dummyTest.map {
      case (result, latency) =>
        println(latency.show)
        println(result.show)
    }.compile.drain >> IO.delay(println("fin")).as(ExitCode.Success)
  }
}
