package com.catling.datasource.sql

import cats.effect.{ContextShift, IO}
import doobie.util.transactor.Transactor

object Transactors {

  def postgres(dbName: String, user: String, password: String)(implicit cs: ContextShift[IO]): Transactor[IO] =
    Transactor.fromDriverManager[IO](
      "org.postgresql.Driver",
      s"jdbc:postgresql:$dbName",
      user,
      password
    )
}
