package com.catling.library.execution

import com.catling.library.execution.constant.ConstantRpsExecutor
import io.circe.Encoder

import scala.concurrent.duration.FiniteDuration

case class ExecutionStep[T: Encoder, S](executor: Executor[T, S], duration: FiniteDuration) {

  val totalRequests: Long = executor match {
    case constantRps: ConstantRpsExecutor[T] => constantRps.rps * duration.toSeconds
  }
}
