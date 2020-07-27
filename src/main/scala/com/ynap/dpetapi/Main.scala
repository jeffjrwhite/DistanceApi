package com.ynap.dpetapi

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import doobie.util.transactor.Transactor
import fs2.Stream
import cats.implicits._
import com.ynap.dpetapi.endpoints.DivisionsHandlerImpl
import org.http4s.server.Router
import org.http4s.{Request, Response}
import org.http4s.server.blaze.BlazeServerBuilder

object Main extends IOApp {

//  def serveStream(transactor: Transactor[IO], serverConfig: ServerConfig): Stream[IO, ExitCode] = {
//
//    def makeRouter(transactor: Transactor[IO]): Kleisli[IO, Request[IO], Response[IO]] = {
//      Router[IO](
//        "/api/v1" -> DivisionsRoutes.routes(new DivisionsHandlerImpl(transactor))
//      ).orNotFound
//    }
//
//    BlazeServerBuilder[IO]
//      .bindHttp(serverConfig.port, serverConfig.host)
//      .withHttpApp(makeRouter(transactor))
//      .serve
//  }

  override def run(args: List[String]) =
    DpetapiServer.stream[IO].compile.drain.as(ExitCode.Success)

//    val stream = for {
//      config <- Stream.eval(Config.load())
//      xa <- Stream.eval(Database.transactor(config.dbConfig))
//      //_ <- Stream.eval(Database.bootstrap(xa))
//      exitCode <- serveStream(xa, config.serverConfig)
//    } yield exitCode
//
//    stream.compile.drain.as(ExitCode.Success)
}