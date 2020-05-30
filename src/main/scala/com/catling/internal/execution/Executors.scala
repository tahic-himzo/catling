package com.catling.internal.execution

import cats.data.NonEmptyList
import cats.effect.{Concurrent, IO, Timer}
import com.catling.loadtest.Executor
import io.circe.Encoder

object Executors {

  def from[T: Encoder, S](
      executors: NonEmptyList[ExecutionStep[T, S]])(implicit t: Timer[IO], c: Concurrent[IO]): Executor[T, S] =
    in => {
      val initExec   = executors.head
      val initStream = in.through(initExec.executor).take(initExec.totalRequests)
      val (finalStream, _, _) = executors.tail.foldLeft((initStream, initExec.duration, initExec.totalRequests)) {
        case ((stream, offsetDur, offsetReq), exec @ ExecutionStep(pipe, dur)) =>
          val out = stream.merge(in.drop(offsetReq).delayBy(offsetDur).take(exec.totalRequests).through(pipe))
          (out, offsetDur + dur, offsetReq + exec.totalRequests)
      }
      finalStream
    }
}
