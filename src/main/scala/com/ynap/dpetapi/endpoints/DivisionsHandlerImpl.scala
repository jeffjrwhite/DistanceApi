package com.ynap.dpetapi.endpoints

import cats.Applicative
import cats.effect.IO
import cats.implicits._
import com.ynap.dpetapi.endpoints.divisions.{DivisionsHandler, GetDivisionsResponse}
import com.ynap.dpetapi.endpoints.definitions.DivisionsResponse
import com.ynap.dpetapi._
import doobie.util.transactor.Transactor

class DivisionsHandlerImpl[F[_] : Applicative](xa: Transactor[IO]) extends DivisionsHandler[F] {
  override def getDivisions(respond: GetDivisionsResponse.type)(): F[GetDivisionsResponse] = {
    import doobie.implicits._
    /**
     * Implicit classes and imports used for JSON "pretty" printing
     */
    implicit val formats = net.liftweb.json.DefaultFormats
    import net.liftweb.json.Serialization.write

    var jsonList = for {
      division <- DivisionQuery.search().to[List].transact(xa).unsafeRunSync
    } yield {
      val jsonStr = write(division)
      jsonStr
    }

    for {
      message <- s"${jsonList.mkString("[", ",", "]")}".pure[F]
    } yield respond.Ok(DivisionsResponse(message))
  }

}