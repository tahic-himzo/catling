package com.catling.loadtest

import cats.effect.IO
import fs2.Pipe
import com.catling.internal.http.{Request, TimedResponse}

abstract class Executor[T, S] extends Pipe[IO, Request[T], TimedResponse[S]]
