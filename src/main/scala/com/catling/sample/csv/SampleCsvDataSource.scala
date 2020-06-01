package com.catling.sample.csv

import cats.effect.IO
import com.catling.library.datasource.{DataSource, DataSources}
import com.catling.library.http.model.Request
import com.catling.sample.{SampleItem, SamplePayload, SampleRequest}

class SampleCsvDataSource(batchSize: Int) extends DataSource[Request[SamplePayload]] {

  private val rawCsvStream = DataSources.csv("sample.csv", hasHeader = true).get

  override def get: fs2.Stream[IO, Request[SamplePayload]] =
    rawCsvStream
      .chunkN(batchSize)
      .map(
        c => {
          val items = c.toList.map {
            case Vector(someKey, someValue) => SampleItem(someKey.toInt, someValue.toInt)
          }
          SampleRequest.get(SamplePayload(items))
        }
      )
}
