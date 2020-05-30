package com.gatling.datasource

import com.catling.internal.datasource.CsvDataSource
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CsvDataSourceSpec extends AnyWordSpec with Matchers with EitherValues {
  "CsvDataSource" should {
    "load data from a CSV" in {
      val csvStream = new CsvDataSource("test_without_header.csv", hasHeader = false).get
      val expectedOutput = List(
        List("9071151191", "405"),
        List("1274860393", "283"),
        List("6792896903", "025")
      )
      val output = csvStream.compile.toList.attempt.unsafeRunSync()
      output.right.value.map(_.toList) should ===(expectedOutput)
    }

    "skip the header if applicable" in {
      val csvStream = new CsvDataSource("test_with_header.csv", hasHeader = true).get
      val expectedOutput = List(
        List("9071151191", "405"),
        List("1274860393", "283"),
        List("6792896903", "025")
      )
      val output = csvStream.compile.toList.attempt.unsafeRunSync()
      output.right.value.map(_.toList) should ===(expectedOutput)
    }
  }
}
