package com.catling.loadtest

import cats.effect.IO
import com.catling.internal.http.TimedResponse

trait Evaluator[A, B] extends (List[TimedResponse[A]] => IO[B])
