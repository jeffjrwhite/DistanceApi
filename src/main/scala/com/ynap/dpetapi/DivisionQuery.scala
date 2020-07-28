package com.ynap.dpetapi

import doobie.implicits._

case class Division(id: Int, name: String, shortname: String, active: Boolean)

object DivisionQuery {

  // search account
  def search(id: Option[Int],
             name: Option[String],
             pageNumber: Option[Int],
             pageSize: Option[Int]): doobie.Query0[Division] = {

    (id, name, pageNumber, pageSize) match {
      case (Some(divId), _, _, _) =>
        sql"""
             |SELECT ID_Divisione as id, Descrizione as name, ShortName, Attivo as active FROM Divisione
             |WHERE ID_Divisione = ${divId}
             |    ORDER BY 1
       """.stripMargin
          .query[Division]
      case (None, Some(namePattern), _, _) =>
        sql"""
             |SELECT ID_Divisione as id, Descrizione as name, ShortName, Attivo as active FROM Divisione
             |WHERE Descrizione like ${namePattern}
             |    ORDER BY 1
       """.stripMargin
          .query[Division]
      case (None, None, Some(pageNum), Some(pageSiz)) =>
        sql"""
               |SELECT ID_Divisione as id, Descrizione as name, ShortName, Attivo as active
               |    FROM (
               |    SELECT ID_Divisione, Descrizione, ShortName, Attivo,
               |        ROW_NUMBER() OVER (ORDER BY ID_Divisione, Descrizione, ShortName) AS RowNumber
               |        FROM Divisione) Divisione
               |    WHERE RowNumber > (${pageNumber.get}-1)*${pageSize.get}
               |        AND RowNumber <= ${pageNumber.get}*${pageSize.get}
               |    ORDER BY 1
       """.stripMargin
          .query[Division]
      case (None, None, _, _) =>
        sql"""
             |    SELECT ID_Divisione as id, Descrizione as name, ShortName, Attivo as active
             |        FROM Divisione
             |    ORDER BY 1
       """.stripMargin
          .query[Division]

    }
  }

}