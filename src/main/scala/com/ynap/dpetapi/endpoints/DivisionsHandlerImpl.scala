package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.implicits._
import com.ynap.dpetapi.database.DatabaseClient.Databases
import scala.util.{Failure, Success}
import com.ynap.dpetapi.endpoints.divisions.{DivisionsHandler, GetDivisionsResponse}
import com.ynap.dpetapi.database.DatabaseClient
import com.ynap.dpetapi.endpoints.definitions.DivisionsResponse
import io.circe.Encoder
import io.circe.syntax._

case class Division(id: Int, name: String, shortname: String, active: Boolean)

class DivisionsHandlerImpl[F[_] : Applicative]() extends DivisionsHandler[F] {

  implicit val encodeFieldType: Encoder[Division] =
    Encoder.forProduct4("id", "name", "shortname", "active")(Division.unapply(_).get)

  override def getDivisions(respond: GetDivisionsResponse.type)(
    id: Option[Int],
    name: Option[String],
    pageNumber: Option[Int],
    pageSize: Option[Int]
  ): F[GetDivisionsResponse] = {

    val query =
      """SELECT ID_Divisione as id, Descrizione as name, ShortName, Attivo as active
                       FROM Divisione
                         ORDER BY 1
       """.stripMargin
    var res = for {
      client <- DatabaseClient.getClient(Databases.Fashion)
      result <- client.run(query, DatabaseClient.getJsonList)
    } yield
      result
    val jsonList:List[String] = res match {
      case Failure(ex) => throw ex
      case Success(rs) =>
        rs
    }
    var json: List[io.circe.Json] = for {
      division <- jsonList
    } yield {
      division.asJson
    }
    for {
      divisions <- Some(json.toIndexedSeq).pure[F]
    } yield
      respond.Ok(DivisionsResponse(Some(json.length), divisions))
  }

}