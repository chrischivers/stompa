package support

import cats.{CoflatMap, Monad, MonadError}

import scala.concurrent.{ExecutionContext, Future}

object FutureSupport {
  import cats.instances.future._

  implicit val executionContext = ExecutionContext.Implicits.global //todo change

  implicit val futureMonad = catsStdInstancesForFuture
}
