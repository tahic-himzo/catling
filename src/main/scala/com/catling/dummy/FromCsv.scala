package com.catling.dummy

import cats.effect.IO
import com.catling.internal.http.Request
import io.circe.Json
import com.catling.loadtest.DataPreparator
import sttp.model.Uri.UriContext

class FromCsv(chunkSize: Int) extends DataPreparator[Array[String], Request[Json]] {
  override def apply(in: fs2.Stream[IO, Array[String]]): fs2.Stream[IO, Request[Json]] =
    in.chunkN(chunkSize)
      .map(
        c => {
          val items = c.toList.map {
            case Array(ean, quantity) =>
              Json.obj(("some-key", Json.fromString(ean)), ("some-value", Json.fromInt(quantity.toInt)))
          }
          val body = Json.obj(("items", Json.fromValues(items)))

          Request(uri"http://localhost:8080", body, Map("Authorization" -> "Bearer dummy"))
        }
      )
}
