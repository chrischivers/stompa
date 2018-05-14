package stompa
import cats.effect.IO

class IOClientTest extends Client[IO] {
  override implicit def evaluate[T]: IO[T] => T = _.unsafeRunSync()
}
