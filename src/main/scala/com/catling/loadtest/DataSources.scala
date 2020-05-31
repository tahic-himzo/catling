package com.catling.loadtest

import cats.effect.{ContextShift, IO}
import com.catling.internal.datasource.{ConstantDataSource, CsvDataSource, SqlDataSource}

object DataSources {
  def const[T](dummy:  T): ConstantDataSource[T] = new ConstantDataSource[T](dummy)
  def csv(path:        String, hasHeader: Boolean): CsvDataSource = new CsvDataSource(path, hasHeader)
  def sql(implicit cs: ContextShift[IO]): SqlDataSource = new SqlDataSource
}
