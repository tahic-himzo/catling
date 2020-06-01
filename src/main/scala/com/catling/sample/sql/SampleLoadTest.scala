package com.catling.sample.sql

import cats.NonEmptyParallel
import cats.effect.{ContextShift, IO, Timer}
import com.catling.library.evaluation.Evaluators
import com.catling.library.evaluation.latency.Latency
import com.catling.library.evaluation.statuscode.model.StatusCodesResult
import com.catling.library.http.FakeHttpClient
import com.catling.library.LoadTest
import com.catling.sample.SampleExecutor
import fs2.Stream

object SampleLoadTest {

  def get(implicit t: Timer[IO], ec: ContextShift[IO], nep: NonEmptyParallel[IO]): Stream[IO, (StatusCodesResult, Latency)] = {
    val ds   = new SampleSqlDataSource(10)
    val exec = SampleExecutor.get(new FakeHttpClient)
    val eval = Evaluators.default[String]
    LoadTest.from(ds.get, exec, eval)
  }

}
