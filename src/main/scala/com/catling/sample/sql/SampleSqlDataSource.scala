package com.catling.sample.sql

import cats.effect.{ContextShift, IO}
import com.catling.datasource.sql.Transactors
import com.catling.datasource.{DataSource, DataSources}
import com.catling.http.model.Request
import com.catling.sample.{SampleItem, SamplePayload, SampleRequest}
import doobie.syntax.string.toSqlInterpolator

class SampleSqlDataSource(batchSize: Int)(implicit cs: ContextShift[IO]) extends DataSource[Request[SamplePayload]] {

  private val xa        = Transactors.postgres("testdb", "postgres", "")
  private val sqlStream = DataSources.sql[SampleItem](sql"""select some-key, some-value from "test-data"""", xa).get

  override def get: fs2.Stream[IO, Request[SamplePayload]] =
    sqlStream.chunkN(batchSize).map(c => SampleRequest.get(SamplePayload(c.toList)))
}
