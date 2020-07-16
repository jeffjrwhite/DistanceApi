package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.implicits._
import com.ynap.dpetapi.endpoints.definitions.FarewellResponse
import com.ynap.dpetapi.endpoints.farewell.{FarewellHandler, GetFarewellResponse}

class FarewellHandlerImpl[F[_] : Applicative]() extends FarewellHandler[F] {
  override def getFarewell(respond: GetFarewellResponse.type)(forename: String, surname: String): F[GetFarewellResponse] = {
    for {
      message <- s"Farewell, ${forename} $surname".pure[F]
    } yield respond.Ok(FarewellResponse(message))
  }
}