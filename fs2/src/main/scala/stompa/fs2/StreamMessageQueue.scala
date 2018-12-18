package stompa.fs2

import cats.effect.Sync
import fs2.concurrent.Queue
import stompa.MessageQueue

object StreamMessageQueue {
  def apply[F[_]: Sync, T](queue: Queue[F, T]) =
    new MessageQueue[F, T] {
      override def enqueue(t: T): F[Unit] = queue.enqueue1(t)
      override def dequeue: F[Option[T]]  = queue.tryDequeue1
      override def dequeueAll: F[List[T]] = queue.dequeueChunk(Integer.MAX_VALUE).compile.toList
    }
}
