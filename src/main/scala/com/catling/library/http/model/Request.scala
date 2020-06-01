package com.catling.library.http.model

import io.circe.Encoder
import sttp.model.Uri

final case class Request[T: Encoder](url: Uri, payload: T, headers: Map[String, String] = Map.empty)
