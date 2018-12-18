package stompa.client
import cats.effect.{IO, Sync}
import cats.effect.concurrent.Ref
import com.typesafe.scalalogging.StrictLogging
import io.circe.Decoder
import stompa.fake.RefMessageQueue
import stompa.{Handler, MessageQueue}

class IORefClientTest extends ClientTest[IO] with StrictLogging {

  override def evaluate[A]: IO[A] => A                  = _.unsafeRunSync()
  override def messageQueue[T]: IO[MessageQueue[IO, T]] = Ref.of[IO, List[T]](List.empty[T]).map(RefMessageQueue(_))
  override def queueToHandler[T](implicit decoder: Decoder[T]): MessageQueue[IO, T] => Handler =
    queue => Handler[IO, T](queue, err => IO(logger.error(s"Error handling message: $err")))
}
