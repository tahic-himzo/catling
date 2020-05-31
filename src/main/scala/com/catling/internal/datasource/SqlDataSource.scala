package com.catling.internal.datasource

import cats.effect.{ContextShift, IO}
import doobie.syntax.stream.toDoobieStreamOps
import doobie.syntax.string.toSqlInterpolator
import doobie.util.transactor.Transactor
import fs2.Stream

class SqlDataSource(implicit cs: ContextShift[IO]) {

  private val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:testdb",
    "postgres",
    ""
  )

  val stream: Stream[IO, (Int, Int)] = sql"""select id, value from "test-data""""
    .query[(Int, Int)]
    .stream
    .transact(xa)

  def get: Stream[IO, (Int, Int)] = stream

}
