package com.ynap.dpetapi

import cats.effect.IO
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor

class Database() {

  def transactor(dbConfig: DbConfig): IO[HikariTransactor[IO]] = {
    // hikari config
    val config = new HikariConfig()
    config.setJdbcUrl(dbConfig.url)
    config.setUsername(dbConfig.username)
    config.setPassword(dbConfig.password)
    config.setDriverClassName(dbConfig.driver)
    config.setMaximumPoolSize(dbConfig.poolSize)
    config.setConnectionTimeout(dbConfig.connectionTimeout)
    config.setMaxLifetime(100000)
    config.setValidationTimeout(100000)

    // transactor with config
    val transactor: IO[HikariTransactor[IO]] =
      IO.pure(HikariTransactor.apply[IO](new HikariDataSource(config)))
    transactor
  }
}