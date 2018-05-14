package stompa

import java.util
import cats.Monad
import cats.syntax.functor._
import com.typesafe.scalalogging.StrictLogging
import net.ser1.stomp.Listener

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.util.Try

trait Handler[F[_]] extends Listener with StrictLogging

object BasicMessageHandler {
  def apply[F[_]](action: Message => F[Unit])(implicit evaluate: F[Unit] => Unit) =
    new Handler[F] {
      override def message(headers: util.Map[_, _], body: String): Unit =
        Try(headers.asInstanceOf[util.Map[String, String]].asScala.toMap)
          .map(stringHeaders => evaluate(action(Message(stringHeaders, body))))
          .getOrElse(logger.error(s"Unable to handle message [$headers, $body]"))
    }
}

object ListBufferMessageHandler {
  def apply[F[_]: Monad](messageList: ListBuffer[Message])(implicit evaluate: F[Unit] => Unit) =
    BasicMessageHandler(m => Monad[F].pure(messageList += m).void)
}
