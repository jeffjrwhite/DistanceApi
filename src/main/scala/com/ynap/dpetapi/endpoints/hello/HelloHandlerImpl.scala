package com.ynap.dpetapi.endpoints.hello

import cats.Applicative
import cats.implicits._
import com.ynap.dpetapi.endpoints.definitions.HelloResponse

class HelloHandlerImpl[F[_] : Applicative]() extends HelloHandler[F] {
  override def getHello(respond: GetHelloResponse.type)(): F[GetHelloResponse] = {
    for {
      message <- "Hello, world".pure[F]
    } yield respond.Ok(HelloResponse(message))
  }
}