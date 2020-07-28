package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.effect.IO
import cats.implicits._
import doobie.implicits._
import com.ynap.dpetapi.endpoints.divisions.{DivisionsHandler, GetDivisionsResponse}
import com.ynap.dpetapi.endpoints.definitions.DivisionsResponse
import com.ynap.dpetapi._
import doobie.util.transactor.Transactor
import io.circe.Encoder
import io.circe.syntax._

class DivisionsHandlerImpl[F[_] : Applicative](xa: Transactor[IO]) extends DivisionsHandler[F] {

  implicit val encodeFieldType: Encoder[Division] =
    Encoder.forProduct4("id", "name", "shortname", "active")(Division.unapply(_).get)

  override def getDivisions(respond: GetDivisionsResponse.type)(
    id: Option[Int],
    name: Option[String],
    pageNumber: Option[Int],
    pageSize: Option[Int]
  ): F[GetDivisionsResponse] = {

    var jsonList: List[io.circe.Json] = for {
      division <- DivisionQuery.search(id, name, pageNumber, pageSize).to[List].transact(xa).unsafeRunSync
    } yield {
      division.asJson
    }
    for {
      list <- jsonList.toIndexedSeq.pure[F]
    } yield
      respond.Ok(DivisionsResponse(Some(list.length), Some(list)))

  }

}