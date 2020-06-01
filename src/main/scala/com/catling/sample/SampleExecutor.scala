package com.catling.sample

import cats.data.NonEmptyList
import cats.effect.{ContextShift, IO, Timer}
import com.catling.execution.constant.ConstantRpsExecutor
import com.catling.execution.{ExecutionStep, Executor, Executors}
import com.catling.http.HttpClient

import scala.concurrent.duration._

object SampleExecutor {

  def get(httpClient: HttpClient)(implicit cs: ContextShift[IO], t: Timer[IO]): Executor[SamplePayload, String] = Executors.from(
    NonEmptyList.of(
      ExecutionStep(new ConstantRpsExecutor(httpClient, 10), 10.seconds),
      ExecutionStep(new ConstantRpsExecutor(httpClient, 15), 10.seconds)
    )
  )
}
