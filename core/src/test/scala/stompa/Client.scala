package stompa

import cats.Monad
import cats.effect.Sync
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.Matchers._
import org.scalatest.{FlatSpec, OptionValues}
import stompa.TestFeatures._

import scala.collection.mutable.ListBuffer
import cats.syntax.flatMap._
import cats.syntax.functor._

abstract class Client[F[_]: Monad] extends FlatSpec with TypeCheckedTripleEquals with OptionValues {

  implicit def evaluate[T]: F[T] => T

  private def lift[T](t: T) = Monad[F].pure(t)

  it should "subscribe to a topic and receive notifications for that topic" in {

    val topic       = Any.topic()
    val message     = Any.message()
    val messageList = ListBuffer[Message]()

    withClient[F] { client =>
      for {
        handler <- lift(ListBufferMessageHandler[F](messageList))
        _       <- client.subscribe(topic, handler)
        _       <- client.publishMessage(topic, message)
      } yield {
        messageList should have size 1
        messageList.head should ===(message)
      }
    }
  }

  it should "not receive notifications for topics not subscribed to" in {

    val messageList = ListBuffer[Message]()

    withClient[F] { client =>
      for {
        handler <- lift(ListBufferMessageHandler[F](messageList))
        _       <- client.subscribe(Any.topic(), handler)
        _       <- client.publishMessage(Any.topic(), Any.message())
      } yield {
        messageList should have size 0
      }
    }
  }

  it should "disconnect" in {

    val messageList = ListBuffer[Message]()
    val topic       = Any.topic()

    withClient[F] { client =>
      for {
        handler  <- lift(ListBufferMessageHandler[F](messageList))
        _        <- client.subscribe(topic, handler)
        _        <- client.publishMessage(topic, Any.message())
        _        <- client.disconnect()
        isClosed <- client.isClosed()
        _        <- lift { messageList should have size 1 }
        _        <- lift(isClosed shouldBe true)
        _        <- client.publishMessage(Any.topic(), Any.message())
      } yield {
        messageList should have size 1
      }
    }
  }

}
