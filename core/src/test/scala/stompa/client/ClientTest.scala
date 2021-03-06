package stompa.client

import cats.effect.Sync
import cats.effect.concurrent.Ref
import io.circe.{Decoder, Encoder}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.Matchers._
import org.scalatest.{Assertion, FlatSpec, OptionValues}
import stompa.TestFeatures._
import cats.syntax.functor._
import cats.syntax.flatMap._
import stompa.{Handler, MessageQueue}

abstract class ClientTest[F[_]: Sync] extends FlatSpec with TypeCheckedTripleEquals with OptionValues {

  def evaluate[A]: F[A] => A
  implicit val evaluateUnit      = evaluate[Unit]
  implicit val evaluateAssertion = evaluate[Assertion]

  def messageQueue[T]: F[MessageQueue[F, T]]
  def queueToHandler[T](implicit decoder: Decoder[T]): MessageQueue[F, T] => Handler

  it should "subscribe to a topic and receive notifications for that topic (string type)" in {

    val topic   = Any.topic()
    val message = Any.alpha()

    withClient[F, String] { client =>
      for {
        queue              <- messageQueue[String]
        handler            <- Sync[F].delay(queueToHandler[String].apply(queue))
        _                  <- client.subscribe(topic, handler)
        _                  <- client.publishMessage(topic, message)
        updatedMessageList <- queue.dequeueAll
      } yield {
        updatedMessageList should have size 1
        updatedMessageList.head should ===(message)
      }
    }
  }

  it should "subscribe to a topic and receive notifications for that topic (encoded case class type)" in {

    import io.circe.generic.semiauto._
    case class TestType(value: Int)
    implicit val decoder: Decoder[TestType] = deriveDecoder[TestType]
    implicit val encoder: Encoder[TestType] = deriveEncoder[TestType]

    val topic   = Any.topic()
    val message = TestType(Any.int())

    withClient[F, TestType] { client =>
      for {
        queue              <- messageQueue[TestType]
        handler            <- Sync[F].delay(queueToHandler[TestType].apply(queue))
        _                  <- client.subscribe(topic, handler)
        _                  <- client.publishMessage(topic, message)
        updatedMessageList <- queue.dequeueAll
      } yield {
        updatedMessageList should have size 1
        updatedMessageList.head should ===(message)
      }
    }
  }

  it should "not receive notifications for topics not subscribed to" in {

    withClient[F, String] { client =>
      for {
        queue              <- messageQueue[String]
        handler            <- Sync[F].delay(queueToHandler[String].apply(queue))
        _                  <- client.subscribe(Any.topic(), handler)
        _                  <- client.publishMessage(Any.topic(), Any.alpha())
        updatedMessageList <- queue.dequeueAll
      } yield {
        updatedMessageList should have size 0
      }
    }
  }

  it should "disconnect" in {

    val topic = Any.topic()

    withClient[F, String] { client =>
      for {
        queue              <- messageQueue[String]
        handler            <- Sync[F].delay(queueToHandler[String].apply(queue))
        _                  <- client.subscribe(topic, handler)
        _                  <- client.publishMessage(topic, Any.alpha())
        _                  <- client.disconnect()
        isClosed           <- client.isClosed()
        _                  <- Sync[F].delay(isClosed shouldBe true)
        _                  <- client.publishMessage(Any.topic(), Any.alpha())
        updatedMessageList <- queue.dequeueAll
      } yield {
        updatedMessageList should have size 1
      }
    }
  }

}
