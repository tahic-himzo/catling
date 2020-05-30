package com.catling.loadtest

import cats.effect.IO
import fs2.Pipe

trait DataPreparator[A, B] extends Pipe[IO, A, B]
