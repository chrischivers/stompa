package stompa

import cats.effect.Sync
import net.ser1.stomp.Client

trait StompClient[F[_]] {

  def subscribe(topic: Topic, handler: Handler): F[Unit]

  def unsubscribe(topic: Topic, handler: Handler): F[Unit]

  def disconnect(): F[Unit]

  def isClosed(): F[Boolean]

}

object StompClient {
  def apply[F[_]: Sync](config: StompConfig) = new StompClient[F] {

    private val client = new Client(config.host, config.port, config.username, config.password)

    override def subscribe(topic: Topic, handler: Handler): F[Unit] =
      Sync[F].delay(client.subscribe(topic.value, handler))

    override def unsubscribe(topic: Topic, handler: Handler): F[Unit] =
      Sync[F].delay(client.unsubscribe(topic.value, handler))

    override def disconnect(): F[Unit] =
      Sync[F].delay(client.disconnect())

    override def isClosed(): F[Boolean] = Sync[F].delay(client.isClosed)
  }
}
