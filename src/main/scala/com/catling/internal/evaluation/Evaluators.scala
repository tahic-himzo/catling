package com.catling.internal.evaluation

import cats.effect.IO
import fs2.{Chunk, Stream}
import com.catling.internal.evaluation.latency.{Latency, LatencyEvaluator}
import com.catling.internal.evaluation.statuscode.StatusCodeEvaluator
import com.catling.internal.evaluation.statuscode.model.StatusCodesResult
import com.catling.internal.http.TimedResponse
import com.catling.loadtest.Evaluator

object Evaluators {

  def latency: Evaluator[Latency] = LatencyEvaluator

  def responseCodes: Evaluator[StatusCodesResult] = new StatusCodeEvaluator

  def default: Evaluator[(StatusCodesResult, Latency)] = eval2(responseCodes, latency)

  def eval2[A, B](a: Evaluator[A], b: Evaluator[B]): Evaluator[(A, B)] = new Evaluator[(A, B)] {
    override val empty: (A, B) = (a.empty, b.empty)

    override def apply(in: Stream[IO, Chunk[TimedResponse[String]]]): Stream[IO, (A, B)] =
      in.through(a).zipAll(in.through(b))(a.empty, b.empty)
  }

  def eval3[A, B, C](a: Evaluator[A], b: Evaluator[B], c: Evaluator[C]): Evaluator[(A, B, C)] = new Evaluator[(A, B, C)] {
    override val empty: (A, B, C) = (a.empty, b.empty, c.empty)

    override def apply(in: Stream[IO, Chunk[TimedResponse[String]]]): Stream[IO, (A, B, C)] =
      eval2(eval2(a, b), c)(in).map {
        case ((a, b), c) => (a, b, c)
      }
  }

  def eval4[A, B, C, D](a: Evaluator[A], b: Evaluator[B], c: Evaluator[C], d: Evaluator[D]): Evaluator[(A, B, C, D)] =
    new Evaluator[(A, B, C, D)] {
      override val empty: (A, B, C, D) = (a.empty, b.empty, c.empty, d.empty)

      override def apply(in: Stream[IO, Chunk[TimedResponse[String]]]): Stream[IO, (A, B, C, D)] =
        eval2(eval3(a, b, c), d)(in).map {
          case ((a, b, c), d) => (a, b, c, d)
        }
    }

}
