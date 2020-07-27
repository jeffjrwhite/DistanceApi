package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.implicits._
import com.ynap.dpetapi.endpoints.definitions.ExampleResponse
import com.ynap.dpetapi.endpoints.example.{ExampleHandler, GetExampleResponse}

class ExampleHandlerImpl[F[_] : Applicative]() extends ExampleHandler[F] {
  override def getExample(respond: GetExampleResponse.type)(forename: String, surname: String): F[GetExampleResponse] = {
    for {
      message <- s"Example, ${forename} $surname".pure[F]
    } yield respond.Ok(ExampleResponse(message))
  }
}