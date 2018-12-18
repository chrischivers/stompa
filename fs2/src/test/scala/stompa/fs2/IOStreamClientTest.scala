package stompa.fs2

import cats.effect.{ContextShift, IO}
import com.typesafe.scalalogging.StrictLogging
import io.circe.Decoder
import stompa.client.ClientTest
import stompa.{Handler, MessageQueue}

import scala.concurrent.ExecutionContext

class IOStreamClientTest extends ClientTest[IO] with StrictLogging {

  implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  override def evaluate[A]: IO[A] => A = _.unsafeRunSync()
  override def messageQueue[T]: IO[MessageQueue[IO, T]] = fs2.concurrent.Queue.unbounded[IO, T].map { queue =>
    StreamMessageQueue(queue)
  }
  override def queueToHandler[T](implicit decoder: Decoder[T]): MessageQueue[IO, T] => Handler =
    queue => Handler[IO, T](queue, err => IO(logger.error(s"Error handling message: $err")))
}
