package stompa

import cats.effect.Sync
import cats.effect.concurrent.Ref
import io.circe.Encoder
import org.scalatest.Assertion
import stompa.fake.StubStompClient

import scala.util.Random

object TestFeatures {

  object Any {
    def alpha(): String = Random.alphanumeric.filter(_.isLetter).take(30).mkString("")
    def int(): Int      = Random.nextInt()
    def topic()         = Topic(alpha())
  }

  def withClient[F[_]: Sync, T](f: StubStompClient[F, T] with StompClient[F] => F[Assertion])(
      implicit evaluate: F[Assertion] => Assertion,
      encoder: Encoder[T]): Assertion = {

    import cats.syntax.flatMap._
    import cats.syntax.functor._

    evaluate(for {
      subscribed <- Ref.of[F, Set[(Topic, Handler)]](Set.empty)
      closed     <- Ref.of[F, Boolean](false)
      client = StubStompClient[F, T](subscribed, closed)
      result <- f(client)
    } yield result)
  }
}
