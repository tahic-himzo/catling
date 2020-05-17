package eval

import cats.effect.IO
import fs2.Pipe
import sttp.client.Response

object Evaluators {
  def response: Pipe[IO, Response[String], Status] = _.map(v => Status(v.code.toString))
}
