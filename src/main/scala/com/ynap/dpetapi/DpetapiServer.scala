package com.ynap.dpetapi

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import com.ynap.dpetapi.endpoints.farewell.FarewellResource
import com.ynap.dpetapi.endpoints.hello.{FarewellHandlerImpl, HelloHandlerImpl, HelloResource}
import fs2.Stream
import org.http4s.HttpRoutes
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object DpetapiServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      httpApp = (
          new HelloResource().routes(new HelloHandlerImpl())
            <+> new FarewellResource().routes(new FarewellHandlerImpl())

        ).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
