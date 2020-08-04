package com.ynap.dpetapi.database

import com.ynap.dpetapi.AppConfig

case class DBConfigRequest(
                            driver: String,
                            connectionString: String,
                            username: String,
                            password: String
                          )

object DBConfigRequest {

  val sqlDriver =  AppConfig.getConfigOrElseDefault("driver","com.microsoft.sqlserver.jdbc.SQLServerDriver")
  val orcDriver = AppConfig.getConfigOrElseDefault("web-commerce-database.driver","oracle.jdbc.driver.OracleDriver")

  def apply(
             driver: String,
             connectionString: String,
             username: String,
             password: String
           ): DBConfigRequest = new DBConfigRequest(driver, connectionString, username, password)

  def Fashion = DBConfigRequest(
    driver = sqlDriver,
    connectionString = AppConfig.getConfigOrElseDefault(s"back-end-sql-database.fashion.${AppConfig.myEnvironment}.url",
      "jdbc:sqlserver://viking.be.integ.yoox.net;DatabaseName=fashion;"),
    username = AppConfig.getConfigOrElseDefault(s"back-end-sql-database.fashion.${AppConfig.myEnvironment}.user.name","yooxcrmtools"),
    password = AppConfig.getConfigOrElseDefault(s"back-end-sql-database.fashion.${AppConfig.myEnvironment}.user.password","y00xCRMt00ls")
  )

  def AUTOMA = DBConfigRequest(
    driver = sqlDriver,
    connectionString = AppConfig.getConfigOrElseDefault(s"back-end-sql-database.automa.${AppConfig.myEnvironment}.url",
      "jdbc:sqlserver://viking.be.integ.yoox.net;DatabaseName=automa;"),
    username = AppConfig.getConfigOrElseDefault(s"back-end-sql-database.automa.${AppConfig.myEnvironment}.user.username","yooxcrmtools"),
    password = AppConfig.getConfigOrElseDefault(s"back-end-sql-database.automa.${AppConfig.myEnvironment}.user.password","y00xCRMt00ls")
  )

  def FrontendMultibrand = DBConfigRequest(
    driver = sqlDriver,
    connectionString = AppConfig.getConfigOrElseDefault(s"front-end-sql-database.multibrand.${AppConfig.myEnvironment}.url",
      "jdbc:sqlserver://yooxcluster.fe.integ.yoox.net;DatabaseName=Yoox;"),
    username = AppConfig.getConfigOrElseDefault(s"front-end-sql-database.multibrand.${AppConfig.myEnvironment}.user.username","yooxCustomer"),
    password = AppConfig.getConfigOrElseDefault(s"front-end-sql-database.multibrand.${AppConfig.myEnvironment}.user.password","K0nch4LOV5ky")
  )

  def FrontendMonobrand = DBConfigRequest(
    driver = sqlDriver,
    connectionString = AppConfig.getConfigOrElseDefault(s"front-end-sql-database.monobrand.${AppConfig.myEnvironment}.url",
      "jdbc:sqlserver://oscluster.fe.integ.yoox.net;DatabaseName=osdb;"),
    username = AppConfig.getConfigOrElseDefault(s"front-end-sql-database.monobrand.${AppConfig.myEnvironment}.user.username","osCustomer"),
    password = AppConfig.getConfigOrElseDefault(s"front-end-sql-database.monobrand.${AppConfig.myEnvironment}.userpassword","3WZtDfsm")
  )

  def MergedFashion = DBConfigRequest(
    driver = sqlDriver,
    connectionString = AppConfig.getConfigOrElseDefault(s"merged-fashion-sql-database.fashion.${AppConfig.myEnvironment}.url",
      "jdbc:sqlserver://viking.be.integ.yoox.net;DatabaseName=OMS_Fashion;"),
    username = AppConfig.getConfigOrElseDefault(s"merged-fashion-sql-database.fashion.${AppConfig.myEnvironment}.user.username","yooxcrmtools"),
    password = AppConfig.getConfigOrElseDefault(s"merged-fashion-sql-database.fashion.${AppConfig.myEnvironment}.user.password","y00xCRMt00ls")
  )

  def FashionEventLog = DBConfigRequest(
    driver = sqlDriver,
    connectionString = AppConfig.getConfigOrElseDefault(s"fashion-event-log-sql-database.fashioneventlog.${AppConfig.myEnvironment}.url",
      "jdbc:sqlserver://viking.be.integ.yoox.net;DatabaseName=OMS_Fashion;"),
    username = AppConfig.getConfigOrElseDefault(s"fashion-event-log-sql-database.fashioneventlog.${AppConfig.myEnvironment}.user.username","yooxcrmtools"),
    password = AppConfig.getConfigOrElseDefault(s"fashion-event-log-sql-database.fashioneventlog.${AppConfig.myEnvironment}.user.password","y00xCRMt00ls")
  )

  def OracleWcsStore = DBConfigRequest(
    driver = orcDriver,
    connectionString = AppConfig.getConfigOrElseDefault("web-commerce-database.integ_store.url",
      "jdbc:oracle:thin:@yaiwcs01.cyom9nicjzk0.eu-west-1.rds.amazonaws.com:1521:yaiwcs01"),
    username = AppConfig.getConfigOrElseDefault("web-commerce-database.integ_store.user","WCINT01_FUNTST_RO"),
    password = AppConfig.getConfigOrElseDefault("web-commerce-database.integ_store.password","J19wgUwj917IWp")
  )

  def OracleWcsStaging = DBConfigRequest(
    driver = orcDriver,
    connectionString = AppConfig.getConfigOrElseDefault("web-commerce-database.integ_stg.url",
      "jdbc:oracle:thin:@yaiwcs01.cyom9nicjzk0.eu-west-1.rds.amazonaws.com:1521:yaiwcs01"),
    username = AppConfig.getConfigOrElseDefault("web-commerce-database.integ_stg.user","WCINT01_FUNTST_RO"),
    password = AppConfig.getConfigOrElseDefault("web-commerce-database.integ_stg.password","J19wgUwj917IWp")
  )

  def WMS = DBConfigRequest(
    driver = sqlDriver,
    connectionString = AppConfig.getConfigOrElseDefault(s"back-end-sql-database.wms.${AppConfig.myEnvironment}.url",
      "jdbc:sqlserver://viking.be.integ.yoox.net;DatabaseName=WMS;integratedSecurity=true;authenticationScheme=NativeAuthentication"),
    username = "",
    password = "")

}
