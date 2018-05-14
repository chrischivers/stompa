package stompa

import cats.Monad
import net.ser1.stomp.Client

trait StompClient[F[_]] {

  def subscribe(topic: Topic, handler: Handler[F]): F[Unit]

  def unsubscribe(topic: Topic, handler: Handler[F]): F[Unit]

  def disconnect(): F[Unit]

  def isClosed(): F[Boolean]

}

object StompClient {
  def apply[F[_]: Monad](config: StompConfig) = new StompClient[F] {

    private val client = new Client(config.host, config.port, config.username, config.password)

    override def subscribe(topic: Topic, handler: Handler[F]): F[Unit] =
      Monad[F].pure(client.subscribe(topic.value, handler))

    override def unsubscribe(topic: Topic, handler: Handler[F]): F[Unit] =
      Monad[F].pure(client.unsubscribe(topic.value, handler))

    override def disconnect(): F[Unit] =
      Monad[F].pure(client.disconnect())

    override def isClosed(): F[Boolean] = Monad[F].pure(client.isClosed)
  }
}
