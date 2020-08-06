package com.ynap.dpetapi

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import com.ynap.dpetapi.endpoints.divisions.DivisionsResource
import com.ynap.dpetapi.endpoints.{DivisionsHandlerImpl, ExampleHandlerImpl, WcsInventoryHandlerImpl, WmsInventoryHandlerImpl}
import com.ynap.dpetapi.endpoints.example.ExampleResource
import com.ynap.dpetapi.endpoints.wmsInventory.WmsInventoryResource
import com.ynap.dpetapi.endpoints.wcsInventory.WcsInventoryResource
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object DpetapiServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {

    val host = AppConfig.getConfigOrElseDefault("server.host", "localhost")
    val port = AppConfig.getConfigOrElseDefault("server.port", "8761").toInt
    for {
      client <- BlazeClientBuilder[F](global).stream
      httpApp = (
        //-------------------------------------------
        // Add all route handler implementations here
          new ExampleResource().routes(new ExampleHandlerImpl())
            <+> new DivisionsResource().routes(new DivisionsHandlerImpl())
            <+> new WmsInventoryResource().routes(new WmsInventoryHandlerImpl())
            <+> new WcsInventoryResource().routes(new WcsInventoryHandlerImpl())
        // ------------------------------------------
        ).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(port, host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
