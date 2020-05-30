package com.catling.loadtest

import com.catling.internal.datasource.{ConstantDataSource, CsvDataSource}

object DataSources {
  def const[T](dummy: T): ConstantDataSource[T] = new ConstantDataSource[T](dummy)
  def csv(path:       String, hasHeader: Boolean): CsvDataSource = new CsvDataSource(path, hasHeader)
}
