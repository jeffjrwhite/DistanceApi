package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.implicits._
import com.ynap.dpetapi.database.DatabaseClient
import com.ynap.dpetapi.database.DatabaseClient.Databases
import com.ynap.dpetapi.endpoints.definitions.InventoryResponse
import com.ynap.dpetapi.endpoints.wmsInventory.{GetWmsInventoryResponse, WmsInventoryHandler}
import scala.util.{Failure, Success}

class WmsInventoryHandlerImpl[F[_] : Applicative]() extends WmsInventoryHandler[F] {

  override def getWmsInventory(respond: GetWmsInventoryResponse.type)(
    gtin: Iterable[String],
    warehouse: String,
    division: String,
    pageNumber: Option[Int],
    pageSize: Option[Int]
  ): F[GetWmsInventoryResponse] = {

    val query = s"""
                  |         SELECT gtin, COUNT(CodiceMatricola) AS supply
                  |         FROM vw_GTINArticleBinding gab WITH (NOLOCK)
                  |         INNER JOIN RepArticolo ra WITH (NOLOCK) ON ra.Codice = gab.Code10
                  |         INNER JOIN Matricole m WITH (NOLOCK) ON ra.ID_RepArticolo = m.Articolo_ID AND gab.SizeOrder = m.OrdineTaglia
                  |         INNER JOIN magazzinifisici mf WITH (NOLOCK) ON  mf.ID_MagazziniFisici = m.Mag2_ID
                  |         INNER JOIN Fashion.dbo.Divisione d ON ra.Divisione_ID = d.ID_Divisione
                  |         INNER JOIN Fornitori fo WITH (NOLOCK) ON fo.ID_Fornitori = m.Fornitori_ID
                  |         INNER JOIN LogisticHub lh WITH (NOLOCK) ON mf.LogisticHub_ID = lh.ID_LogisticHub
                  |         INNER JOIN Repgiacenze AS rg (NOLOCK)
                  |         		ON rg.reparticolo_id = ra.reparticolo_parent_id
                  |         		  AND rg.MF_ID = m.Mag1_ID
                  |         		  AND rg.OrdineTaglia = m.OrdineTaglia
                  |         WHERE gtin IN (${gtin.mkString("'","','","'")})
                  |         AND lh.code = '${warehouse}'
                  |         AND d.Descrizione IN ('${division}')
                  |         AND m.Mag1_ID = m.Mag2_ID
                  |         AND m.stato IN (23)
                  |           GROUP BY gtin, m.OrdineTaglia
                  |          ORDER BY 1
       """.stripMargin
    var res = for {
      client <- DatabaseClient.getClient(Databases.Fashion)
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