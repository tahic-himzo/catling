package exec

import cats.data.NonEmptyList
import cats.effect.{Concurrent, IO, Timer}
import fs2.Pipe
import http.{Request, TimedResponse}

abstract class Executor[T, S] extends Pipe[IO, Request[T], TimedResponse[S]]

object Executor {

  def from[T, S](executors: NonEmptyList[ExecutionStep[T, S]])(
      implicit t:           Timer[IO],
      c:                    Concurrent[IO]): Pipe[IO, Request[T], TimedResponse[S]] =
    in => {
      val initStream = in.through(executors.head.executor).interruptAfter(executors.head.duration)
      val (finalStream, _) = executors.tail.foldLeft((initStream, executors.head.duration)) {
        case ((stream, offset), ExecutionStep(pipe, dur)) =>
          val out = stream.merge(in.delayBy(offset).through(pipe).interruptAfter(offset + dur))
          (out, offset + dur)
      }
      finalStream
    }

  def fromAlt[T, S](executors: NonEmptyList[ExecutionStep[T, S]])(
      implicit t:              Timer[IO],
      c:                       Concurrent[IO]): Pipe[IO, Request[T], TimedResponse[S]] =
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
