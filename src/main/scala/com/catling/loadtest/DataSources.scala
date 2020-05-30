package com.catling.loadtest

import cats.effect.IO
import fs2.Stream
import com.catling.internal.datasource.CsvDataSource

object DataSources {
  def const[T](dummy: T): Stream[IO, T] = Stream(dummy).repeat
  def csv[T](path:    String, hasHeader: Boolean): CsvDataSource = new CsvDataSource(path, hasHeader)
}
