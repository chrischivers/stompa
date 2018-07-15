package stompa.monix

import monix.reactive.Observer
import stompa.{BasicMessageHandler, Message}

import scala.concurrent.{ExecutionContext, Future}

object MonixMessageHandler {
  def apply(observer: Observer[Message])(implicit evaluate: Future[Unit] => Unit,
                                         executionContext: ExecutionContext) = {
    BasicMessageHandler[Future](msg => observer.onNext(msg).map(_ => ()))
  }
}
