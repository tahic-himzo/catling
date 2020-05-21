package main

import http.Request
import io.circe.Json
import sttp.model.Uri.UriContext

object DummyRequest {

  def get(batchSize: Int): Request[Json] = {
    val items = List
      .range(0, batchSize)
      .map(i => Json.obj(("some-key", Json.fromString(i.toString)), ("some-value", Json.fromInt(i))))
    val body = Json.obj(("items", Json.fromValues(items)))

    val url =
      uri"http://localhost:8080"

    val additionalHeaders = Map(
      "Authorization" -> "Bearer dummy"
    )
    Request(url, body, additionalHeaders)
  }
}
