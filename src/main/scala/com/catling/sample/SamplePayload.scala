package com.catling.sample

import io.circe.Encoder
import io.circe.derivation.deriveEncoder

case class SamplePayload(items: List[SampleItem])
case class SampleItem(someKey:  Int, someValue: Int)

object SamplePayload {
  implicit val encoder: Encoder[SamplePayload] = deriveEncoder
}

object SampleItem {
  implicit val encoder: Encoder[SampleItem] = deriveEncoder
}
