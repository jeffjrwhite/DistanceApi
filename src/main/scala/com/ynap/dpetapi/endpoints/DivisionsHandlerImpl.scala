package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.effect.IO
import cats.implicits._
import com.ynap.dpetapi.endpoints.divisions.{DivisionsHandler, GetDivisionsResponse}
import com.ynap.dpetapi.endpoints.definitions.DivisionsResponse
import com.ynap.dpetapi._
import com.ynap.dpetapi.endpoints.divisions.GetDivisionsResponse.Ok
import doobie.util.transactor.Transactor

// account model
case class Division(ID_Divisione: String, Descrizione: String, timestamp: Long)

class DivisionsHandlerImpl[F[_] : Applicative](xa: Transactor[IO]) extends DivisionsHandler[F] {
  override def getDivisions(respond: GetDivisionsResponse.type)(): F[GetDivisionsResponse] = {
    for {
      //message <- s"Divisions...".pure[F]
      message <-  DivisionQuery.search().to[List]
    } yield respond.Ok(DivisionsResponse(message))
  }

//  override def getDivision(id: String) = {
//    DivisionQuery.searchWithId(id).option.transact(xa)
//  }
//
//  override def getDivisions() = {
//    DivisionQuery.search().to[List].transact(xa)
//  }

}