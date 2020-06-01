package com.catling.datasource

import com.catling.datasource.const.ConstantDataSource
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ConstantDataSourceSpec extends AnyWordSpec with Matchers with EitherValues {
  "ConstantDataSource" should {
    "emit an infinite amount of identical data" in {
      val stream = new ConstantDataSource[String]("dummy").get
      val output = stream.take(10000).compile.toList.attempt.unsafeRunSync()
      output.right.value shouldEqual List.fill(10000)("dummy")
    }
  }
}
