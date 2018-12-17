package stompa.client
import cats.effect.IO

class IOClientTest extends ClientTest[IO] {
  override def evaluate[A]: IO[A] => A = _.unsafeRunSync()
}
