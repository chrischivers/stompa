package stompa

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import cats.Monad
import org.scalatest.Assertion
import stompa.fake.StubStompClient

import scala.util.Random

object TestFeatures {

  object Any {
    def alpha(): String = Random.alphanumeric.filter(_.isLetter).take(30).mkString("")

    def message(): Message = Message(Map(Any.alpha() -> Any.alpha()), alpha())

    def topic() = Topic(alpha())
  }

  def withClient[F[_]: Monad](f: StubStompClient[F] with StompClient[F] => F[Assertion])(
      implicit evaluate: F[Assertion] => Assertion): Assertion = {

    import cats.syntax.flatMap._
    import cats.syntax.functor._

    evaluate(for {
      subscribed <- Monad[F].pure(new AtomicReference[Set[(Topic, Handler[F])]](Set.empty))
      closed     <- Monad[F].pure(new AtomicBoolean(false))
      client = StubStompClient[F](subscribed, closed)
      result <- f(client)
    } yield result)
  }
}
