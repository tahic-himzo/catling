package com.catling.loadtest

import cats.effect.{ContextShift, IO}
import com.catling.internal.datasource.{ConstantDataSource, CsvDataSource, SqlDataSource}
import doobie.util.Read
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor

object DataSources {
  def const[T](dummy: T): ConstantDataSource[T] = new ConstantDataSource[T](dummy)
  def csv(path:       String, hasHeader: Boolean): CsvDataSource = new CsvDataSource(path, hasHeader)

  def sql[T: Read](sql: Fragment, xa: Transactor[IO], buffer: Int)(implicit cs: ContextShift[IO]): SqlDataSource[T] =
    new SqlDataSource(sql, xa)(Some(buffer))

  def sql[T: Read](sql: Fragment, xa: Transactor[IO])(implicit cs: ContextShift[IO]): SqlDataSource[T] =
    new SqlDataSource(sql, xa)()
}
