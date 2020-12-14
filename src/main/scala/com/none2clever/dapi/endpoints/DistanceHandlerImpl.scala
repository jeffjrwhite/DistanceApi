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
import com.none2clever.dapi.AppConfig
import com.none2clever.dapi.endpoints.definitions.DistanceResponse
import com.none2clever.dapi.endpoints.distance.{DistanceHandler, GetDistanceResponse}
import com.none2clever.dapi.models.{CityLocation, Coordinate}
import com.none2clever.process.{DistanceCalculation, GreatCircleRadiusEnum, Helpers, LocationCache}

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

    def getLocation(name: String): Coordinate = {
      // Check to see if city is in the location cache - if not go to GeoLocation service
      LocationCache.getCachedLocation(name) match {
        case None =>
          logger.warn(s"Nothing in cache for $name, try geocodingservice")
          // Set up repeated try for short period time, repeat for 30 secs with 5 sec intervals
          Helpers.retryForSecondsUntilSuccess[Coordinate](
            Try {
              val res = Try { httpClient.expect[Coordinate](s"${
                AppConfig.getConfigOrElseDefault("geocodingservice.uri",
                  "http://localhost:8080")
              }/geocoding?name=$name").unsafeRunSync()} match {
                case Success(res) =>
                  res
                case Failure(ex) =>
                  logger.warn(ex.getLocalizedMessage)
                  throw ex
              }
              res
            }, 30, 5)
        case Some(coordinate) =>
          coordinate
      }
    }
    // Process location requests for the list of city names
    val locations = Try {
      cities.map(name => getLocation(name))
    } match {
      case Success(locs) =>
        locs
      case Failure(ex) =>
        logger.error(ex.getLocalizedMessage)
        throw ex
    }
    // Zip city name and location coordinates together and cache the locations
    val cityLocations = cities.zip(locations).toSeq
    LocationCache.cacheLocations(cityLocations)
    // Perform distance location on first 2 locations
    // TODO: The API and response message allows for a chain of cities and distances
    // This can be done with a small amount of refactoring here
    val calc = DistanceCalculation(
      locations.head,
      locations.tail.head,
      GreatCircleRadiusEnum.withName(units.getOrElse("KM")))
    val distance = calc.getDistance
    import io.circe.syntax._
    implicit val encodeFieldTypeCoordinate: Encoder[Coordinate] =
      Encoder.forProduct2("latitude", "longitude")(Coordinate.unapply(_).get)
    implicit val encodeFieldTypeCityLocation: Encoder[CityLocation] =
      Encoder.forProduct3("name", "location", "distance")(CityLocation.unapply(_).get)
    val calculations = Vector(CityLocation(cities.head, locations.head, 0.0),
                       CityLocation(cities.tail.head, locations.tail.head, distance))
    // Convert models into JSON and send response
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