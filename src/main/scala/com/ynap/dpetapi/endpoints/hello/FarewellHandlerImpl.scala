package com.ynap.dpetapi.endpoints.hello

import cats.Applicative
import cats.implicits._
import com.ynap.dpetapi.endpoints.definitions.{FarewellResponse}
import com.ynap.dpetapi.endpoints.farewell.{FarewellHandler, GetFarewellResponse}

class FarewellHandlerImpl[F[_] : Applicative]() extends FarewellHandler[F] {
  override def getFarewell(respond: GetFarewellResponse.type)(): F[GetFarewellResponse] = {
    for {
      message <- "Farewell, world".pure[F]
    } yield respond.Ok(FarewellResponse(message))
  }
}