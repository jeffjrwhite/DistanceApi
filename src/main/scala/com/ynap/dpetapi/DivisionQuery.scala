package com.ynap.dpetapi

import doobie.implicits._

case class Division(id: Int, name: String)

object DivisionQuery {

  // search account
  def search(id: Option[String], name: Option[String]): doobie.Query0[Division] = {

    (id, name) match {
      case (Some(divId), _) =>
        sql"""
             |SELECT ID_Divisione as id, Descrizione as name FROM Divisione
             |WHERE ID_Divisione = ${divId}
       """.stripMargin
          .query[Division]
      case (None, Some(namePattern)) =>
        sql"""
             |SELECT ID_Divisione as id, Descrizione as name FROM Divisione
             |WHERE Descrizione like ${namePattern}
       """.stripMargin
          .query[Division]
      case (None, None) =>
          sql"""
               |SELECT ID_Divisione as id, Descrizione as name FROM Divisione
       """.stripMargin
            .query[Division]
    }
  }

}