package com.ynap.dpetapi.endpoints

import java.sql.ResultSet

import cats.Applicative
import cats.effect.IO
import cats.implicits._
import com.ynap.dpetapi.endpoints.divisions.{DivisionsHandler, GetDivisionsResponse}
import com.ynap.dpetapi.endpoints.definitions.DivisionsResponse
import com.ynap.dpetapi._
import com.ynap.dpetapi.endpoints.divisions.GetDivisionsResponse.Ok
import doobie.util.transactor.Transactor

import scala.util.Try

// account model
//case class Division(ID_Divisione: String, Descrizione: String, timestamp: Long)

//class DivisionsHandlerImpl[F[_] : Applicative](xa: Transactor[IO]) extends DivisionsHandler[F] {

//  override def getDivisions(respond: GetDivisionsResponse.type)(): F[GetDivisionsResponse] = {
//    /**
//     * Implicit classes and imports used for JSON "pretty" printing
//     */
////    implicit val formats = net.liftweb.json.DefaultFormats
////    import net.liftweb.json.Serialization.write
//    //val json: List[String] =
////    for {
////      divisionList <- DivisionQuery.search().to[List]
////      //division <- divisionList
////    } yield {
////      val jsonStr = "" //write(division)
////      jsonStr
////      divisionList
////      respond.Ok(DivisionsResponse(jsonmsg = "{}"))
////    }
//
//    //respond.Ok(DivisionsResponse(json.mkString("[",",","]")))
//
//    for {
//      message <- s"Hello, ${name.getOrElse("world")}".pure[F]
//    } yield respond.Ok(DivisionsResponse(message))
//
//  }
////
//  override def getDivision(id: String) = {
//    DivisionQuery.searchWithId(id).option.transact(xa)
//  }
//
//  override def getDivisions() = {
//    DivisionQuery.search().to[List].transact(xa)
//  }
class DivisionsHandlerImpl[F[_] : Applicative]() extends DivisionsHandler[F] {
  override def getDivisions(respond: GetDivisionsResponse.type)(): F[GetDivisionsResponse] = {
    for {
      message <- s"Divisions".pure[F]
    } yield respond.Ok(DivisionsResponse(message))
  }


}