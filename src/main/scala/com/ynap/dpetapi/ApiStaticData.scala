package com.ynap.dpetapi

import cats.Applicative
import cats.implicits._
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait ApiStaticData[F[_]]{
    def hello(n: ApiStaticData.Name): F[ApiStaticData.Status]
  }

  object ApiStaticData {
    implicit def apply[F[_]](implicit ev: ApiStaticData[F]): ApiStaticData[F] = ev

    final case class Name(name: String) extends AnyVal
    /**
      * More generally you will want to decouple your edge representations from
      * your internal data structures, however this shows how you can
      * create encoders for your data.
      **/
    final case class Status(greeting: String) extends AnyVal
    object Status {
      implicit val greetingEncoder: Encoder[Status] = new Encoder[Status] {
        final def apply(a: Status): Json = Json.obj(
          ("message", Json.fromString(a.greeting)),
        )
      }
      implicit def greetingEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Status] =
        jsonEncoderOf[F, Status]
    }

    def impl[F[_]: Applicative]: ApiStaticData[F] = new ApiStaticData[F]{
      def hello(n: ApiStaticData.Name): F[ApiStaticData.Status] =
        Status("ApiStaticData, " + n.name).pure[F]
    }

}
