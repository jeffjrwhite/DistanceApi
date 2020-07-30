package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.effect.IO
import cats.implicits._
import com.ynap.dpetapi._
import com.ynap.dpetapi.endpoints.definitions.{DivisionsResponse, InventoryResponse}
import com.ynap.dpetapi.endpoints.divisions.{DivisionsHandler, GetDivisionsResponse}
import com.ynap.dpetapi.endpoints.inventory.{GetWmsInventoryResponse, InventoryHandler}
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.circe.Encoder
import io.circe.syntax._

class InventoryHandlerImpl[F[_] : Applicative](xa: Transactor[IO]) extends InventoryHandler[F] {

  implicit val encodeFieldType: Encoder[Inventory] =
    Encoder.forProduct2("gtin", "supply")(Inventory.unapply(_).get)

  override def getWmsInventory(respond: GetWmsInventoryResponse.type)(
    gtin: Option[String],
    warehouse: Option[String],
    pageNumber: Option[Int],
    pageSize: Option[Int]
  ): F[GetWmsInventoryResponse] = {

    var jsonList: List[io.circe.Json] = for {
      inventory <- InventoryQuery.search(gtin, warehouse, pageNumber, pageSize).to[List].transact(xa).unsafeRunSync
    } yield {
      inventory.asJson
    }
    for {
      list <- jsonList.toIndexedSeq.pure[F]
    } yield
      respond.Ok(InventoryResponse(Some(list.length), warehouse, Some(list)))

  }

}