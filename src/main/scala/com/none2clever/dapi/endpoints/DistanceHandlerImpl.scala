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

import scala.concurrent.ExecutionContext.global
import scala.util.{Failure, Success, Try}

class DistanceHandlerImpl[F[_] : Applicative]() extends DistanceHandler[F] with LazyLogging {

  override def getDistance(respond: GetDistanceResponse.type)(
    cities: Iterable[String],
    units: Option[String]
  ): F[GetDistanceResponse] = {

    import io.circe.syntax._
    case class Coordinate(latitude: Double, longitude: Double)
    implicit val encodeFieldType: Encoder[Coordinate] =
      Encoder.forProduct2("latitude", "longitude")(Coordinate.unapply(_).get)

    implicit val cs: ContextShift[IO] = IO.contextShift(global)
    implicit val timer: Timer[IO] = IO.timer(global)
    val blockingEC = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))
    val httpClient: Client[IO] = JavaNetClientBuilder[IO](blockingEC).create
    def getLocation(name: String) = { //: IO[Coordinate] = {
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
    val calculations = locations
    val jsonList = Some(calculations.map(x => x.asJson).toIndexedSeq)
    import cats.implicits._
    for {
      distances <- jsonList.pure[F]
    } yield
      respond.Ok(DistanceResponse(Some(distances.size), units, distances))
  }

}