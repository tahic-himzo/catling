package com.catling.internal.datasource

import cats.effect.IO
import fs2.Stream

class ConstantDataSource[T](dummy: T) {
  val stream: Stream[IO, T] = Stream(dummy).repeat

  def get: Stream[IO, T] = stream
}
