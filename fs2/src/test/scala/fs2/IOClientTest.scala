package fs2

import cats.effect.IO
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.Matchers._
import org.scalatest.{Assertion, FlatSpec, OptionValues}
import stompa.TestFeatures.{Any, withClient}
import stompa.fake.StubStompClient
import stompa.{Message, StompClient}
import stompa.support.IOSupport._

class IOClientTest extends FlatSpec with TypeCheckedTripleEquals with OptionValues {

  def withIOClient(f: StubStompClient[IO] with StompClient[IO] => IO[Assertion])(
      implicit evaluate: IO[Assertion] => Assertion) = withClient[IO](f)

  it should "subscribe to a topic and receive notifications for that topic (using fs2 handler)" in {

    import scala.concurrent.ExecutionContext.Implicits.global

    val topic   = Any.topic()
    val message = Any.message()

    withIOClient { client =>
      for {
        queue           <- fs2.async.mutable.Queue.unbounded[IO, Message]
        handler         <- IO(Fs2MessageHandler[IO](queue))
        _               <- client.subscribe(topic, handler)
        _               <- client.publishMessage(topic, message)
        dequeuedMessage <- queue.dequeue1
      } yield {
        dequeuedMessage shouldBe message
      }
    }
  }
}
