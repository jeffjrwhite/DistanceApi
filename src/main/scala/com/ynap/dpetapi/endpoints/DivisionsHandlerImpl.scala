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
    Encoder.forProduct2("ID_Divisione", "Descrizione")(Division.unapply(_).get)

  override def getDivisions(respond: GetDivisionsResponse.type)(id: Option[String] = None): F[GetDivisionsResponse] = {

    var jsonList: List[io.circe.Json] = for {
      division <- DivisionQuery.search(id).to[List].transact(xa).unsafeRunSync
    } yield {
      val jsonStr = division.asJson
      jsonStr
    }
    val isJson: IndexedSeq[io.circe.Json] = for {
      a <- 0 to jsonList.length-1
    } yield {
        jsonList(a)
    }
    for {
      list <- isJson.pure[F]
    } yield respond.Ok(DivisionsResponse(Some(isJson.length), Some(list)))

  }

}