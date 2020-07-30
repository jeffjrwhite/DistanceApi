package com.ynap.dpetapi

import cats.effect.IO
import io.circe.config.parser
import io.circe.generic.auto._

case class ServerConfig(port: Int, host: String)

case class DbConfig(url: String,
                    driver: String,
                    username: String,
                    password: String,
                    poolSize: Int,
                    connectionTimeout: Long)

case class Config(serverConfig: ServerConfig, dbConfig: DbConfig)

object Config {

  def load(databaseName: String): IO[Config] = {
    for {
      dbConf <- parser.decodePathF[IO, DbConfig](s"db.$databaseName")
      serverConf <- parser.decodePathF[IO, ServerConfig]("server")
    } yield Config(serverConf, dbConf)
  }
}