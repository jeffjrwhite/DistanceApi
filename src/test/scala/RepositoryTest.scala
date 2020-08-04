import java.sql.ResultSet

import com.ynap.dpetapi.database.{DBConfigRequest, DatabaseClient, RepositoryClient}
import com.ynap.dpetapi.AppConfig
import com.ynap.dpetapi.database.DatabaseClient.Databases
import javax.sql.rowset.CachedRowSet
import net.liftweb.json.{JObject, JValue}
import org.scalatest.{FlatSpec, Matchers, _}

import scala.util.{Failure, Success, Try}
import scala.xml.{Node, PrettyPrinter}

class RepositoryTest extends FlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  override def beforeAll(): Unit = {
    println("Setup:beforeAll Create Fixtures")
  }

  override def afterAll(): Unit = {
    println("Setup:afterAll")
  }

  override def beforeEach {
    println("Setup:beforeEach")
  }

  override def afterEach {
    println("Teardown:afterEach")
  }

  val clientRequestMonclerFerrari = DBConfigRequest(
    driver = AppConfig.getConfigOrElseDefault("web-commerce-database.driver","oracle.jdbc.driver.OracleDriver"),
    connectionString = AppConfig.getConfigOrElseDefault("web-commerce-database.integ_store.url", "jdbc:oracle:thin:@yaiwcs01.cyom9nicjzk0.eu-west-1.rds.amazonaws.com:1521:yaiwcs01"),
    username = AppConfig.getConfigOrElseDefault("web-commerce-database.integ_store.user","WCINT01_FUNTST_RO"),
    password = AppConfig.getConfigOrElseDefault("web-commerce-database.integ_store.password","J19wgUwj917IWp")
  )

  "A Repository object will be created that" should "instantiate a DB driver correctly and run a test query" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(true)
    println("Creating Repository object with a default DB driver.")

    val client: RepositoryClient[ResultSet] = new DatabaseClient(
      connectionString = AppConfig.getConfigOrElseDefault("back-end-sql-database.fashion.url",
        "jdbc:sqlserver://oscluster.fe.integ.yoox.net;DatabaseName=osdb;"),
      username = "yooxcrmtools",
      password = "y00xCRMt00ls"
    )
    client.connect match {
      case Failure(ex) => throw ex
      case _ => ()
    }
    client.test match {
      case Failure(ex) => throw ex
      case rs => rs.foreach(println)
    }
    client.disconnect match {
      case Failure(ex) => throw ex
      case _ => ()
    }
  }

  "A Repository object will be created that" should "instantiate a billy DB driver and run a test query" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    println("Creating Repository object with a billy DB driver.")

    val client: RepositoryClient[ResultSet] = new DatabaseClient(
      connectionString =
        AppConfig.getConfigOrElseDefault("billy-sql-database.url", "jdbc:sqlserver://nav.be.integ.yoox.net;DatabaseName=Accounting;"),
      driver =
        AppConfig.getConfigOrElseDefault("billy-sql-database.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
      username =
        AppConfig.getConfigOrElseDefault("billy-sql-database.user.name", "billyusertestIDE"),
      password =
        AppConfig.getConfigOrElseDefault("billy-sql-database.user.password", "T3S7i/3*")
    )

    client.connect match {
      case Failure(ex) => throw ex
      case _ => ()
    }
    client.test match {
      case Failure(ex) => throw ex
      case rs => rs.foreach(println)
    }
    client.disconnect match {
      case Failure(ex) => throw ex
      case _ => ()
    }
  }

  "A Repository object will be created that" should "instantiate a windows authentication correctly and run a test query" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.runOracleTests)

    val query = "SELECT top 5 * FROM orders.orderheaders"
    println("Creating Repository object with a default Windows Authentication")

    println(s"Run query[$query] using DatabaseClient.getMapList")
    val res = for {
      client <- DatabaseClient.getClient(Databases.WMS) //.WMS Database)
      result <- client.run(query, DatabaseClient.getMapList)
    } yield result
    res match {
      case Failure(ex) => throw ex
      case item => item.foreach(println)
    }
  }

  "A Repository object will be created that" should "instantiate a DB driver correctly and transform a sample SQL query using getMapList" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(true)

    val query = "SELECT top 5 * FROM Nazioni"

    println(s"Run query[$query] using DatabaseClient.getMapList")
    var res = for {
      client <- DatabaseClient.getClient(Databases.Fashion) //.FrontendMultibrand)
      result <- client.run(query, DatabaseClient.getMapList)
    } yield
      result
    res match {
      case Failure(ex) =>
        throw ex
      case item =>
        item.foreach(println)
    }

  }

  "A Repository object will be created that" should "instantiate a DB driver correctly and transform a sample SQL query using JSON" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(true)

    val query = "SELECT top 5 * FROM Nazioni"

    println(s"Run query[$query] using DatabaseClient.getJsonList")
    var res = for {
      client <- DatabaseClient.getClient(Databases.Fashion) //.FrontendMultibrand)
      result <- client.run(query, DatabaseClient.getJsonList)
    } yield result
    res match {
      case Failure(ex) => throw ex
      case item => item.foreach(println)
    }
  }

  "A Repository object will be created that" should "instantiate a DB driver explicitly and return a CachedRowSet" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(true)
    println("Creating Repository object with a default DB driver.")

    val query = "SELECT top 5 * FROM Nazioni"

    val newClient = new DatabaseClient(
      connectionString = AppConfig.getConfigOrElseDefault(
        "front-end-sql-database.multibrand.url",
        "jdbc:sqlserver://yooxcluster.fe.integ.yoox.net;DatabaseName=Yoox;"),
      username = AppConfig.getConfigOrElseDefault("front-end-sql-database.multibrand.username", "yooxCustomer"),
      password = AppConfig.getConfigOrElseDefault("front-end-sql-database.multibrand.password", "K0nch4LOV5ky")
    )

    println(s"Run query[$query] using DatabaseClient.getCachedRowSet")
    val res = for {
      result <- newClient.run(query, DatabaseClient.getCachedRowSet)
    } yield result
    res match {
      case Failure(ex) => throw ex
      case Success(crs: CachedRowSet) =>
        Iterator.continually(crs.next()).takeWhile(identity).foreach(cr => {
          val columnCount = crs.getMetaData.getColumnCount
          for (i <- 1 to columnCount) {
            print(crs.getString(i))
            if (i == columnCount) println()
            else print(", ")
          }
        })
      case _ => println("Undefined match/case encountered - should not get here.")
    }
  }

  "A Repository object will be created with invalid username that" should "FAIL to instantiate a DB driver correctly" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.integTest)

    println("Creating Repository object with invalid username/password.")

    val query = "SELECT top 5 * FROM Nazioni"

    val client = new DatabaseClient(
      connectionString = AppConfig.getConfigOrElseDefault(
        "back-end-sql-database.fashion.url",
        "jdbc:sqlserver://oscluster.fe.integ.yoox.net;DatabaseName=osdb;"),
      username = "username",
      password = "password"
    )

    println(s"Run query[$query] using DatabaseClient.getJsonList and catch SQLServerException")
    val thrown = intercept[com.microsoft.sqlserver.jdbc.SQLServerException] {
      client.run(query, DatabaseClient.getJsonList) match {
        case Failure(ex) => throw ex
        case item => item.foreach(println)
      }
    }
    println("Check the DatabaseClient SQL execution failed with SQLServerException : " + "Login failed for user 'username'")
    assert(thrown.getMessage.startsWith("Login failed for user 'username'"))
  }

  "A Repository object will be created that" should "instantiate an Oracle DB driver correctly and return a CachedRowSet" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.integTest)

    println("Creating Repository object with an Oracle DB driver.")

    val query = "select c.partnumber as SKU, t.name as trade_position, op.currency, op.price, i.quantity as inventory_quantity from " +
      "wcint01_owner.catentry c, wcint01_owner.tradeposcn t, wcint01_owner.offer o, wcint01_owner.offerprice op, wcint01_owner.inventory i " +
      "where c.catentry_id = o.catentry_id " +
      "and c.catentry_id = i.catentry_id " +
      "and o.TRADEPOSCN_ID = t.TRADEPOSCN_ID " +
      "and o.offer_id = op.offer_id " +
      "and t.name like 'Moncler%' FETCH NEXT 10 ROWS ONLY"

    val client: RepositoryClient[ResultSet] = DatabaseClient.getClient(Databases.OracleWcsStaging) match {
      case Failure(ex) => throw ex
      case Success(x) => x
    }
    println(s"Run query: $query \n using DatabaseClient.printDataList")
    client.run(query, DatabaseClient.printDataList) match {
      case Failure(ex) => throw ex
      case Success(_) => ()
    }

    println(s"Run query[$query] using DatabaseClient.printDataList utility method")

    DatabaseClient.printDataList(Databases.OracleWcsStaging, query) match {
      case Failure(ex) => throw ex
      case Success(_) => ()
    }

  }

  "A Repository object will be created that" should "instantiate an Oracle DB driver correctly and return a MapList" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.integTest)

    println("Creating Repository object with an Oracle DB driver.")
    val query = "select c.partnumber as SKU, t.name as trade_position, op.currency, op.price, i.quantity as inventory_quantity from " +
      "wcint01_owner.catentry c, wcint01_owner.tradeposcn t, wcint01_owner.offer o, wcint01_owner.offerprice op, wcint01_owner.inventory i " +
      "where c.catentry_id = o.catentry_id " +
      "and c.catentry_id = i.catentry_id " +
      "and o.TRADEPOSCN_ID = t.TRADEPOSCN_ID " +
      "and o.offer_id = op.offer_id " +
      "and t.name like 'Moncler%' FETCH NEXT 10 ROWS ONLY"

    val client: RepositoryClient[ResultSet] = DatabaseClient.getMyConnection(clientRequestMonclerFerrari) match {
      case Failure(ex) => throw ex
      case Success(x) => x
    }

    println(s"Run query[$query] using DatabaseClient.getMapList")
    client.run(query, DatabaseClient.getMapList) match {
      case Failure(ex) =>
        println(s"Failed. Reason: ${clientRequestMonclerFerrari}")
        throw ex
      case item => item.foreach(println)
    }
  }

  "A Repository object will be created that" should "instantiate an Oracle DB driver correctly and return a JsonList" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.integTest)

    println("Creating Repository object with an Oracle DB driver.")

    val query = "select c.partnumber as SKU, t.name as trade_position, op.currency, op.price, i.quantity as inventory_quantity from " +
      "wcint01_owner.catentry c, wcint01_owner.tradeposcn t, wcint01_owner.offer o, wcint01_owner.offerprice op, wcint01_owner.inventory i " +
      "where c.catentry_id = o.catentry_id " +
      "and c.catentry_id = i.catentry_id " +
      "and o.TRADEPOSCN_ID = t.TRADEPOSCN_ID " +
      "and o.offer_id = op.offer_id " +
      "and t.name like 'Moncler%' FETCH NEXT 10 ROWS ONLY"

    val client: RepositoryClient[ResultSet] = DatabaseClient.getClient(Databases.OracleWcsStaging) match {
      case Failure(ex) => throw ex
      case Success(x) => x
    }

    println(s"Run query[$query] using DatabaseClient.getJsonList")
    client.run(query, DatabaseClient.getJsonList) match {
      case Failure(ex) =>
        println(s"Failed. Reason: ${Databases.OracleWcsStaging}")
        throw ex
      case item => item.foreach(println)
    }
  }

  "A Repository object will be created that" should "test DML statements insert/update/delete" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    try {
      // Drop table quietly if Temp Table already exists
      val dropQuery =
        s"""DROP TABLE TempDBMatricole""".stripMargin
      DatabaseClient.executeUpdate(Databases.Fashion, dropQuery).get
    }
    catch {
      case e: Exception => () // Do Nothing
    }

    val query1 =
      s"""
          SELECT m.CodiceMatricola, m.Articolo_ID, m.OrdineTaglia, m.stato INTO TempDBMatricole FROM fashion.dbo.Matricole m
         |          WHERE m.Articolo_ID = 319601 AND m.OrdineTaglia = 12
         |                    """.stripMargin

    val result0 = DatabaseClient.executeUpdate(Databases.Fashion, query1).get

    println(s"Result from Insert Into TempDBMatricole result0 = $result0")
    result0 should equal(-1)

    val query2 =
      s"""
         |select * from TempDBMatricole
               """.stripMargin

    DatabaseClient.getJsonList(DatabaseClient.getClient(Databases.Fashion), query2).get

    val updateQuery =
      s"""
         |UPDATE TempDBMatricole SET stato = 23 WHERE Articolo_ID = 319601 AND OrdineTaglia = 12
      """.stripMargin

    var result1 = DatabaseClient.executeUpdate(Databases.Fashion, updateQuery).get

    println(s"Result from Update TEMPDBMATRicole result1 = $result1")
    result1 should equal(1)

    DatabaseClient.getJsonList(DatabaseClient.getClient(Databases.Fashion), query2).get

    val dropQuery =
      s"""DROP TABLE TempDBMatricole""".stripMargin

    var result2 = DatabaseClient.executeUpdate(Databases.Fashion, dropQuery).get

    println(s"Result from DROP TABLE TempDBMatricole result2 = $result2")
    result2 should equal(0)

  }

  "A Repository object will be created that" should "run an invalid SQL query" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    val query = "SELECT * FROM NazioniXXX"

    // Query should fail
    val thrown = intercept[com.microsoft.sqlserver.jdbc.SQLServerException] {
      println(s"Run query[$query] using DatabaseClient.getJsonList")
      var res = DatabaseClient.getJsonList(DatabaseClient.getClient(Databases.Fashion), query).get
    }
    println("Should throw exception SQLServerException :" + "Invalid object name 'NazioniXXX'.")
    assert(thrown.getMessage === "Invalid object name 'NazioniXXX'.")

  }

  "A Repository object will be created that" should "create SQL query that parses JSON using match/case" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    import net.liftweb.json._ // Needed for JSON parse() method below

    val query = "SELECT top 5 * FROM Nazioni"

    println(s"Run query[$query] using DatabaseClient.getJsonList")
    DatabaseClient.getJsonList(DatabaseClient.getClient(Databases.Fashion), query).get.foreach {
      item => printJsonValues(parse(item.toString))
    }

    println(s"Run query[$query] using DatabaseClient.getJsonList")
    DatabaseClient.getJsonList(DatabaseClient.getClient(Databases.Fashion), query).get.foreach {
      item =>
        println("Nome : " + DatabaseClient.parseJsonForField(item.toString, "Nome"))
        println("ID_Nazioni : " + DatabaseClient.parseJsonForField(item.toString, "ID_Nazioni"))
        println("CodiceAuto : " + DatabaseClient.parseJsonForField(item.toString, "CodiceAuto"))
        println("Cliente_MagazziniFisici_ID : " + DatabaseClient.parseJsonForField(item.toString, "Cliente_MagazziniFisici_ID"))
    }
  }

  def printJsonValues(json: JValue): Unit = {
    json match {
      case obj: JObject =>
        obj.values match {
          case fieldList: Map[String, Any] =>
            val fieldCount = fieldList.size; // Add index to create a Tuple using zipWithIndex so we can identify last item
            fieldList.toList.zipWithIndex.foreach {
              field => {
                print(field._1.toString.trim)
                if (field._2 == fieldCount - 1) println()
                else print(", ")
              }
            }
          case _ => println("Undefined match/case encountered - should not get here 01.")
        }
      case _ => println("Undefined match/case encountered - should not get here 02.")
    }
  }

  "A Repository object will be created that" should "create SQL query that creates a shaped XML response as ATTRIBUTES" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    val query = "SELECT top (10) RepOrdiniClienti.ID_RepOrdiniClienti, count(RepOrdiniClienti.ID_RepOrdiniClienti) as NumRows," +
      "    (SELECT *" +
      "      FROM RepRigheOrdiniClienti" +
      "      where ID_RepOrdiniClienti = RepOrdiniClienti_ID" +
      "      FOR XML AUTO, TYPE)" +
      "    FROM RepOrdiniClienti, RepRigheOrdiniClienti" +
      "    where ID_RepOrdiniClienti = RepOrdiniClienti_ID" +
      "    group by RepOrdiniClienti.ID_RepOrdiniClienti" +
      "    having count(RepOrdiniClienti.ID_RepOrdiniClienti) > 1" +
      "    FOR XML AUTO, TYPE, XMLSCHEMA, ROOT('ORDERS') "

    println(s"Run query[$query] using DatabaseClient.getXmlResult with XML AUTO, TYPE, XMLSCHEMA")

    val prettyXml = new scala.xml.PrettyPrinter(80, 4)
    var xml = DatabaseClient.getXmlResult(Databases.Fashion, query).get.head
    println(new PrettyPrinter(80, 2).format(xml))
  }

  "A Repository object will be created that" should "create SQL query that creates a shaped XML response as PATH" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    val query = "SELECT top (10) RepOrdiniClienti.ID_RepOrdiniClienti AS '@ID', count(RepOrdiniClienti.ID_RepOrdiniClienti) AS '@COUNT'," +
      " (SELECT *      FROM RepRigheOrdiniClienti" +
      " where ID_RepOrdiniClienti = RepOrdiniClienti_ID" +
      " FOR XML PATH('ITEM'), TYPE)" +
      //        "    AS 'ORDER'" +
      "    FROM RepOrdiniClienti, RepRigheOrdiniClienti" +
      "    where ID_RepOrdiniClienti = RepOrdiniClienti_ID" +
      "    group by RepOrdiniClienti.ID_RepOrdiniClienti" +
      "    having count(RepOrdiniClienti.ID_RepOrdiniClienti) > 1" +
      "    FOR XML PATH('ORDER'), ROOT('ORDERS')"

    println(s"Run query[$query] using DatabaseClient.getXmlResult with XML PATH")

    var xml = DatabaseClient.getXmlResult(Databases.Fashion, query).get.head
    println(new PrettyPrinter(80, 2).format(xml))
  }

  "A Repository object will be created that" should "run a shaped XML query and extract data using XPath " taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    val query = "SELECT top (10) RepOrdiniClienti.ID_RepOrdiniClienti AS '@ID', count(RepOrdiniClienti.ID_RepOrdiniClienti) AS '@COUNT'," +
      " (SELECT *      FROM RepRigheOrdiniClienti" +
      " where ID_RepOrdiniClienti = RepOrdiniClienti_ID" +
      " FOR XML PATH('ITEM'), TYPE)" +
      //        "    AS 'ORDER'" +
      "    FROM RepOrdiniClienti, RepRigheOrdiniClienti" +
      "    where ID_RepOrdiniClienti = RepOrdiniClienti_ID" +
      "    group by RepOrdiniClienti.ID_RepOrdiniClienti" +
      "    having count(RepOrdiniClienti.ID_RepOrdiniClienti) > 1" +
      "    FOR XML PATH('ORDER'), ROOT('ORDERS')"

    println(s"Run XPath query on XML node.")

    var xml = DatabaseClient.getXmlResult(Databases.Fashion, query).get.head
    val nodeSeq = xml \ "ORDER" \ "ITEM"
    for (node <- nodeSeq) {
      println(s"Item - Codice10 : ${(node \ "codice10").text}\tListini_ID : ${(node \ "Listini_ID").text}\tPrezzoUnit : ${(node \ "PrezzoUnit").text}")
    }
  }

  "A Repository object will be created that" should "instantiate an Oracle DB driver and return pretty print XML DIRECTLY" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.runOracleTests)

    val query = "SELECT XMLElement(\"ORDERS\", XMLAGG(XMLElement(\"ORDER\"," +
      "XMLForest(c.partnumber AS SKU, t.name AS trade_position, op.currency, op.price, i.quantity AS inventory_quantity)))) " +
      "AS \"RESULT\" " +
      "FROM " +
      "wcint01_owner.catentry c, wcint01_owner.tradeposcn t, wcint01_owner.offer o, " +
      "wcint01_owner.offerprice op, wcint01_owner.inventory i " +
      "WHERE c.catentry_id = o.catentry_id " +
      "AND c.catentry_id = i.catentry_id " +
      "AND o.TRADEPOSCN_ID = t.TRADEPOSCN_ID " +
      "AND o.offer_id = op.offer_id " +
      "AND t.name LIKE 'Moncler%'  FETCH NEXT 10 ROWS ONLY"

    println(s"Run query[$query] using DatabaseClient.getXmlResult and pretty print DIRECTLY in one line")

    println(new PrettyPrinter(80, 2).format(DatabaseClient.getXmlResult(Databases.OracleWcsStaging, query).get.head))

  }

  "A Repository object will be created that" should "instantiate an Oracle DB driver and return XML" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.runOracleTests)

    println("Creating Repository object with an Oracle DB driver.")

    val query = "SELECT XMLElement(\"ORDERS\", XMLAGG(XMLElement(\"ORDER\"," +
      "XMLForest(c.partnumber AS SKU, t.name AS trade_position, op.currency, op.price, i.quantity AS inventory_quantity)))) " +
      "AS \"RESULT\" " +
      "FROM " +
      "wcdev02_owner.catentry c, wcdev02_owner.tradeposcn t, wcdev02_owner.offer o, " +
      "wcdev02_owner.offerprice op, wcdev02_owner.inventory i " +
      "WHERE c.catentry_id = o.catentry_id " +
      "AND c.catentry_id = i.catentry_id " +
      "AND o.TRADEPOSCN_ID = t.TRADEPOSCN_ID " +
      "AND o.offer_id = op.offer_id " +
      "AND t.name LIKE 'Moncler%'  FETCH NEXT 10 ROWS ONLY"

    println(s"Run query[$query] using DatabaseClient.getXmlResult")

    val client: RepositoryClient[ResultSet] = Try(new DatabaseClient(
      connectionString = "jdbc:oracle:thin:@10.224.18.201:1521:YXDWCS01",
      driver = "oracle.jdbc.driver.OracleDriver",
      username = "WCDEV02_FUNTST_STG_RO",
      password = "oxyhydr0chrg3"
    )) match {
      case Failure(ex) => throw ex
      case Success(x) => x
    }

    val xml: Node = client.run(query, DatabaseClient.getXmlResult) match {
      case Failure(ex) =>
        throw ex
      case Success(item: List[_]) if item.head.isInstanceOf[Node] =>
        item.head
      case _ => throw new Error("Unmatched case")
    }
    println(new PrettyPrinter(80, 2).format(xml))
  }

  "A Repository object will be created that" should "create an Item List" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.runOracleTests)

    println("Creating Repository object with an Oracle DB driver.")

    val schema_stg_owner = "wcdev01_stg_owner"
    val query = "select * from" +
      "(select c.partnumber as SKU, t.name as trade_position, op.currency, op.price, i.quantity as inventory_quantity from " +
      s"$schema_stg_owner.catentry c, $schema_stg_owner.tradeposcn t, $schema_stg_owner.offer o, $schema_stg_owner.offerprice op, $schema_stg_owner.inventory i " +
      "where c.catentry_id = o.catentry_id " +
      "and c.catentry_id = i.catentry_id " +
      "and o.TRADEPOSCN_ID = t.TRADEPOSCN_ID " +
      "and o.offer_id = op.offer_id " +
      s"and t.name like 'Moncler_%' " +
      "order by DBMS_RANDOM.RANDOM) " +
      "where ROWNUM = 1"

    val clientDBName = new DatabaseClient(
      connectionString = "jdbc:oracle:thin:@10.224.18.201:1521:YXDWCS01",
      driver = "oracle.jdbc.driver.OracleDriver",
      username = "WCDEV01_FUNTST_STG_RO",
      password = "s0larch2rg3r1"
    )

    val client: RepositoryClient[ResultSet] = Try(clientDBName) match {
      case Failure(ex) => throw ex
      case Success(x) => x
    }

    case class Item(
                     sku: Option[String] = None,
                     currency: Option[String] = None,
                     price: Option[String] = None,
                     invQty: Option[String] = None,
                     save: (Item) => Item = identity[Item]
                   )

    //Item.productPrefix

    def myCustomList(rs: ResultSet): Try[List[Item]] = {
      Try {
        val buildCustomList = () =>
          Item(sku = Some(rs.getString("SKU")),
            currency = Some(rs.getString("CURRENCY")),
            price = Some(rs.getString("PRICE")),
            invQty = Some(rs.getString("INVENTORY_QUANTITY")))
        Iterator.continually(rs.next()).takeWhile(identity).map(_ => buildCustomList()).toList
      }
    }

    println(s"Run query[$query] using myCustomList")
    val myList = client.run(query, myCustomList) match {
      case Failure(ex) => throw ex
      case Success(list) => list
    }
    println("Custom Function myCustomList() produces a list of Item objects: " + myList)

  }

  "A Repository object will be created for AUTOMA DB that" should "instantiate a DB driver correctly and transform a sample SQL query using getMapList" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    val query = "SELECT PRG_Modello AS MODEL, PRG_Colore AS COLOR, EAN_Fornitore AS EAN\nFROM Fashion_Binding\nWHERE Barcode = '1000000015598785'"

    println(s"Run query[$query] using DatabaseClient.getMapList")
    var res = for {
      client <- DatabaseClient.getClient(Databases.AUTOMA) //.FrontendMultibrand)
      result <- client.run(query, DatabaseClient.getMapList)
    } yield result
    res match {
      case Failure(ex) =>
        println(s"Failed. Reason: ${Databases.AUTOMA}")
        throw ex
      case item => item.foreach(println)
    }
  }

  "A Repository object will be created that" should "show how to return data from a getMapList" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    val query = "SELECT PRG_Modello AS MODEL, PRG_Colore AS COLOR, EAN_Fornitore AS EAN\nFROM Fashion_Binding\nWHERE Barcode = '1000000015598785'"
    println(s"Run query[$query] using DatabaseClient.getMapList helper function")
    // Use DatabaseClient "helper" function to create a connection and run the query
    var res = DatabaseClient.getMapList(DatabaseClient.getClient(Databases.AUTOMA), query)
    // Test the result for Success or Failure
    res match {
      case Failure(ex) =>
        // Print or "throw" the exception
        println(s"DB error : ${ex.getLocalizedMessage}")
      case Success(records) =>
        // DatabaseClient.getMapList returns a "Success" containing a List() of Map() objects
        for (record <- records) {
          // Print out each DB data field in the Map object by key name
          println(s"MODEL [${record.get("MODEL")}]")
          println(s"COLOR [${record.get("COLOR")}]")
          println(s"EAN [${record.get("EAN")}]")
        }
    }
  }

  "A Repository object will be created that" should "show how to return data from a getJsonList" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    val query = "SELECT top 5 * FROM Nazioni"

    println(s"Run query[$query] using DatabaseClient.getJsonList")
    // Use DatabaseClient "helper" function to create a connection and run the query
    var res = DatabaseClient.getJsonList(DatabaseClient.getClient(Databases.Fashion), query)
    // Test the result for Success or Failure
    res match {
      case Failure(ex) =>
        // Print or "throw" the exception
        println(s"DB error : ${ex.getLocalizedMessage}")
      case Success(records) =>
        // DatabaseClient.getJsonList returns a "Success" containing a List() of JSON objects
        records.foreach {
          item =>
            println("Nome : " + DatabaseClient.parseJsonForField(item.toString, "Nome"))
            println("ID_Nazioni : " + DatabaseClient.parseJsonForField(item.toString, "ID_Nazioni"))
            println("CodiceAuto : " + DatabaseClient.parseJsonForField(item.toString, "CodiceAuto"))
            println("Cliente_MagazziniFisici_ID : " + DatabaseClient.parseJsonForField(item.toString, "Cliente_MagazziniFisici_ID"))
        }
    }
  }

  "A Repository object will be created that" should "show how to return data from a getCachedRowSet" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    val query = "SELECT top 5 * FROM Nazioni"

    println(s"Run query[$query] using DatabaseClient.getCachedRowSet")
    // Use DatabaseClient "helper" function to create a connection and run the query
    val client =  DatabaseClient.getClient(Databases.Fashion)
    var res = DatabaseClient.getCachedRowSet(client, query)

    // Test the result for Success or Failure
    res match {
      case Failure(ex) =>
        // Print or "throw" the exception
        println(s"DB error : ${ex.getLocalizedMessage}")
      case Success(rs) =>
        // DatabaseClient.getJsonList returns a "Success" containing a List() of JSON objects
        while (rs.next()) {
          println("Nome : " + rs.getString("Nome"))
          println("ID_Nazioni : " + rs.getString("ID_Nazioni"))
          println("CodiceAuto : " + rs.getString("CodiceAuto"))
          println("Cliente_MagazziniFisici_ID : " + rs.getString("Cliente_MagazziniFisici_ID"))
        }
    }
  }

  "A Repository object will be created using an explicit username/password that" should "show how to return data from a getCachedRowSet" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    // Create a DB client connection specifying username and password explicitly
    val client: RepositoryClient[ResultSet] = Try(new DatabaseClient(
      connectionString = "jdbc:sqlserver://viking.be.integ.yoox.net;DatabaseName=fashion;",
      username = "yooxcrmtools",
      password = "y00xCRMt00ls"
    )) match {
      case Failure(ex) => throw ex
      case Success(x) => x
    }
    // Using the client connection run a query and return a CacherRowSet
    // The same approach can be applied to the getMapList and getJsonList transformation methods
    val query = "SELECT top 5 * FROM Nazioni"
    var res = client.run(query, DatabaseClient.getCachedRowSet)
    // Test the result for Success or Failure
    res match {
      case Failure(ex) =>
        // Print or "throw" the exception
        println(s"DB error : ${ex.getLocalizedMessage}")
      case Success(rs) =>
        // DatabaseClient.getJsonList returns a "Success" containing a List() of JSON objects
        while (rs.next()) {
          println("Nome : " + rs.getString("Nome"))
          println("ID_Nazioni : " + rs.getString("ID_Nazioni"))
          println("CodiceAuto : " + rs.getString("CodiceAuto"))
          println("Cliente_MagazziniFisici_ID : " + rs.getString("Cliente_MagazziniFisici_ID"))
        }
    }
  }

  "A Repository CaseClass transformation will be used that" should "create a CaseClass List" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.runOracleTests)

    println("Creating Repository object with an Oracle DB driver.")

    val schema_stg_owner = "wcdev01_stg_owner"
    val query = "select * from" +
      "(select c.partnumber as SKU, t.name as trade_position, op.currency, op.price, i.quantity as inventory_quantity from " +
      s"$schema_stg_owner.catentry c, $schema_stg_owner.tradeposcn t, $schema_stg_owner.offer o, $schema_stg_owner.offerprice op, $schema_stg_owner.inventory i " +
      "where c.catentry_id = o.catentry_id " +
      "and c.catentry_id = i.catentry_id " +
      "and o.TRADEPOSCN_ID = t.TRADEPOSCN_ID " +
      "and o.offer_id = op.offer_id " +
      s"and t.name like 'Moncler_%' " +
      "order by DBMS_RANDOM.RANDOM) " +
      "where ROWNUM = 1"

    val clientDBName = new DatabaseClient(
      connectionString = "jdbc:oracle:thin:@10.224.18.201:1521:YXDWCS01",
      driver = "oracle.jdbc.driver.OracleDriver",
      username = "WCDEV01_FUNTST_STG_RO",
      password = "s0larch2rg3r1"
    )

    val client: RepositoryClient[ResultSet] = Try(clientDBName) match {
      case Failure(ex) => throw ex
      case Success(x) => x
    }

    case class Item(
                     sku: Option[String] = None,
                     currency: Option[String] = None,
                     price: Option[String] = None,
                     invQty: Option[String] = None,
                     save: (Item) => Item = identity[Item]
                   )

    def myCustomList(rs: ResultSet): Try[List[Item]] = {
      Try {
        val buildCustomList = () =>
          Item(sku = Some(rs.getString("SKU")),
            currency = Some(rs.getString("CURRENCY")),
            price = Some(rs.getString("PRICE")),
            invQty = Some(rs.getString("INVENTORY_QUANTITY")))
        Iterator.continually(rs.next()).takeWhile(identity).map(_ => buildCustomList()).toList
      }
    }

    println(s"Run query[$query] using myCustomList")
    val myList = client.run(query, myCustomList) match {
      case Failure(ex) => throw ex
      case Success(list) => list
    }
    println("Custom Function myCustomList() produces a list of Item objects: " + myList)

  }

  "A temporary table" should "be created and used in a JOIN to get Front End inventory" in {

    assume(GlobalSettings.doAllTests)

    def c10SizeList(rs: ResultSet): Try[List[(String, String, String)]] = {
      Try {
        val buildCustomList = () => {
          (rs.getString("Gtin"), rs.getString("Code10"), rs.getString("SizeOrder"))
        }
        Iterator.continually(rs.next()).takeWhile(identity).map(_ => buildCustomList()).toList
      }
    }

    val listgtins = Seq("0400000007991", "0400000023649", "0400000025353", "0400000028903", "0400000028941")
    var query1 =
      s"""
         |SELECT  Code10, SizeLabel, SizeOrder, Gtin, MAX(CreationDate) AS CreationDate
         |   FROM dbo.GTINArticleBinding
         |	   WHERE
         |   Gtin IN (${listgtins.mkString("'", "','", "'")})
         |     GROUP BY Code10, SizeLabel, SizeOrder, Gtin
             """.stripMargin
    var res = for {
      client <- DatabaseClient.getClient(Databases.Fashion)
      result <- client.run(query1, c10SizeList)
    } yield result
    var dataList = res match {
      case Failure(ex) =>
        println(s"Failed. Reason: ${Databases.Fashion}")
        throw ex
      case Success(items) => items.map { case (gtin, c10, size) => s"('$gtin','$c10','$size')" }
    }
    val c10SizeInsertData = dataList.mkString(",\n")
    println(s"c10SizeInsertData $c10SizeInsertData")
    val siteCode = "YOOX_IT"
    val warehouseId = 2715
    val queryList = Seq(
      s"""
          CREATE TABLE #GtinLookup (gtin VARCHAR(100),c10 VARCHAR(100),size VARCHAR(100))
      """.stripMargin,
      s"""
          INSERT INTO #GtinLookup (gtin, c10, size) VALUES $c10SizeInsertData
      """.stripMargin,
      s"""
              SELECT
                   rg.qtagiacenza as Supply,
                   rg.qtaimpegnata as Demand,
                   m.Warhouse_ID as WarehouseId,
         				   r.Codice as c10,
         				   rg.ordinetaglia as sizeLabel,
                   gt.gtin as gtin
               FROM RepArticolo AS r (NOLOCK)
                   INNER JOIN Listini AS l (NOLOCK) ON l.RepArticolo_ID = r.ID_RepArticolo
                   INNER JOIN Repgiacenze AS rg (NOLOCK) ON rg.reparticolo_id = r.reparticolo_parent_id
                   INNER JOIN MarketWarehouseBinding AS m (NOLOCK) ON rg.mf_id = m.warhouse_id
                   INNER JOIN #GtinLookup as gt ON c10 = r.Codice AND size = rg.ordinetaglia
               WHERE
                   l.Mercati_ID = (SELECT  Mercati_ID FROM Nazioni WHERE siteCode='$siteCode')
                   AND m.Market_ID = (SELECT  Mercati_ID FROM Nazioni WHERE siteCode='$siteCode')
                   AND m.Division_ID = (SELECT  Division_ID FROM Nazioni WHERE siteCode='$siteCode')
         				   AND m.Warhouse_ID = $warehouseId
      """.stripMargin
    )
    val rs = DatabaseClient.getMapList(DatabaseClient.getFrontendDatabaseEnumeration(siteCode), queryList)
    println(s"rs $rs")
    rs match {
      case Failure(ex) =>
        println(s"DB error : ${ex.getLocalizedMessage}")
      case Success(maplist) =>
        for (item <- maplist) {
          println("gtin : " + item.get("gtin"))
          println("c10 : " + item.get("c10"))
          println("sizeLabel : " + item.get("sizeLabel"))
          println("WarehouseId : " + item.get("WarehouseId"))
          println("Supply : " + item.get("Supply"))
          println("Demand : " + item.get("Demand"))
        }
    }
  }

  "A Repository object will be created that" should "show how to return a list of the name field" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(GlobalSettings.doAllTests)

    val query = "SELECT top 5 * FROM Nazioni"

    println(s"Run query[$query] using DatabaseClient.getFieldList")
    // Use DatabaseClient "helper" function to create a connection and run the query
    var res = DatabaseClient.getFieldList[String](Databases.Fashion, query, "Nome")
    // Test the result for Success or Failure
    res match {
      case Failure(ex) =>
        // Print or "throw" the exception
        println(s"DB error : ${ex.getLocalizedMessage}")
      case Success(list) =>
        // DatabaseClient.getFieldList returns a "Success" containing a List() of strings
        println(s"Nome : $list")
        assert(list.tail.head == "Italy")
    }
    var resInts = DatabaseClient.getFieldList[Int](Databases.Fashion, query, "Mercati_ID")
    resInts match {
      case Failure(ex) =>
        println(s"DB error : ${ex.getLocalizedMessage}")
      case Success(list) =>
        println(s"Mercati_ID (as Ints): $list")
        assert(list.tail.head == 2)
    }

  }

//  "This method will take siteCode as pameter" should "return resultset containing shipNode" taggedAs(ApiTestTag, LinuxTestTag) in {
//
//    assume(GlobalSettings.doAllTests)
//
//    val siteCode = "THEOUTNET_US"
//    val rs = DatabaseFacilities.getShipNodeRSBySiteCode(siteCode)
//
//    val listOfShipNodes = rs.toCollection("ShipNode")
//    println(s"shipNodes for  $siteCode: $listOfShipNodes")
//    assert(listOfShipNodes.size() > 0, s"ShipNode not found for $siteCode")
//  }

  "Get configuration parameter using getMyConnection" should "get a configuration value from the specified file path" taggedAs (ApiTestTag) in {

    assume(true)

    val client: RepositoryClient[ResultSet] = DatabaseClient.getMyConnection(clientRequestMonclerFerrari) match {
      case Failure(ex) => throw ex
      case Success(x) => x
    }

    val query = "select c.partnumber as SKU, t.name as trade_position, op.currency, op.price, i.quantity as inventory_quantity from " +
      "wcint01_owner.catentry c, wcint01_owner.tradeposcn t, wcint01_owner.offer o, wcint01_owner.offerprice op, wcint01_owner.inventory i " +
      "where c.catentry_id = o.catentry_id " +
      "and c.catentry_id = i.catentry_id " +
      "and o.TRADEPOSCN_ID = t.TRADEPOSCN_ID " +
      "and o.offer_id = op.offer_id " +
      "and t.name like 'Moncler%' FETCH NEXT 10 ROWS ONLY"

    println(s"Run query: $query \n using DatabaseClient.printDataList")
    client.run(query, DatabaseClient.printDataList) match {
      case Failure(ex) =>
        println(s"Failed. Reason: ${clientRequestMonclerFerrari}")
        throw ex
      case Success(_) => ()
    }

  }

}