package com.catling.internal.datasource

import cats.effect.{ContextShift, IO}
import doobie.syntax.stream._
import doobie.util.Read
import doobie.util.fragment.Fragment
import doobie.util.transactor.Transactor
import fs2.Stream

class SqlDataSource[T: Read](sql: Fragment, xa: Transactor[IO])(buffer: Option[Int] = None)(implicit cs: ContextShift[IO]) {

  def get: Stream[IO, T] = {
    val stream = sql.query[T].stream.transact(xa)
    buffer match {
      case Some(value) => stream.buffer(value)
      case None        => stream
    }
  }

}

object Transactors {

  def postgres(dbName: String, user: String, password: String)(implicit cs: ContextShift[IO]): Transactor[IO] =
    Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      s"jdbc:postgresql:$dbName",
      user,
      password
    )
}
