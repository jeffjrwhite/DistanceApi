package com.ynap.dpetapi

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import doobie.implicits._
import doobie.util.transactor.Transactor

object Database {

  def transactor(dbConfig: DbConfig): IO[HikariTransactor[IO]] = {
    // hikari config
    val config = new HikariConfig()
    config.setJdbcUrl(dbConfig.url)
    config.setUsername(dbConfig.username)
    config.setPassword(dbConfig.password)
    config.setDriverClassName(dbConfig.driver)
    config.setMaximumPoolSize(dbConfig.poolSize)

    // transactor with config
    val transactor: IO[HikariTransactor[IO]] =
      IO.pure(HikariTransactor.apply[IO](new HikariDataSource(config)))
    transactor
  }

//  def bootstrap(xa: Transactor[IO]): IO[Int] = {
    //AccountQuery.createTable.run.transact(xa)
//  }
}