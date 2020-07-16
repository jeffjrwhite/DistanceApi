package com.ynap.dpetapi

import cats.effect.IO
import io.circe.config.parser
import io.circe.generic.auto._

case class ServerConfig(port: Int, host: String)

case class DbConfig(url: String, driver: String, username: String, password: String, poolSize: Int)

case class Config(serverConfig: ServerConfig, dbConfig: DbConfig)

object Config {

  def load(): IO[Config] = {
    for {
      dbConf <- parser.decodePathF[IO, DbConfig]("db")
      serverConf <- parser.decodePathF[IO, ServerConfig]("server")
    } yield Config(serverConf, dbConf)
  }
}