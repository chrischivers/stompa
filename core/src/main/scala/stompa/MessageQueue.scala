package stompa
import cats.effect.Sync

abstract class MessageQueue[F[_]: Sync, T] {
  def enqueue(t: T): F[Unit]
  def dequeue: F[Option[T]]
  def dequeueAll: F[List[T]]
}
