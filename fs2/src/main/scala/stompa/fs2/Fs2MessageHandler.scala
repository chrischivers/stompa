//package stompa.fs2
//
//import cats.effect.Effect
//import fs2.async.mutable.Queue
//import stompa.{BasicMessageHandler, Handler, Message}
//import scala.concurrent.ExecutionContext
//
//object Fs2MessageHandler {
//  def apply[F[_]: Effect](queue: Queue[F, Message])(implicit evaluate: F[Unit] => Unit,
//                                                    executionContext: ExecutionContext): Handler[F] =
//    BasicMessageHandler(queue.enqueue1)
//}
