package com.ynap.dpetapi

import doobie.implicits._

case class Division(ID_Divisione: String, Descrizione: String)

object DivisionQuery {

  // search account
  def search(id: Option[String]): doobie.Query0[Division] = {

    id match {
      case Some(divId) =>
        sql"""
             |SELECT ID_Divisione, Descrizione FROM Divisione
             |WHERE ID_Divisione = ${divId}
       """.stripMargin
          .query[Division]
      case None =>
          sql"""
               |SELECT ID_Divisione, Descrizione FROM Divisione
       """.stripMargin
            .query[Division]
    }
  }

}