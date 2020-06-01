package com.catling.sample.const

import cats.NonEmptyParallel
import cats.effect.{ContextShift, IO, Timer}
import com.catling.LoadTest
import com.catling.evaluation.Evaluators
import com.catling.evaluation.latency.Latency
import com.catling.evaluation.statuscode.model.StatusCodesResult
import com.catling.http.FakeHttpClient
import com.catling.sample.SampleExecutor
import fs2.Stream

object SampleLoadTest {

  def get(implicit t: Timer[IO], ec: ContextShift[IO], nep: NonEmptyParallel[IO]): Stream[IO, (StatusCodesResult, Latency)] = {
    val ds   = new SampleConstDataSource(10)
    val exec = SampleExecutor.get(new FakeHttpClient)
    val eval = Evaluators.default[String]
    LoadTest.from(ds.get, exec, eval)
  }

}
