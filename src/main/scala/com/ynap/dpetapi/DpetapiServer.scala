package com.ynap.dpetapi

import cats.effect.{ConcurrentEffect, ContextShift, IO, Timer}
import cats.implicits._
import com.ynap.dpetapi.endpoints.divisions.DivisionsResource
import com.ynap.dpetapi.endpoints.{DivisionsHandlerImpl, ExampleHandlerImpl, InventoryHandlerImpl}
import com.ynap.dpetapi.endpoints.example.ExampleResource
import com.ynap.dpetapi.endpoints.inventory.InventoryResource
import doobie.hikari
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object DpetapiServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {

    val config = Config.load("viking").unsafeRunSync()
    val vikingDatabase = new Database()
    val xaVikingTransactor: hikari.HikariTransactor[IO] = vikingDatabase.transactor(config.dbConfig).unsafeRunSync()

    for {
      client <- BlazeClientBuilder[F](global).stream
      httpApp = (
        //-------------------------------------------
        // Add all route handler implementations here
          new ExampleResource().routes(new ExampleHandlerImpl())
            <+> new DivisionsResource().routes(new DivisionsHandlerImpl(xaVikingTransactor))
            <+> new InventoryResource().routes(new InventoryHandlerImpl(xaVikingTransactor))
        // ------------------------------------------
        ).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(config.serverConfig.port, config.serverConfig.host)
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
