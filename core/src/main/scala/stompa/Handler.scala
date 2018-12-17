package stompa

import java.util

import com.typesafe.scalalogging.StrictLogging
import io.circe.Decoder
import io.circe.parser._
import net.ser1.stomp.{Listener => StompListener}
import cats.syntax.functor._, cats.syntax.flatMap._

trait Handler extends StompListener with StrictLogging

object Handler {
  def apply[F[_], T, E](onSuccess: T => F[Unit], onFailure: io.circe.Error => F[Unit])(implicit decoder: Decoder[T],
                                                                                       evaluate: F[Unit] => Unit) =
    new Handler {
      override def message(headers: util.Map[_, _], body: String): Unit =
        evaluate {
          parse(body)
            .flatMap(json => decoder(json.hcursor))
            .fold(onFailure, onSuccess)
        }
    }
}
