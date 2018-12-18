package stompa.fake
import cats.effect.Sync
import cats.effect.concurrent.Ref
import com.typesafe.scalalogging.StrictLogging
import io.circe.Decoder
import stompa.{Handler, MessageQueue}
import cats.syntax.functor._
import cats.syntax.flatMap._

object RefMessageQueue {
  def apply[F[_]: Sync, T](ref: Ref[F, List[T]]) = new MessageQueue[F, T] {
    override def enqueue(t: T): F[Unit] = ref.update(_ :+ t)
    override def dequeue: F[Option[T]] =
      for {
        list <- ref.get
        h = list.headOption
        _ <- ref.update(_.tail)
      } yield h
    override def dequeueAll: F[List[T]] =
      for {
        list <- ref.get
        _    <- ref.update(_ => List.empty[T])
      } yield list
  }
}
