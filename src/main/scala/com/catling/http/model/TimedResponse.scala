package com.catling.http.model

import sttp.client.Response

final case class TimedResponse[T](response: Response[T], durInMs: Long)
