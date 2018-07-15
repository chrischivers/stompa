//package monix
//
//import cats.effect.IO
//import monix.execution.Ack
//import monix.execution.Ack.Continue
//import monix.reactive.Observer
//import monix.reactive.observers.{ConnectableSubscriber, Subscriber}
//import org.scalactic.TypeCheckedTripleEquals
//import org.scalatest.Matchers._
//import org.scalatest.{Assertion, FlatSpec, OptionValues}
//import stompa.TestFeatures.{Any, withClient}
//import stompa.fake.StubStompClient
//import stompa.monix.MonixMessageHandler
//import stompa.{Message, StompClient, TestFeatures}
//import monix.execution.Scheduler.{global => scheduler}
//
//import scala.concurrent.{Await, Future}
//
//class ClientTest extends FlatSpec with TypeCheckedTripleEquals with OptionValues {
//
//  override implicit def evaluate[T]: Future[T] => T = Await.result(_, 10.seconds)
//
//  def withFutureClient = TestFeatures.withClient[Future]
//
//  it should "subscribe to a topic and receive notifications for that topic (using fs2 handler)" in {
//
//    import scala.concurrent.ExecutionContext.Implicits.global
//
//    val topic   = Any.topic()
//    val message = Any.message()
//
//    val observer = new Observer[Message] {
//      def onNext(msg: Message): Future[Ack] = {
//        Continue
//      }
//      def onError(ex: Throwable): Unit =
//        ex.printStackTrace()
//      def onComplete(): Unit =
//        println("O completed")
//    }
//
//    withFutureClient { client =>
//      for {
//        handler         <- IO(MonixMessageHandler(observer))
//        subscriber = Subscriber(observer, scheduler)
//        _               <- client.subscribe(topic, handler)
//        _               <- client.publishMessage(topic, message)
//        _ <- subscriber.scheduler.
//      } yield {
//        dequeuedMessage shouldBe message
//      }
//    }
//  }
//}
