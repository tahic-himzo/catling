package com.catling.dummy

import cats.effect.IO
import com.catling.internal.http.Request
import fs2.Pipe
import io.circe.Json
import sttp.model.Uri.UriContext

class FromTuple2(chunkSize: Int) extends Pipe[IO, (Int, Int), Request[Json]] {
  override def apply(in: fs2.Stream[IO, (Int, Int)]): fs2.Stream[IO, Request[Json]] =
    in.chunkN(chunkSize)
      .map(
        c => {
          val items = c.toList.map {
            case (ean, quantity) =>
              Json.obj(("some-key", Json.fromString(ean.toString)), ("some-value", Json.fromInt(quantity)))
          }
          val body = Json.obj(("items", Json.fromValues(items)))

          Request(uri"http://localhost:8080", body, Map("Authorization" -> "Bearer dummy"))
        }
      )
}
