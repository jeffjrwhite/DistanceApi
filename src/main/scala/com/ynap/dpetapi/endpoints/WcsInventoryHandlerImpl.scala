package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.implicits._
import com.ynap.dpetapi.database.DatabaseClient
import com.ynap.dpetapi.endpoints.definitions.InventoryResponse
import com.ynap.dpetapi.endpoints.wcsInventory.{GetWcsInventoryResponse, WcsInventoryHandler}

import scala.util.{Failure, Success}

class WcsInventoryHandlerImpl[F[_] : Applicative]() extends WcsInventoryHandler[F] {

  override def getWcsInventory(respond: GetWcsInventoryResponse.type)(
    gtin: Iterable[String],
    warehouse: String,
    division: String,
    pageNumber: Option[Int],
    pageSize: Option[Int]
  ): F[GetWcsInventoryResponse] = {

    val schema_owner = DatabaseClient.getSchemaOwner(division)
    val query = s"""
                  |    SELECT CE.partnumber AS gtin, INV.QUANTITY AS Supply FROM $schema_owner.INVENTORY INV
                  |          LEFT JOIN $schema_owner.CATENTRY CE ON CE.CATENTRY_ID = INV.CATENTRY_ID
                  |          LEFT JOIN $schema_owner.FFMCENTER FFM ON INV.FFMCENTER_ID = FFM.FFMCENTER_ID
                  |          LEFT JOIN $schema_owner.STORE ST ON ST.FFMCENTER_ID = INV.FFMCENTER_ID
                  |          WHERE FFM.NAME = '$warehouse'
                  |            AND CE.partnumber IN (${gtin.mkString("'","','","'")})
                  |          ORDER BY 1
       """.stripMargin
    var res = for {
      client <- DatabaseClient.getDbClient(division)
      result <- client.run(query, DatabaseClient.getJsonList)
    } yield
      result

    res match {
      case Failure(ex) => throw ex
      case Success(rs) =>
        for {
          inventory <- Some(rs).pure[F]
        } yield
          respond.Ok(InventoryResponse(Some(rs.length), Some(division), Some(warehouse), inventory))
    }
  }

}