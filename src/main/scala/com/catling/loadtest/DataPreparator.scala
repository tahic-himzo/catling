package com.catling.loadtest

import cats.effect.IO
import fs2.Pipe

abstract class DataPreparator[A, B] extends Pipe[IO, A, B]
