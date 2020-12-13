package com.none2clever.dapi.endpoints

import cats.Applicative
import cats.effect.IO
import org.http4s.client._
import cats.effect._

import scala.concurrent.ExecutionContext
import java.util.concurrent._

import cats.effect.IO.ioEffect
import com.typesafe.scalalogging.LazyLogging
import io.circe.generic.decoding.DerivedDecoder.deriveDecoder
import io.circe.Encoder
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import cats.implicits.{catsStdInstancesForVector, catsSyntaxParallelTraverse}
import com.none2clever.dapi.AppConfig
import com.none2clever.dapi.endpoints.definitions.DistanceResponse
import com.none2clever.dapi.endpoints.distance.{DistanceHandler, GetDistanceResponse}
import com.none2clever.dapi.models.{Coordinate, DistanceCalculation, GreatCircleRadiusEnum}

import scala.concurrent.ExecutionContext.global
import scala.util.{Failure, Success, Try}

class DistanceHandlerImpl[F[_] : Applicative]() extends DistanceHandler[F] with LazyLogging {

  override def getDistance(respond: GetDistanceResponse.type)(
    cities: Iterable[String],
    units: Option[String]
  ): F[GetDistanceResponse] = {

    implicit val cs: ContextShift[IO] = IO.contextShift(global)
    implicit val timer: Timer[IO] = IO.timer(global)
    val blockingEC = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))
    val httpClient: Client[IO] = JavaNetClientBuilder[IO](blockingEC).create
    def getLocation(name: String): IO[Coordinate] = {
      val result = httpClient.expect[Coordinate](s"${AppConfig.getConfigOrElseDefault("geocodingservice.uri", "http://localhost:8080")}/geocoding?name=$name")
      result
    }
    val locations = Try {
      cities.toVector.parTraverse(getLocation).unsafeRunSync()
    } match {
      case Success(locs) =>
        locs
      case Failure(ex) =>
        logger.error(ex.getLocalizedMessage)
        throw ex
    }
    val calc = DistanceCalculation(
      locations.head,
      locations.tail.head,
      GreatCircleRadiusEnum.withName(units.getOrElse("KM")))
    val distance = calc.getDistance
    import io.circe.syntax._
    case class CityLocation(
                      name: String,
                      location: Coordinate,
                      distance: Double,
    )
    implicit val encodeFieldTypeCoordinate: Encoder[Coordinate] =
      Encoder.forProduct2("latitude", "longitude")(Coordinate.unapply(_).get)
    implicit val encodeFieldTypeCityLocation: Encoder[CityLocation] =
      Encoder.forProduct3("name", "location", "distance")(CityLocation.unapply(_).get)
    val calculations = Vector(CityLocation(cities.head, locations.head, 0.0),
                       CityLocation(cities.tail.head, locations.tail.head, distance))
    val jsonList = Some(calculations.map(x => x.asJson).toIndexedSeq)
    import cats.implicits._
    for {
      distances <- jsonList.pure[F]
    } yield {
      val total = calculations.map(x => x.distance).sum
      val totalBigDecimal: BigDecimal = total
      respond.Ok(DistanceResponse(Some(calculations.size), units, Some(totalBigDecimal), distances))
    }
  }

}