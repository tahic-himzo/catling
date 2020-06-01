package com.catling.datasource.sql

import cats.effect.{ContextShift, IO}
import com.catling.datasource.DataSource
import doobie.syntax.stream._
import doobie.util.Read
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import fs2.Stream

class SqlDataSource[T: Read](sql: Fragment, xa: Transactor[IO])(buffer: Option[Int] = None)(implicit cs: ContextShift[IO])
    extends DataSource[T] {

  def get: Stream[IO, T] = {
    val stream = sql.query[T].stream.transact(xa)
    buffer match {
      case Some(value) => stream.buffer(value)
      case None        => stream
    }
  }

}
