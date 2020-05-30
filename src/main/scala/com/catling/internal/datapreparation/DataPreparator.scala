package com.catling.internal.datapreparation

import cats.effect.IO
import fs2.Pipe

object DataPreparator {
  def passThrough[A]: Pipe[IO, A, A] = in => in
}
