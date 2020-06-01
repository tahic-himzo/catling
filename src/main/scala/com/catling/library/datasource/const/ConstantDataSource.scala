package com.catling.library.datasource.const

import cats.effect.IO
import com.catling.library.datasource.DataSource
import fs2.Stream

class ConstantDataSource[T](dummy: T) extends DataSource[T] {
  def get: Stream[IO, T] = Stream(dummy).repeat
}
