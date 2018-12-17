package stompa.fake
import cats.effect.Sync
import cats.effect.concurrent.Ref
import com.typesafe.scalalogging.StrictLogging
import io.circe.Decoder
import stompa.Handler

object ListRefHandler extends StrictLogging {
  def apply[F[_]: Sync, T, E](list: Ref[F, List[T]])(implicit decoder: Decoder[T], evaluate: F[Unit] => Unit) = {
    Handler.apply[F, T, E](msg => list.update(_ :+ msg),
                           err => Sync[F].delay(logger.error(s"Error handling message: $err")))
  }
}
