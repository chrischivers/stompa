package stompa.fake

import java.util
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import cats.Monad
import stompa.{Handler, Message, StompClient, Topic}

import scala.collection.JavaConverters._

trait StubStompClient[F[_]] {
  def publishMessage(topic: Topic, message: Message): F[Unit]
}

object StubStompClient {
  def apply[F[_]: Monad](subscribed: AtomicReference[Set[(Topic, Handler[F])]], closed: AtomicBoolean) =
    new StubStompClient[F] with StompClient[F] {
      override def subscribe(topic: stompa.Topic, handler: Handler[F]): F[Unit] =
        Monad[F].pure(subscribed.updateAndGet(_ + ((topic, handler))))

      override def unsubscribe(topic: stompa.Topic, handler: Handler[F]): F[Unit] =
        Monad[F].pure(subscribed.updateAndGet(_ - ((topic, handler))))

      override def disconnect: F[Unit] = Monad[F].pure(closed.set(true))

      override def publishMessage(publishedTopic: Topic, message: Message): F[Unit] =
        if (closed.get) Monad[F].unit
        else
          Monad[F].pure {
            subscribed.get.filter { case (topic, _) => publishedTopic == topic }.foreach {
              case (_, handler) => (handler.message _).tupled(unwrapMessage(message))
            }
          }

      private def unwrapMessage(message: Message): (util.Map[String, String], String) =
        (message.headers.asJava, message.body)

      override def isClosed(): F[Boolean] = Monad[F].pure(closed.get)
    }
}
