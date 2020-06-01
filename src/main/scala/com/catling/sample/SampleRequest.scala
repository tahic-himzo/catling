package com.catling.sample

import com.catling.http.model.Request
import sttp.model.Uri.UriContext

object SampleRequest {

  def get(sampleRequest: SamplePayload): Request[SamplePayload] = {
    val url               = uri"http://localhost:8080"
    val additionalHeaders = Map("Authorization" -> "Bearer dummy")
    Request(url, sampleRequest, additionalHeaders)
  }
}
