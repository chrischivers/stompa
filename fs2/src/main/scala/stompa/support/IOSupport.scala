//package stompa.support
//
//import cats.effect.IO
//
//object IOSupport {
//
//  implicit def IOEvaluate[T]: IO[T] => T = _.unsafeRunSync()
//}
