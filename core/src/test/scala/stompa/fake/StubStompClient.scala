package stompa.fake

import java.util
import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.syntax.flatMap._
import io.circe.Encoder
import stompa.{Handler, StompClient, Topic}
import cats.instances.list._
import cats.syntax.functor._
import cats.syntax.traverse._

import scala.collection.JavaConverters._

class StubStompClient[F[_]: Sync, T](subscribed: Ref[F, Set[(Topic, Handler)]], closed: Ref[F, Boolean])(
    implicit encoder: Encoder[T])
    extends StompClient[F] {
  def publishMessage(publishedTopic: Topic, message: T): F[Unit] = {
    for {
      closedValue <- isClosed()
      _ <- if (closedValue) Sync[F].unit
      else
        subscribed.get.flatMap {
          _.filter { case (topic, _) => publishedTopic == topic }.toList.traverse[F, Unit] {
            case (_, handler) => Sync[F].delay((handler.message _).tupled(unwrapMessage(message)))
          }
        }
    } yield ()
  }
  override def subscribe(topic: stompa.Topic, handler: Handler): F[Unit] =
    subscribed.update(_ + ((topic, handler)))

  override def unsubscribe(topic: stompa.Topic, handler: Handler): F[Unit] =
    subscribed.update(_ - ((topic, handler)))

  override def disconnect: F[Unit] = closed.set(true)

  private def unwrapMessage(message: T): (util.Map[String, String], String) =
    (Map.empty[String, String].asJava, encoder(message).noSpaces)

  override def isClosed(): F[Boolean] = closed.get
}

object StubStompClient {
  def apply[F[_]: Sync, T](subscribed: Ref[F, Set[(Topic, Handler)]], closed: Ref[F, Boolean])(
      implicit encoder: Encoder[T]): StubStompClient[F, T] = new StubStompClient(subscribed, closed)
}
