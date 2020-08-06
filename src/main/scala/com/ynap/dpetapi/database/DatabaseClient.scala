package com.ynap.dpetapi.database

import java.sql.{Connection, ResultSet}

import com.sun.rowset.CachedRowSetImpl
import com.ynap.dpetapi.AppConfig
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import javax.sql.rowset.CachedRowSet
import oracle.xdb.XMLType
import scala.annotation.tailrec
import scala.util.{Failure, Try}
import scala.xml.{Node, XML}

/** DatabaseClient companion class containing static "helper" transformation functions
 *
 */
object DatabaseClient {  object Databases extends Enumeration {
  val Fashion,
  AUTOMA,
  FrontendMultibrand,
  FrontendMonobrand,
  MergedFashion,
  FashionEventLog,
  OracleWcsStaging,
  OracleWcsStore,
  WMS
  = Value
}

  def getDbClient(siteCode: String): Try[DatabaseClient] = {
    siteCode match {
      case "VALENTINO" =>
        Try(new DatabaseClient(
          AppConfig.getConfigOrElseDefault("val.web-commerce-database.url", "jdbc:oracle:thin:@rds.wcs01.int4.ewe1.aws.dev.e-comm:1521:yaiwcs40"),
          AppConfig.getConfigOrElseDefault("val.web-commerce-database.driver", "oracle.jdbc.driver.OracleDriver"),
          AppConfig.getConfigOrElseDefault("val.web-commerce-database.user", "WCINT04_YNPTST_RO"), //VALENTINO WCS
          AppConfig.getConfigOrElseDefault("val.web-commerce-database.password", "gT3Ushj3dk") //VALENTINO WCS
        ))
      case "VALENTINO_UAT" =>
        Try(new DatabaseClient(
          AppConfig.getConfigOrElseDefault("val.web-commerce-database.url", "jdbc:oracle:thin:@yaiwcs40.clvlinlaa9nh.eu-west-1.rds.amazonaws.com:1521:yaiwcs40"),
          AppConfig.getConfigOrElseDefault("val.web-commerce-database.driver", "oracle.jdbc.driver.OracleDriver"),
          AppConfig.getConfigOrElseDefault("val.web-commerce-database.user", "WCUAT04_DATAPROV_RO"), //VALENTINO WCS
          AppConfig.getConfigOrElseDefault("val.web-commerce-database.password", "L3nu9uIwSR") //VALENTINO WCS
        ))
      case "MONCLER_UAT" =>
        Try(new DatabaseClient(
          AppConfig.getConfigOrElseDefault("monuat.web-commerce-database.url", "jdbc:oracle:thin:@yauwcs01.cyom9nicjzk0.eu-west-1.rds.amazonaws.com:1521:yauwcs01"),
          AppConfig.getConfigOrElseDefault("monuat.web-commerce-database.driver", "oracle.jdbc.driver.OracleDriver"),
          AppConfig.getConfigOrElseDefault("monuat.web-commerce-database.user", "WCUAT01_FUNTST_RO"),
          AppConfig.getConfigOrElseDefault("monuat.web-commerce-database.password", "xhRbYi_jbK")
        ))
      case "FERRARI_UAT" =>
        Try(new DatabaseClient(
          AppConfig.getConfigOrElseDefault("feruat.web-commerce-database.url", "jdbc:oracle:thin:@yauwcs01.cyom9nicjzk0.eu-west-1.rds.amazonaws.com:1521:yauwcs01"),
          AppConfig.getConfigOrElseDefault("feruat.web-commerce-database.driver", "oracle.jdbc.driver.OracleDriver"),
          AppConfig.getConfigOrElseDefault("feruat.web-commerce-database.user", "WCUAT01_FUNTST_RO"), //FERRARI WCS
          AppConfig.getConfigOrElseDefault("feruat.web-commerce-database.password", "xhRbYi_jbK") //FERRARI WCS
        ))
      case "MONCLER" =>
        Try(new DatabaseClient(
          AppConfig.getConfigOrElseDefault("mon.web-commerce-database.url", "jdbc:oracle:thin:@yaiwcs01.cyom9nicjzk0.eu-west-1.rds.amazonaws.com:1521:yaiwcs01"),
          AppConfig.getConfigOrElseDefault("mon.web-commerce-database.driver", "oracle.jdbc.driver.OracleDriver"),
          AppConfig.getConfigOrElseDefault("mon.web-commerce-database.user", "WCINT01_FUNTST_RO"),
          AppConfig.getConfigOrElseDefault("mon.web-commerce-database.password", "J19wgUwj917IWp")
        ))
      case "FERRARI" =>
        Try(new DatabaseClient(
          AppConfig.getConfigOrElseDefault("fer.web-commerce-database.url", "jdbc:oracle:thin:@yaiwcs01.cyom9nicjzk0.eu-west-1.rds.amazonaws.com:1521:yaiwcs01"),
          AppConfig.getConfigOrElseDefault("fer.web-commerce-database.driver", "oracle.jdbc.driver.OracleDriver"),
          AppConfig.getConfigOrElseDefault("fer.web-commerce-database.user", "WCINT01_FUNTST_RO"), //FERRARI WCS
          AppConfig.getConfigOrElseDefault("fer.web-commerce-database.password", "J19wgUwj917IWp") //FERRARI WCS
        ))
      case "THEOUTNET" =>
        Try(new DatabaseClient(
          AppConfig.getConfigOrElseDefault("ton.web-commerce-database.url", "jdbc:oracle:thin:@rds.wcs01.int3.ewe1.aws.yoox.net:1521:yaiwcs30"), //TON
          AppConfig.getConfigOrElseDefault("ton.web-commerce-database.driver", "oracle.jdbc.driver.OracleDriver"),
          AppConfig.getConfigOrElseDefault("ton.web-commerce-database.user", "WCINT03_FUNTST_RO"), //TON
          AppConfig.getConfigOrElseDefault("ton.web-commerce-database.password", "MmHFi95BCi") //TON
        ))
      case "MRPORTER" | "MRP" =>
        Try(new DatabaseClient(
          AppConfig.getConfigOrElseDefault("mrp.web-commerce-database.url", "jdbc:oracle:thin:@yaiwcs50.cb5nsacp180c.eu-west-1.rds.amazonaws.com:1521:yaiwcs50"),
          AppConfig.getConfigOrElseDefault("mrp.web-commerce-database.driver", "oracle.jdbc.driver.OracleDriver"),
          AppConfig.getConfigOrElseDefault("mrp.web-commerce-database.user", "WCINT05_E2EAUT_RO"),
          AppConfig.getConfigOrElseDefault("mrp.web-commerce-database.password", "pianoD7Qux")
        ))
      case "NETAPORTER" | "NAP" =>
        Try(new DatabaseClient(
          AppConfig.getConfigOrElseDefault("nap.web-commerce-database.url", "jdbc:oracle:thin:@napwi01l.cj0kr05midza.eu-west-1.rds.amazonaws.com:1521:napwi01l"),
          AppConfig.getConfigOrElseDefault("nap.web-commerce-database.driver", "oracle.jdbc.driver.OracleDriver"),
          AppConfig.getConfigOrElseDefault("nap.web-commerce-database.user", "WCINT08_FUNTST_STG_RO"),
          AppConfig.getConfigOrElseDefault("nap.web-commerce-database.password", "Bu_pwgqvXB")
        ))
      case _ =>
        Failure(new RuntimeException(s"No WCS Oracle database defined for [$siteCode]"))
    }
  }
  def getSchemaOwner(siteCode: String):String = {
    siteCode match {
      case "VALENTINO_UAT" =>
        AppConfig.getConfigOrElseDefault("vdi.web-commerce-database.schemaowner", "wcuat04_owner")
      case "VALENTINO" =>
        AppConfig.getConfigOrElseDefault("vdi.web-commerce-database.schemaowner", "wcint04_owner")
      case "MONCLER" =>
        AppConfig.getConfigOrElseDefault("mon.web-commerce-database.schemaowner", "wcint01_owner")
      case "FERRARI" =>
        AppConfig.getConfigOrElseDefault("fer.web-commerce-database.schemaowner", "wcint01_owner")
      case "MONCLER_UAT" =>
        AppConfig.getConfigOrElseDefault("mon.web-commerce-database.schemaowner", "wcuat01_owner")
      case "FERRARI_UAT" =>
        AppConfig.getConfigOrElseDefault("fer.web-commerce-database.schemaowner", "wcuat01_owner")
      case "THEOUTNET" =>
        AppConfig.getConfigOrElseDefault("ton.web-commerce-database.schemaowner", "wcint03_owner")
      case "MRPORTER" | "MRP" =>
        AppConfig.getConfigOrElseDefault("mrp.web-commerce-database.schemaowner", "wcint05_owner")
      case "NETAPORTER" | "NAP" =>
        AppConfig.getConfigOrElseDefault("nap.web-commerce-database.schemaowner", "wcint08_stg_owner")
      case _ =>
        "UNKNOWN"
    }
  }

  /**
   * Implicit classes and imports used for JSON "pretty" printing
   */
  implicit val formats = net.liftweb.json.DefaultFormats

  import net.liftweb.json.Extraction._
  import net.liftweb.json.JsonAST._

  /**
   * Enumeration of available database configurations
   */

  def getMyConnection(confRequest: DBConfigRequest): Try[RepositoryClient[ResultSet]] = {
    Try(new DatabaseClient(
      driver = confRequest.driver,
      connectionString = confRequest.connectionString,
      username = confRequest.username,
      password = confRequest.password))
  }

  /**
   * Function for creating a DB Repository client on the specified database.
   *
   * @param databaseEnumeration Enumeration of the database to get a connection on
   * @return Try of a RepositoryClient connect
   */
  def getClient(databaseEnumeration: Databases.Value): Try[RepositoryClient[ResultSet]] = {
    databaseEnumeration match {
      case Databases.Fashion =>
        getMyConnection(DBConfigRequest.Fashion)
      case Databases.AUTOMA =>
        getMyConnection(DBConfigRequest.AUTOMA)
      case Databases.FrontendMultibrand =>
        getMyConnection(DBConfigRequest.FrontendMultibrand)
      case Databases.FrontendMonobrand =>
        getMyConnection(DBConfigRequest.FrontendMonobrand)
      case Databases.MergedFashion =>
        getMyConnection(DBConfigRequest.MergedFashion)
      case Databases.FashionEventLog =>
        getMyConnection(DBConfigRequest.FashionEventLog)
      case Databases.OracleWcsStaging =>
        getMyConnection(DBConfigRequest.OracleWcsStaging)
      case Databases.OracleWcsStore =>
        getMyConnection(DBConfigRequest.OracleWcsStore)
      case Databases.WMS =>
        getMyConnection(DBConfigRequest.WMS)
      case _ => Failure(new RuntimeException(s"Database not recognised [$databaseEnumeration]"))
    }
  }

  /**
   * Utility method to return the database Enumeration based on the contents of the start of the Site Code string
   *
   * @param siteCode Website location code
   * @return Enumerated value for the database connection required
   */
  def getFrontendDatabaseEnumeration(siteCode: String): Databases.Value = {
    if (siteCode.toUpperCase.startsWith("YOOX")
      || siteCode.toUpperCase.startsWith("THECORNER")
      || siteCode.toUpperCase.startsWith("SHOESCRIBE"))
      Databases.FrontendMultibrand
    else
      Databases.FrontendMonobrand
  }

  /**
   * Helper function to parse a JSON object return data from a specific field
   * @param json JSON object to parse for data
   * @param fieldName Field name to search for
   * @return Contents of JSON data field
   */
  def parseJsonForField(json: String, fieldName: String): String = {
    import net.liftweb.json._  // Needed for JSON parse() method below
    parse(json) match {
      case obj: JObject => obj.values.filter(_._1 == fieldName).map(x => x._2.toString).toList.head
      case _ => "Invalid case matched"
    }
  }

  /**
   * Helper function to return a list of strings for a column of the rowset
   * @param fieldName Column name of fields to return
   * @param rs ResultSet
   * @return List of fields result
   */
  def getFieldList[T](fieldName: String)(rs: ResultSet): Try[List[T]] = {
    Try {
      val buildCustomList = () => {
        rs.getObject(fieldName).asInstanceOf[T]
      }
      Iterator.continually(rs.next()).takeWhile(identity).map(_ => buildCustomList()).toList
    }
  }

  /**
   * Null transformation to return the original SQL XML result string
   *
   * @param rs ResultSet
   * @return
   */
  def getXmlResult(rs: ResultSet): Try[List[Node]] = {
    Try {
      val xml =
        Iterator.continually(rs.next()).takeWhile(identity).map(_ => {
          rs.getObject(1) match {
            case xmlType: XMLType => // Oracle XML record type
              xmlType.getStringVal
            case xmlString => // SqlServer XML record type
              xmlString.toString
          }
        }).toList.mkString // If SQL fetch size is smaller than XML payload returns multiple records then merge the List of XML together into a single string
      List(XML.loadString(xml))
    }
  }

  /**
   * Decorator transformation to convert a ResultSet row into a Map of DB table column name/value pairs
   *
   * @param rs ResultSet
   * @return List of Map objects
   */
  def getMapList(rs: ResultSet): Try[List[Map[String, _]]] = {
    Try {
      val md = rs.getMetaData
      val colNames = for (i <- 1 to md.getColumnCount) yield md.getColumnName(i)
      val buildMap = () => (for (n <- colNames) yield n -> rs.getObject(n)).toMap
      Iterator.continually(rs.next()).takeWhile(identity).map(_ => buildMap()).toList
    }
  }

  /**
   * Decorator transformation to convert a ResultSet row into a JSON object of DB table column name/value pairs
   *
   * @param rs ResultSet
   * @return List of JSON objects
   */
  def getJsonList(rs: ResultSet): Try[IndexedSeq[io.circe.Json]] = {
    import io.circe.parser._
    Try {
      val md = rs.getMetaData
      val colNames = for (i <- 1 to md.getColumnCount) yield md.getColumnName(i)
      val buildMap = () => parse(prettyRender(decompose((for (n <- colNames) yield n -> rs.getObject(n) match {
        case (str: String, s: String) =>
          (str, s)
        case (str: String, i: Integer) =>
          (str, i)
        case (str: String, n: Number) if n.getClass.getName == "java.math.BigDecimal" =>
          // Needed to convert Oracle BigDecimal (INT) values to integer.
          (str, n.toString.toInt)
        case (str: String, b: Object) =>
          // Boolean
          (str, b)
        case (str: String, any) =>
          (str, any)
      }).toMap))) match {
        case Left(failure) =>
          throw new RuntimeException(s"Invalid JSON : ${failure.message}")
        case Right(json) =>
          json
      }
      Iterator.continually(rs.next()).takeWhile(identity).map(_ => buildMap()).toIndexedSeq
    }
  }

  /**
   * Decorator transformation to convert the ResultSet into a persisted CachedRowSet
   * that will still be available when the DB connection is closed
   *
   * @param rs ResultSet from the DB query.
   * @return CachedRowSet of the data returned from the query
   */
  def getCachedRowSet(rs: ResultSet): Try[CachedRowSet] = {
    Try {
      val crs: CachedRowSet = new CachedRowSetImpl
      crs.populate(rs)
      crs
    }
  }

  /**
   * Decorator transformation to print a column list of the ResultSet returned
   *
   * @param rs ResultSet
   */
  def printDataList(rs: ResultSet): Try[_] = {
    Try {
      val md = rs.getMetaData
      val columnCount = md.getColumnCount
      val colNames = for (i <- 1 to columnCount) yield md.getColumnName(i)
      for (i <- 1 to columnCount) {
        print(colNames(i - 1))
        if (i == columnCount) println()
        else print(", ")
      }
      Iterator.continually(rs.next()).takeWhile(identity).foreach(cr => {
        for (i <- 1 to columnCount) {
          print(rs.getString(i))
          if (i == columnCount) println()
          else print(", ")
        }
      })
    }
  }
  /**
   * Utility function that builds a List of Strings response from the specified named DB and query and field name
   *
   * @param connection Mnemonic if database to run query against
   * @param query SQL query to evaluate
   * @param fieldName Field name of the column to return
   * @return List of strings result
   */
  def getFieldList[T](connection: DatabaseClient.Databases.Value, query: String, fieldName: String): Try[List[T]] = {
    getFieldList[T](connection, Seq(query), fieldName)
  }
  def getFieldList[T](connection: DatabaseClient.Databases.Value, query: Seq[String], fieldName: String): Try[List[T]] = {
    val res = for {
      client <- DatabaseClient.getClient(connection)
      result <- client.run(query, DatabaseClient.getFieldList[T](fieldName))
    } yield result
    res.asInstanceOf[Try[List[T]]]
  }
  /**
   * Utility function that builds a XML response from the specified named DB and query
   *
   * @param connection Mnemonic if database to run query against
   * @param query      SQL query to evaluate
   * @return XML string result
   */
  def getXmlResult(connection: DatabaseClient.Databases.Value, query: String): Try[List[Node]] = {
    getXmlResult(connection, Seq(query))
  }
  def getXmlResult(connection: DatabaseClient.Databases.Value, query: Seq[String]): Try[List[Node]] = {
    val res = for {
      client <- DatabaseClient.getClient(connection)
      result <- client.run(query, DatabaseClient.getXmlResult)
    } yield result
    res.asInstanceOf[Try[List[Node]]]
  }

  /**
   * Utility function that builds a CachedRowSet from the specified named DB and query
   *
   * @param connection Mnemonic if database to run query against
   * @param query      SQL query to evaluate
   * @return Cached row set of the data
   */
  def getCachedRowSet(connection: DatabaseClient.Databases.Value, query: String): Try[CachedRowSet] = {
    getCachedRowSet(connection, Seq(query))
  }
  def getCachedRowSet(connection: DatabaseClient.Databases.Value, query: Seq[String]): Try[CachedRowSet] = {
    val res = for {
      client <- DatabaseClient.getClient(connection)
      result <- client.run(query, DatabaseClient.getCachedRowSet)
    } yield result
    res
  }

  /**
   * Utility function that builds a JSON List from the specified named DB and query
   *
   * @param connection Mnemonic if database to run query against
   * @param query      SQL query to evaluate
   * @return JSON List of the data
   */
  def getJsonList(connection: DatabaseClient.Databases.Value, query: String): Try[IndexedSeq[io.circe.Json]] = {
    getJsonList(connection, Seq(query))
  }
  def getJsonList(connection: DatabaseClient.Databases.Value, query: Seq[String]): Try[IndexedSeq[io.circe.Json]] = {
    val res = for {
      client <- DatabaseClient.getClient(connection)
      result <- client.run(query, DatabaseClient.getJsonList)
    } yield result
    res
  }

  /**
   * Utility function that builds a Map List from the specified named DB and query
   *
   * @param connection Mnemonic if database to run query against
   * @param query      SQL query to evaluate
   * @return Map List of the data
   */
  def getMapList(connection: DatabaseClient.Databases.Value, query: String): Try[List[Map[String, _]]] = {
    getMapList(connection, Seq(query))
  }
  def getMapList(connection: DatabaseClient.Databases.Value, query: Seq[String]): Try[List[Map[String, _]]] = {
    val res = for {
      client <- DatabaseClient.getClient(connection)
      result <- client.run(query, DatabaseClient.getMapList)
    } yield result
    res
  }

  /**
   * Utility function that prints a data List from the specified named DB and query
   *
   * @param connection Enumerated connection name
   * @param query      SQL database query
   * @return
   */
  def printDataList(connection: DatabaseClient.Databases.Value, query: String): Try[_] = {
    val res = for {
      client <- DatabaseClient.getClient(connection)
      result <- client.run(query, DatabaseClient.printDataList)
    } yield result
    res
  }

  /**
   * Utility function that executes a DML statement on the named DB
   *
   * @param connection Mnemonic if database to run query against
   * @param statement  SQL query to execute
   * @return Number of rows affected
   */
  def executeUpdate(connection: DatabaseClient.Databases.Value, statement: String): Try[Int] = {
    val res = for {
      client <- DatabaseClient.getClient(connection)
      result <- client.run(statement)
    } yield result
    res.asInstanceOf[Try[Int]]
  }

  /**
   * Utility function that builds a CachedRowSet from an existing DatabaseClient
   *
   * @param client DatabaseClient created with encrypted password
   * @param query  SQL query to evaluate
   * @return Cached row set of the data
   */
  def getCachedRowSet(client: Try[RepositoryClient[ResultSet]], query: String): Try[CachedRowSet] = {
    getCachedRowSet(client, Seq(query))
  }
  def getCachedRowSet(client: Try[RepositoryClient[ResultSet]], query: Seq[String]): Try[CachedRowSet] = {
    val res = for {
      client <- client
      result <- client.run(query, DatabaseClient.getCachedRowSet)
    } yield result
    res
  }
  /**
   * Utility function that builds a Json List from an existing DatabaseClient
   *
   * @param client DatabaseClient created with encrypted password
   * @param query  SQL query to evaluate
   * @return List of JSON objects
   */
  def getJsonList(client: Try[RepositoryClient[ResultSet]], query: String): Try[IndexedSeq[io.circe.Json]] = {
    getJsonList(client, Seq(query))
  }
  def getJsonList(client: Try[RepositoryClient[ResultSet]], query: Seq[String]): Try[IndexedSeq[io.circe.Json]] = {
    val res = for {
      client <- client
      result <- client.run(query, DatabaseClient.getJsonList)
    } yield result
    res
  }
  /**
   * Utility function that builds a Json List from an existing DatabaseClient
   *
   * @param client DatabaseClient created with encrypted password
   * @param query  SQL query to evaluate
   * @return List of Map objects
   */
  def getMapList(client: Try[RepositoryClient[ResultSet]], query: String): Try[List[Map[String, _]]] = {
    getMapList(client, Seq(query))
  }
  def getMapList(client: Try[RepositoryClient[ResultSet]], query: Seq[String]): Try[List[Map[String, _]]] = {
    val res = for {
      client <- client
      result <- client.run(query, DatabaseClient.getMapList)
    } yield result
    res
  }

  /**
   * Utility function that builds a Json List from an existing DatabaseClient
   *
   * @param client DatabaseClient created with encrypted password
   * @param query  SQL query to evaluate
   * @return List of Map objects
   */
  def printDataList(client: Try[RepositoryClient[ResultSet]], query: String): Try[_] = {
    val res = for {
      client <- client
      result <- client.run(query, DatabaseClient.printDataList)
    } yield result
    res
  }

  /**
   * Utility function that executes a DML statement on an existing DatabaseClient
   *
   * @param client    DatabaseClient created with encrypted password
   * @param statement SQL query to execute
   * @return Number of rows affected
   */
  def executeUpdate(client: Try[RepositoryClient[ResultSet]], statement: String): Try[Int] = {
    val res = for {
      client <- client
      result <- client.run(statement)
    } yield result
    res.asInstanceOf[Try[Int]]
  }
}

/**
 *
 * @param connectionString SQL connection string
 * @param driver           JDBC driver name
 * @param username         Connection user name
 * @param password         Connection password
 * @example {{{
 *
 *                           val client : RepositoryClient[ResultSet] = new DatabaseClient(
 *                                           connectionString = Configuration.frontEndMultiBrandSqlDatabase,
 *                                           username = Configuration.frontEndMultiBrandSqlDatabaseUserName,
 *                                           password = Configuration.frontEndMultiBrandSqlDatabaseUserPassword
 *                                       )
 *
 *                           val query = "SELECT * FROM dbTable"
 *
 *                           println(s"Run query[${query}] using DatabaseClient.getJsonList")
 *                           client.run(query, DatabaseClient.getJsonList) match {
 *                               case Failure(ex) => ex
 *                               case item => item.foreach(println)
 *                           }
 *                         }
 *
 *          }}}
 */
class DatabaseClient(
                      connectionString: String,
                      driver: String = AppConfig.getConfigOrElseDefault("driver","com.microsoft.sqlserver.jdbc.SQLServerDriver"),
                      username: String,
                      password: String
                    )
  extends RepositoryClient[ResultSet] {

  /**
   * Runs the currently defined select query against the currently defined database using the currently defined credentials
   * and will "decorate" the rows of the ResultSet returned using the given "transform" function provided.
   *
   * @param query     SQL query to be executed
   * @param transform Decorator transformation function (fn :(A) => Try[List[_])
   *                  //    * @tparam ResultSet Resultset generated by the DB query
   * @tparam R
   * @return (Try of) List of decorated objects (e.g. JSON objects, Map objects etc.)
   */
  def run[I, R](query: I, transform: (ResultSet) => Try[R]): Try[R] = {

    @tailrec
    def executeEachQuery[I](query: I): Try[ResultSet] = {
      query match {
        case queryList: Seq[String] if queryList.size == 1 =>
          executeQuery(queryList.head)
        case queryItem: String =>
          executeQuery(queryItem)
        case queryList: Seq[String] =>
          executeQuery(queryList.head)
          executeEachQuery(queryList.tail)
      }
    }
    try {
      for {
        _ <- connect
        rs <- executeEachQuery(query)
        t <- transform(rs)
      } yield t
    } finally {
      disconnect
    }
  }

  /**
   * Runs the currently defined update query against the currently defined database using the currently defined credentials
   * and will return the number of rows affected.
   *
   * @param query SQL query to be executed
   * @return (Try of) count of rows affected.
   */
  def run(query: String): Try[_] = {
    try {
      for {
        _ <- connect
        rs <- executeUpdate(query)
      } yield rs
    } finally {
      disconnect
    }
  }

  /**
   * Simple connection test SQL statement
   *
   * @return ResultSet returned from the query
   */
  def test: Try[ResultSet] = test("select 1")

  /**
   * Current connection for this instance of the DatabaseClient
   */
  var connection: Try[Connection] = _

  /**
   * Function to connect to a SQL database defined by the connection string, username and password
   *
   * @return Try of the connection (or failure reason)
   */
  def connect: Try[Connection] = {
    (connectionString, driver, username, password) match {
      case ("", _, _, _) => throw new RuntimeException(s"No connection string was found.")
      case (cs, _, "", "") =>
        if (cs.split(";").exists(p => p.equalsIgnoreCase("integratedSecurity=true"))) enableIntegratedSecurity
        Class.forName(driver)
        println(s"Connection String: $cs")
        connection = Try {
          val config = new HikariConfig()
          config.setJdbcUrl(connectionString)
          config.setUsername(username)
          config.setPassword(password)
          config.setDriverClassName(driver)
          config.setMaximumPoolSize(20)
          config.setPoolName(s"DPETAPI-$driver")
          config.setMinimumIdle(2)
//          config.setConnectionTimeout(50000)
          val hconn = new HikariDataSource(config).getConnection
          println(s"HikariDataSource: $hconn")
          import com.zaxxer.hikari.pool.HikariPool
          println("hconn.getMetaData ::" + hconn.getMetaData);
          println("hconn.getClientInfo ::" + hconn.getClientInfo);
          println("hconn.getCatalog ::" + hconn.getCatalog);
          val hikariPool = new HikariPool(config)
          println("The hikariPool count is ::" + hikariPool.getActiveConnections());
          hconn
        }
        connection

      case (cs, _, u, p) =>
        Class.forName(driver)
        println(s"Connection String: $cs")
        connection = Try {
          val config = new HikariConfig()
          config.setJdbcUrl(connectionString)
          config.setUsername(username)
          config.setPassword(password)
          config.setDriverClassName(driver)
          config.setMaximumPoolSize(20)
          config.setPoolName(s"DPETAPI-$driver")
          config.setMinimumIdle(2)
//          config.setConnectionTimeout(50000)
          val hconn = new HikariDataSource(config).getConnection
          println(s"HikariDataSource: $hconn")
          import com.zaxxer.hikari.pool.HikariPool
          println("hconn.getMetaData ::" + hconn.getMetaData);
          println("hconn.getClientInfo ::" + hconn.getClientInfo);
          println("hconn.getCatalog ::" + hconn.getCatalog);
          val hikariPool = new HikariPool(config)
          println("The hikariPool count is ::" + hikariPool.getActiveConnections());
          hconn
        }
        connection

    }
  }

  /**
   * Windows authentication (integratedSecurity = true) requires sqljdbc_auth.dll folder to be present in environment path variable.
   *
   * If not the following error occurs:
   *
   * Failed to load the sqljdbc_auth.dll cause : no sqljdbc_auth in java.library.path
   * com.microsoft.sqlserver.jdbc.SQLServerException: This driver is not configured for integrated authentication.
   * Caused by: java.lang.UnsatisfiedLinkError: no sqljdbc_auth in java.library.path
   *
   * The java.library.path is read only once when the JVM starts up. If this property is changed using System.setProperty, it won't make any difference.
   *
   * The following method updates the path where sqljdbc_auth.dll exists in project/libs folder just for runtime.
   */
  def enableIntegratedSecurity {
    val currentDirectory = new java.io.File(".").getCanonicalPath
    val libsDirectory    = s"$currentDirectory\\libs"
    val usrPathsField    = classOf[ClassLoader].getDeclaredField("usr_paths")
    usrPathsField.setAccessible(true)

    val paths = usrPathsField.get(null).asInstanceOf[Array[String]]

    if (!paths.contains(libsDirectory)) {
      usrPathsField.set(null, paths :+ libsDirectory)
    }
  }

  /**
   * Function to disconnect from an open SQL database connection
   *
   * @return Try of the disconnection operation (or failure reason)
   */
  def disconnect: Try[Unit] = {
    Try(connection.get.close())
  }

  /**
   * Function to test an open SQL database connection
   *
   * @return Try of the test result (or failure reason)
   */
  def test(query: String): Try[ResultSet] = {
    Try {
      val ps = connection.get.prepareStatement(query)
      ps.executeQuery()
    }
  }

  /**
   * Function to execute the given SQL query on an open DB connection
   *
   * @param query SQL query to be executed on the open connection
   * @return Try of the ResultSet (or failure reason)
   */
  def executeQuery(query: String): Try[ResultSet] = {
    Try {
      val st = connection.get.createStatement()
      st.executeQuery(query)
    }
  }

  /**
   * Function to execute the given SQL DML statements (INSERT, UPDATE, DELETE) on an open DB connection
   *
   * @param statement SQL statement to be executed on the open connection
   * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
   */
  def executeUpdate(statement: String): Try[Int] = {
    Try {
      val st = connection.get.createStatement()
      st.executeUpdate(statement)
    }
  }
}
