package com.ynap.dpetapi

import cats.effect.concurrent.Ref
import cats.effect.{ConcurrentEffect, ContextShift, ExitCode, IO, Timer}
import cats.implicits._
import com.ynap.dpetapi.auth.JwtTokenValidater
import com.ynap.dpetapi.endpoints.divisions.DivisionsResource
import com.ynap.dpetapi.endpoints.example.ExampleResource
import com.ynap.dpetapi.endpoints.findGtins.FindGtinsResource
import com.ynap.dpetapi.endpoints.findGtinsByStore.FindGtinsByStoreResource
import com.ynap.dpetapi.endpoints.findVariant.FindVariantResource
import com.ynap.dpetapi.endpoints.frontendDatabaseScan.FrontendDatabaseScanResource
import com.ynap.dpetapi.endpoints.validateOffSeasonGtins.ValidateOffSeasonGtinsResource
import com.ynap.dpetapi.endpoints.validateXtrackerGtins.ValidateXtrackerGtinsResource
import com.ynap.dpetapi.endpoints.wcsInventory.WcsInventoryResource
import com.ynap.dpetapi.endpoints.wmsDetails.WmsDetailsResource
import com.ynap.dpetapi.endpoints.wmsInventory.WmsInventoryResource
import com.ynap.dpetapi.endpoints.wmsPreferred.WmsPreferredResource
import com.ynap.dpetapi.endpoints._
import com.ynap.dpetapi.endpoints.createGraphenePo.CreateGraphenePoResource
import com.ynap.dpetapi.endpoints.findPriceByCountry.FindPriceByCountryResource
import com.ynap.dpetapi.endpoints.gtinAddToCart.{GtinAddToCartHandler, GtinAddToCartResource}
import com.ynap.dpetapi.endpoints.omsInventory.OmsInventoryResource
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


        new ExampleResource().routes(new ExampleHandlerImpl())
          <+> Router("/system" -> SysOpsHandler.routes(signal))
          <+> new StaticContentHandler().routes(C)

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