package stompa

import support.FutureSupport._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class FutureClientTest extends Client[Future] {

  override implicit def evaluate[T]: Future[T] => T = Await.result(_, 10.seconds)

}
