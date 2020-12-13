package com.none2clever.dapi

import cats.effect.concurrent.Ref
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, Timer}
import cats.implicits._
import com.none2clever.dapi.endpoints.{DistanceHandlerImpl, StaticContentHandler, SysOpsHandler}
import com.none2clever.dapi.endpoints.distance.DistanceResource

import fs2.Stream
import fs2.concurrent.SignallingRef
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.{Response, Status}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.global

object DpetapiServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {

    val host = AppConfig.getConfigOrElseDefault("server.host", "localhost")
    val port = AppConfig.getConfigOrElseDefault("server.port", "8761").toInt
    for {
      client <- BlazeClientBuilder[F](global).stream
      signal   <- fs2.Stream.eval(SignallingRef[F, Boolean](false))
      exitCode <- fs2.Stream.eval(Ref[F].of(ExitCode.Success))
      httpApp = (
        //-------------------------------------------
        // Add all route handler implementations here
        new DistanceResource().routes(new DistanceHandlerImpl())
          <+> Router("/system" -> SysOpsHandler.routes(signal))
          <+> new StaticContentHandler().routes(C)
        // ------------------------------------------
        ).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      server <- BlazeServerBuilder[F](global)
        .withIdleTimeout(5.minutes)
        .withResponseHeaderTimeout(5.minutes)
        .bindHttp(port, host)
        .withHttpApp(finalHttpApp)
        .withServiceErrorHandler(_ => {
          case ex: Throwable =>
            Response[F](Status.InternalServerError).withEntity(ex.getLocalizedMessage).pure[F]
        })
        .serveWhile(signal, exitCode)
    } yield server
  }.drain
}