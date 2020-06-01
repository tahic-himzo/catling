package com.catling.datasource

import cats.effect.{ContextShift, IO}
import com.catling.datasource.const.ConstantDataSource
import com.catling.datasource.csv.CsvDataSource
import com.catling.datasource.sql.SqlDataSource
import doobie.util.Read
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import fs2.Stream

object DataSources {
  def const[T](dummy: T): ConstantDataSource[T] = new ConstantDataSource[T](dummy)
  def csv(path:       String, hasHeader: Boolean): CsvDataSource = new CsvDataSource(path, hasHeader)

  def sql[T: Read](sql: Fragment, xa: Transactor[IO], buffer: Int)(implicit cs: ContextShift[IO]): SqlDataSource[T] =
    new SqlDataSource(sql, xa)(Some(buffer))

  def sql[T: Read](sql: Fragment, xa: Transactor[IO])(implicit cs: ContextShift[IO]): SqlDataSource[T] =
    new SqlDataSource(sql, xa)()
}

abstract class DataSource[T] {
  def get: Stream[IO, T]
}
