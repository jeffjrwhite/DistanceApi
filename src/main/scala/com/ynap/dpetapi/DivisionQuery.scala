package com.ynap.dpetapi

import doobie.implicits._

case class Division(ID_Divisione: String, Descrizione: String)

object DivisionQuery {

  // search account
  def search(): doobie.Query0[Division] = {
    sql"""
         |SELECT ID_Divisione, Descrizione FROM Divisione
       """.stripMargin
      .query[Division]
  }

  // search with id
  def searchWithId(id: String): doobie.Query0[Division] = {
    sql"""
         |SELECT ID_Divisione, Descrizione FROM Divisione
         |WHERE ID_Divisione = $id
       """.stripMargin
      .query[Division]
  }

}