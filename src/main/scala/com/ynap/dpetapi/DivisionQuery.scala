package com.ynap.dpetapi

import doobie.implicits._

case class Division(ID_Divisione: String, Descrizione: String, timestamp: Long)

object DivisionQuery {

  // search account
  def search(): doobie.Query0[Division] = {
    sql"""
         |SELECT ID_Divisione, Descrizione FROM Fashion.Divisione
       """.stripMargin
      .query[Division]
  }

  // search with id
  def searchWithId(id: String): doobie.Query0[Division] = {
    sql"""
         |SELECT ID_Divisione, Descrizione FROM Fashion.Divisione
         |WHERE ID_Divisione = $id
       """.stripMargin
      .query[Division]
  }

}