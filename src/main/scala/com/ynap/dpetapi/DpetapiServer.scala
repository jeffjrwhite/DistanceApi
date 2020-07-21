package com.ynap.dpetapi

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import com.ynap.dpetapi.endpoints.divisions.DivisionsResource
import com.ynap.dpetapi.endpoints.{DivisionsHandlerImpl, FarewellHandlerImpl, HelloHandlerImpl}
import com.ynap.dpetapi.endpoints.farewell.FarewellResource
import com.ynap.dpetapi.endpoints.hello.HelloResource
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object DpetapiServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    //val serverConfig: ServerConfig
    val svr = for {
      config <- Stream.eval(Config.load())
      xa <- Stream.eval(Database.transactor(config.dbConfig))
    } yield {
      (config.serverConfig, xa)
    }
    val serverConfig = svr.drain
    for {
      client <- BlazeClientBuilder[F](global).stream
      //config <- Stream.eval(Config.load())
      //xa <- Stream.eval(Database.transactor(config.dbConfig))
      httpApp = (
          new HelloResource().routes(new HelloHandlerImpl())
            <+> new FarewellResource().routes(new FarewellHandlerImpl())
            <+> new DivisionsResource().routes(new DivisionsHandlerImpl())
        ).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0") //.bindHttp(serverConfig.port, serverConfig.host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
