package com.catling

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.syntax.flatMap._
import cats.syntax.show._
import com.catling.sample._
import sttp.client.asynchttpclient.cats.AsyncHttpClientCatsBackend
import sttp.client.{NothingT, SttpBackend}

object Main extends IOApp {

  val sttpBackendResource: Resource[IO, SttpBackend[IO, Nothing, NothingT]] =
    Resource.make(AsyncHttpClientCatsBackend[IO]())(r => r.close)

  def run(args: List[String]): IO[ExitCode] = sttpBackendResource.use { implicit backend =>
    println(backend)
    val dummyTest = sql.SampleLoadTest.get
    dummyTest.map {
      case (result, latency) =>
        println(latency.show)
        println(result.show)
    }.compile.drain >> IO.delay(println("fin")).as(ExitCode.Success)
  }
}
