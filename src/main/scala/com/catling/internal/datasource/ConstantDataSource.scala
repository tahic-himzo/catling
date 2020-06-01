package com.catling.internal.datasource

import cats.effect.IO
import fs2.Stream

class ConstantDataSource[T](dummy: T) {

  def get: Stream[IO, T] = Stream(dummy).repeat
}
