package com.ynap.dpetapi.database

import com.zaxxer.hikari.pool.HikariPool
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

case class DpetDataSource(
                  connectionString: String,
                  driver: String,
                  username: String,
                  password: String
                  ) {
}

object HikariCpDataSourceFactory {

  lazy val activePools: scala.collection.mutable.Map[DpetDataSource, HikariDataSource] = scala.collection.mutable.Map()

  def getDataSource(
                     connectionString: String,
                     driver: String,
                     username: String,
                     password: String
                   ): HikariDataSource = {

    val dataSource: HikariDataSource = DpetDataSource(
      connectionString,
      driver,
      username,
      password
    ) match {
      case ds if ds.connectionString.isEmpty =>
        throw new RuntimeException(s"No connection string was found.")
      case ds if HikariCpDataSourceFactory.activePools.contains(ds) =>
        println(s"Hikari Connection Pool Datasource already exists: $ds")
        HikariCpDataSourceFactory.activePools.get(ds).get
      case ds =>
        println(s"Create new Hikari Connection Pool Datasource for $ds")
        Class.forName(driver)
        val config = new HikariConfig()
        config.setJdbcUrl(connectionString)
        if (ds.connectionString.split(";").exists(p => p.equalsIgnoreCase("integratedSecurity=true")))
          DatabaseClient.enableIntegratedSecurity
        config.setUsername(username)
        config.setPassword(password)
        config.setDriverClassName(driver)
        config.setMaximumPoolSize(100)
        config.setPoolName(s"DPETAPI-$connectionString")
        config.setMinimumIdle(2)
        config.setConnectionTimeout(50000)
        val hikariPool = new HikariPool(config)
        println("The hikariPool count is ::" + hikariPool.getActiveConnections());
        val newDS = DpetDataSource(connectionString, driver, username, password)
        val newds = new HikariDataSource(config)
        HikariCpDataSourceFactory.activePools += (newDS -> newds)
        newds
    }
    println(s"Hikari Connection Pool Datasource : $dataSource")
    println(s"Hikari Connection Pool Name : ${dataSource.getPoolName}")
    dataSource
  }

}