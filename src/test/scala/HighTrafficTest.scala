import java.util.concurrent.Executors

import org.scalatest.{FlatSpec, Matchers, _}
import cats.effect.IO
import org.http4s.client._
import cats.effect._

import scala.concurrent.{ExecutionContext, Future}
import cats.effect.IO.ioEffect
import io.circe.generic.decoding.DerivedDecoder.deriveDecoder
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import cats.implicits.{catsStdInstancesForVector, catsSyntaxParallelTraverse}
import com.none2clever.dapi.AppConfig
import com.none2clever.dapi.models.{Coordinate, LocationCache}

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class HighTrafficTest extends FlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

  override def beforeAll(): Unit = {
    println("Setup:beforeAll Create Fixtures")
  }

  override def afterAll(): Unit = {
    println("Setup:afterAll")
  }

  override def beforeEach {
    println("Setup:beforeEach")
  }

  override def afterEach {
    println("Teardown:afterEach")
  }

  "This test" should "run the Retry process handling many requests to the GeoLocator service" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(true)

    def getCityVector(num: Int) = (1 to num).toVector.map(x => s"city$x")

    @tailrec
    def retryForSecondsUntilSuccess[T](fn: => Try[T], seconds: Int = 30, sleep: Int = 5, since: Long = System.currentTimeMillis()): T = {
      val to: Long = since + (seconds * 1000)
      fn match {
        case Success(x) =>
          x
        case _ if System.currentTimeMillis() < to =>
          Thread.sleep(sleep * 1000); retryForSecondsUntilSuccess(fn, seconds, sleep, since)
        case Failure(e) =>
          throw e
      }
    }

    implicit val cs: ContextShift[IO] = IO.contextShift(global)
    implicit val timer: Timer[IO] = IO.timer(global)
    val blockingEC = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(5))
    val httpClient: Client[IO] = JavaNetClientBuilder[IO](blockingEC).create

    def getLocation(name: String): IO[Coordinate] = {
      LocationCache.getCachedLocation(name) match {
        case None =>
          println("Nothing returned - retry....")
          retryForSecondsUntilSuccess[IO[Coordinate]](
            Try(httpClient.expect[Coordinate](s"${
              AppConfig.getConfigOrElseDefault("geocodingservice.uri",
                "http://localhost:8080")
            }/geocoding?name=$name")), 30, 5)
        case Some(coordinate) =>
          println(coordinate)
          IO(coordinate)
      }
    }

    val cities = getCityVector(500)
    val locations = Try {
      cities.parTraverse(getLocation).unsafeRunSync()
    } match {
      case Success(locs) =>
        locs
      case Failure(ex) =>
        println(ex.getLocalizedMessage)
        throw ex
    }
    println(locations)
  }

  "This test" should "run in parallel many 'puts' to the location cache to ensure atomicity of put function" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(true)

    def getCityList(num: Int) = (1 to num).toList.map(x => s"city${"%03d".format(x)}")

    val numberOfCities = 1000
    val cityList = getCityList(numberOfCities)
    val combinedFutures: Future[Unit] =
      for {
        f1 <- Future.sequence(List(
          Future(LocationCache.cacheLocations(cityList.map(x => (x, Coordinate(0.0, 0.1))))),
          Future(LocationCache.cacheLocations(cityList.map(x => (x, Coordinate(0.2, 0.3))))),
          Future(LocationCache.cacheLocations(cityList.map(x => (x, Coordinate(0.4, 0.5))))),
          Future(LocationCache.cacheLocations(cityList.map(x => (x, Coordinate(0.6, 0.7)))))
        ))
      } yield {
        f1
      }
    // Wait for the futures to complete or raise and exception after 60 secs
    val combinedIs = Try(Await.result(combinedFutures, 60 seconds)) match {
      case Success(res) =>
        println(s"Futures processed successfully.")
      case Failure(ex) =>
        throw ex
    }
    //    for ((k,v) <- LocationCache.locationCache.iterator.toList.sortBy(_._1))
    //      printf("key: %s, value: %s\n", k, v)
    //    println(s"Location cache size ${LocationCache.locationCache.size}")

    assert(LocationCache.locationCache.size == numberOfCities, "Too many entries in Location HashMap")
  }

  "This test" should "do too many 'puts' to the location cache" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(true)

    def getCityList(num: Int) = (1 to num).toList.map(x => s"city${"%03d".format(x)}")

    val numberOfCities = 6000
    val cityList = getCityList(numberOfCities)
    LocationCache.cacheLocations(cityList.map(x => (x, Coordinate(0.0, 0.1))))

    assert(LocationCache.isFull == true, "Cache is not full")

  }
}