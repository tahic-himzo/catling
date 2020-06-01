package com.catling.internal.datasource

import cats.effect.IO
import com.opencsv.{CSVReader, CSVReaderHeaderAware}
import fs2.Stream

import scala.collection.JavaConverters.asScalaIteratorConverter
import scala.io.Source

class CsvDataSource(path: String, hasHeader: Boolean) {

  def get: Stream[IO, Vector[String]] =
    Stream
      .bracket(IO.delay(Source.fromResource(path)))(r => IO.delay(r.close()))
      .flatMap(
        s => {
          val reader = if(hasHeader) new CSVReaderHeaderAware(s.reader()) else new CSVReader(s.reader())
          Stream.fromIterator[IO](reader.iterator().asScala.map(_.toVector))
        }
      )

}
