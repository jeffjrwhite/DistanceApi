package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.implicits._
import com.ynap.dpetapi.endpoints.definitions.HelloResponse
import com.ynap.dpetapi.endpoints.hello.{HelloHandler, GetHelloResponse}

class HelloHandlerImpl[F[_] : Applicative]() extends HelloHandler[F] {
  override def getHello(respond: GetHelloResponse.type)(name: Option[String] = None): F[GetHelloResponse] = {
    for {
      message <- s"Hello, ${name.getOrElse("world")}".pure[F]
    } yield respond.Ok(HelloResponse(message))
  }
}