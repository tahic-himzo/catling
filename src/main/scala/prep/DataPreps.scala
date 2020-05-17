package prep

import cats.effect.IO
import fs2.Pipe

object DataPreps {
  def passThrough[T]: Pipe[IO, T, T] = in => in
}
