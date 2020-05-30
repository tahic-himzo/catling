package com.catling.mstock

import cats.effect.IO
import com.catling.internal.http.Request
import fs2.Pipe
import io.circe.Json
import sttp.model.Uri.UriContext

class FromCsv(chunkSize: Int) extends Pipe[IO, Array[String], Request[Json]] {
  override def apply(in: fs2.Stream[IO, Array[String]]): fs2.Stream[IO, Request[Json]] =
    in.chunkN(chunkSize)
      .map(
        c => {
          val items = c.toList.map {
            case Array(ean, quantity) => Json.obj(("ean", Json.fromString(ean)), ("quantity", Json.fromInt(quantity.toInt)))
          }
          val body = Json.obj(("items", Json.fromValues(items)))

          val url =
            uri"https://merchant-stock-service-mocked.merchant-center.zalan.do/merchants/d4fc1867-b707-4cfc-8988-0ffd87160ceb/stock-locations/some-stock-location/stock-quantities"
          val additionalHeaders = Map(
            "X-Consumer" -> "eyJjbGllbnRfaWQiOiIiLCJncm91cHMiOlsiIl0sInNjb3BlcyI6WyJwcm9kdWN0cy9zdG9jay1sb2NhdGlvbi1xdWFudGl0aWVzL3dyaXRlIl0sImJwaWRzIjpbImQ0ZmMxODY3LWI3MDctNGNmYy04OTg4LTBmZmQ4NzE2MGNlYiJdfQ",
            "X-Consumer-Signature" -> "asd-sCfaE7sxKmgJdl4lSdV8lhDH-hElj0SLk7sAylrVbMhfWBYq2ur3kj0Tzzlfyr17FdZGXv-cSXXnqksgHeQRmYQBjwx7OVlWyMQSO6vCwsBpko1HP4i9Yw88OgLS5K0In0tjhmp5OpBIIpO_Lq4nfOab47r6Z-0WahWH8JTY3fWXkoBdoCiPqDteVi4TM9PikHX84wOSeZi7jxOVC1A3sN0pMScxXbghR6CtmGMOIQUbKEZ-4GGPPgifGYjQeuCGh6O4A7vty28_uRhbfuJqBo-KHenVwlLA2j1JLMyP4ei-HjSVj1MGXYTfjYZmUVF-6uzwbluQBGszfVl-CKcOQaLTefyRdMrkJXSzPFGYoIGJR_TN5v6SApPJcf2TtWdfbf7imRREOlvwADDJQWTqTgbmNjcenyDsyLQyJ8-YcmTADs7JN0fRcFDneJFy5YdMHM8oH3_fZkpNukBYkqepA54r2cYjrZ4v46-tYS_hCK-1iDzOaxGgftdGWPo-23-Fu9Y69kbvGlJJwEfMAreK8ya29OPSuoR3ChWyiW2uUWzncpI8l6JRAsDP7U11r0KS6uPvhgqWFjWytadQRJV0rHtWZNVvBOGLU10cjwlckxJwrEaw9uU3DDTclPoaPKjGBEkYNusYJP_Tm5t6xEsf3outToOEYbgkDDIMe_g",
            "X-Consumer-Key-Id" -> "a-key",
            "Authorization" -> "Bearer dummy"
          )

          Request(url, body, additionalHeaders)
        }
      )
}
