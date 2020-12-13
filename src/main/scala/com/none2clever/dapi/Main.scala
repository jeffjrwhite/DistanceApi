package com.none2clever.dapi

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {

  override def run(args: List[String]) =
    DpetapiServer.stream[IO].compile.drain.as(ExitCode.Success)
}