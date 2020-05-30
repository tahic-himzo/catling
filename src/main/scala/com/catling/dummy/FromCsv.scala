package com.catling.dummy

import cats.effect.IO
import com.catling.internal.http.Request
import fs2.Pipe
import io.circe.Json
import sttp.model.Uri.UriContext

class FromCsv(chunkSize: Int) extends Pipe[IO, Vector[String], Request[Json]] {
  override def apply(in: fs2.Stream[IO, Vector[String]]): fs2.Stream[IO, Request[Json]] =
    in.chunkN(chunkSize)
      .map(
        c => {
          val items = c.toList.map {
            case Vector(ean, quantity) =>
              Json.obj(("some-key", Json.fromString(ean)), ("some-value", Json.fromInt(quantity.toInt)))
          }
          val body = Json.obj(("items", Json.fromValues(items)))

          Request(uri"http://localhost:8080", body, Map("Authorization" -> "Bearer dummy"))
        }
      )
}
