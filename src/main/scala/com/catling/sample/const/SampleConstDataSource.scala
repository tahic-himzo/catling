package com.catling.sample.const

import cats.effect.IO
import com.catling.datasource.{DataSource, DataSources}
import com.catling.http.model.Request
import com.catling.sample.{SampleItem, SamplePayload, SampleRequest}

class SampleConstDataSource(batchSize: Int) extends DataSource[Request[SamplePayload]] {

  override def get: fs2.Stream[IO, Request[SamplePayload]] = {
    val items   = List.range(0, batchSize).map(i => SampleItem(i, i))
    val request = SampleRequest.get(SamplePayload(items))
    DataSources.const(request).get
  }
}
