import com.none2clever.dapi.models.{Coordinate, DistanceCalculation, GreatCircleRadiusEnum}
import org.scalatest.{FlatSpec, Matchers, _}

class DistanceCalcTest extends FlatSpec with Matchers with BeforeAndAfterAll with BeforeAndAfterEach {

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

  "A DistanceCalculation object will be created that" should "calculate the distance of 1 minute or arc" taggedAs(ApiTestTag, LinuxTestTag) in {

    assume(true)

    assert(
      DistanceCalculation(
        Coordinate(0.0,97.4),
        Coordinate(0.016666666666667,97.4),
        GreatCircleRadiusEnum.NMI).getUnits == "NMI",
      "Distance calcuation units not correct")
    assert(
      DistanceCalculation(
        Coordinate(0.0,97.4),
        Coordinate(0.016666666666667,97.4),
        GreatCircleRadiusEnum.NMI).getDistance.toInt == 1,
      "Distance calcuation for 1 minute or arc is not 1 Nautical Mile")
  }

}