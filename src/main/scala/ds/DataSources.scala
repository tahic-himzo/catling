package ds
import cats.effect.IO
import fs2.Stream

object DataSources {
  def const[T](dummy: T): Stream[IO, T] = Stream(dummy).repeat
}
