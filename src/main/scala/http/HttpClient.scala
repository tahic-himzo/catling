package http

import cats.effect.IO
import io.circe.Encoder
import sttp.client.circe._
import sttp.client.{NothingT, Response, SttpBackend}
import sttp.model.Uri

class HttpClient(implicit sttpBackend: SttpBackend[IO, Nothing, NothingT]) {

  def post[A: Encoder](url: Uri, body: A, headers: Map[String, String]): IO[Response[String]] =
    sttp.client.quickRequest.headers(headers).post(url).body(body).send()
}
