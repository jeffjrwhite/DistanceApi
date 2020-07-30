package com.ynap.dpetapi

import doobie.{Fragments, _}
import doobie.implicits._
import cats.data._

case class Inventory(gtin: String, supply: Int)

object InventoryQuery {

  // search account
  def search(gtin: Option[String],
             warehouse: Option[String],
             pageNumber: Option[Int],
             pageSize: Option[Int]): doobie.Query0[Inventory] = {

    (gtin, warehouse, pageNumber, pageSize) match {
      case (Some(gtinList), Some(warehouseCode), Some(pageNum), Some(pageSiz)) =>
        val from = (pageNum -1) * pageSiz
        val to = from + pageSiz
        sql"""
             |         SELECT gtin, COUNT(CodiceMatricola) AS supply
             |         FROM vw_GTINArticleBinding gab WITH (NOLOCK)
             |         INNER JOIN RepArticolo ra WITH (NOLOCK) ON ra.Codice = gab.Code10
             |         INNER JOIN Matricole m WITH (NOLOCK) ON ra.ID_RepArticolo = m.Articolo_ID AND gab.SizeOrder = m.OrdineTaglia
             |         INNER JOIN magazzinifisici mf WITH (NOLOCK) ON  mf.ID_MagazziniFisici = m.Mag2_ID
             |         INNER JOIN Fornitori fo WITH (NOLOCK) ON fo.ID_Fornitori = m.Fornitori_ID
             |         INNER JOIN LogisticHub lh WITH (NOLOCK) ON mf.LogisticHub_ID = lh.ID_LogisticHub
             |         INNER JOIN Repgiacenze AS rg (NOLOCK)
             |         		ON rg.reparticolo_id = ra.reparticolo_parent_id
             |         		  AND rg.MF_ID = m.Mag1_ID
             |         		  AND rg.OrdineTaglia = m.OrdineTaglia
             |         WHERE gtin IN ('${gtinList}')
             |         AND lh.code = '$warehouseCode'
             |         AND m.stato IN (23)
             |           GROUP BY gtin, m.OrdineTaglia
             |          ORDER BY 1
       """.stripMargin
          .query[Inventory]
      case (Some(gtinList), Some(warehouseCode), _, _) =>
        // Import some convenience combinators.
        import Fragments.{ in, whereAndOpt }
        import doobie.implicits._
        import cats.implicits._
        val s = s"""
            |         SELECT gtin, COUNT(CodiceMatricola) AS supply
            |         FROM vw_GTINArticleBinding gab WITH (NOLOCK)
            |         INNER JOIN RepArticolo ra WITH (NOLOCK) ON ra.Codice = gab.Code10
            |         INNER JOIN Matricole m WITH (NOLOCK) ON ra.ID_RepArticolo = m.Articolo_ID AND gab.SizeOrder = m.OrdineTaglia
            |         INNER JOIN magazzinifisici mf WITH (NOLOCK) ON  mf.ID_MagazziniFisici = m.Mag2_ID
            |         INNER JOIN Fornitori fo WITH (NOLOCK) ON fo.ID_Fornitori = m.Fornitori_ID
            |         INNER JOIN LogisticHub lh WITH (NOLOCK) ON mf.LogisticHub_ID = lh.ID_LogisticHub
            |         INNER JOIN Repgiacenze AS rg (NOLOCK)
            |         		ON rg.reparticolo_id = ra.reparticolo_parent_id
            |         		  AND rg.MF_ID = m.Mag1_ID
            |         		  AND rg.OrdineTaglia = m.OrdineTaglia
            |         WHERE gtin IN ('${gtinList}')
            |         AND lh.code = '$warehouseCode'
            |         AND m.stato IN (23)
            |           GROUP BY gtin, m.OrdineTaglia
            |          ORDER BY 1
          """.stripMargin
        val q = sql"""$s"""
//        val list = List(gtinList)
//        //val f1 = list.map(n => fr"$n").intercalate(fr",") //
//        val f1 = list.toNel.map(cs => in(fr"gtin", cs)) //gtin.map(s => fr"gtin IN ($s)")
//        val f2 = warehouse.map(s => fr"lh.code = '$s'")
//        val q = fr"""
//         SELECT top 10 gtin, COUNT(CodiceMatricola) AS supply
//         FROM vw_GTINArticleBinding gab
//         INNER JOIN RepArticolo ra ON ra.Codice = gab.Code10
//         INNER JOIN Matricole m ON ra.ID_RepArticolo = m.Articolo_ID AND gab.SizeOrder = m.OrdineTaglia
//         INNER JOIN magazzinifisici mf ON mf.ID_MagazziniFisici = m.Mag2_ID
//         INNER JOIN LogisticHub lh ON mf.LogisticHub_ID = lh.ID_LogisticHub""" ++
//         whereAndOpt(f1, f2) ++
//         fr"""
//         AND m.stato IN (23)
//         GROUP BY gtin, m.OrdineTaglia
//         ORDER BY 1
//        """.stripMargin
        q.query[Inventory]
      case (_, _, _, _) =>
        throw(new RuntimeException("Invalid case"))

    }
  }

}