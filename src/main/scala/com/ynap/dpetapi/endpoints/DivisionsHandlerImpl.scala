package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.effect.IO
import cats.implicits._
import com.ynap.dpetapi.endpoints.divisions.{DivisionsHandler, GetDivisionsResponse}
import com.ynap.dpetapi.endpoints.definitions.DivisionsResponse
import com.ynap.dpetapi._
import doobie.util.transactor.Transactor
import io.circe.Encoder
import io.circe.syntax._

import scala.util.{Failure, Success, Try}

class DivisionsHandlerImpl[F[_] : Applicative](xa: Transactor[IO]) extends DivisionsHandler[F] {
  override def getDivisions(respond: GetDivisionsResponse.type)(): F[GetDivisionsResponse] = {
    import doobie.implicits._
//    /**
//     * Implicit classes and imports used for JSON "pretty" printing
//     */
//    implicit val formats = net.liftweb.json.DefaultFormats
//    import net.liftweb.json.Serialization.write

    implicit val encodeFieldType: Encoder[Division] =
      Encoder.forProduct2("id", "name")(Division.unapply(_).get)

    //val rs2 = DivisionQuery.search().to[List].transact(xa).unsafeRunSync.tail.head.Descrizione
    var jsonList = for {
      division <- DivisionQuery.search().to[List].transact(xa).unsafeRunSync
    } yield {
      val jsonStr = division.asJson.noSpaces
      jsonStr
    }

    val isJson: IndexedSeq[io.circe.Json] = for( a <- 1 to jsonList.length-1) yield jsonList(a)
//    for {
//      list <- jsonList //.pure[F]
//      //message <- list.asJson
//      //s"${jsonList.mkString("[", ",", "]")}".pure[F]
//      //Option[IndexedSeq[io.circe.Json]]
//    } yield
      respond.Ok(DivisionsResponse(Some(isJson)))
  }

}