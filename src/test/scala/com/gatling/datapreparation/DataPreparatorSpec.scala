package com.gatling.datapreparation

import com.catling.internal.datapreparation.DataPreparator
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DataPreparatorSpec extends AnyWordSpec with Matchers with EitherValues {
  "DataPreparator#passThrough" should {
    "leave the input data untouched" in {
      val input  = fs2.Stream(1, 2, 3)
      val output = DataPreparator.passThrough(input).compile.toList.attempt.unsafeRunSync()
      output.right.value shouldEqual List(1, 2, 3)
    }
  }
}
