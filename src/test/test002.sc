
def haversineDistance(pointA: (Double, Double), pointB: (Double, Double)): Double = {
  val deltaLat = math.toRadians(pointB._1 - pointA._1)
  val deltaLong = math.toRadians(pointB._2 - pointA._2)
  val a = math.pow(math.sin(deltaLat / 2), 2) + math.cos(math.toRadians(pointA._1)) * math.cos(math.toRadians(pointB._1)) * math.pow(math.sin(deltaLong / 2), 2)
  val greatCircleDistance = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
  3958.761 * greatCircleDistance // 3,949.9028
}

//The Earth's Mean Spherical Radius (NM) is 3440.2753 nmi
def haversineDistanceNM(pointA: (Double, Double), pointB: (Double, Double)): Double = {
  val deltaLat = math.toRadians(pointB._1 - pointA._1)
  val deltaLong = math.toRadians(pointB._2 - pointA._2)
  val a = math.pow(math.sin(deltaLat / 2), 2) + math.cos(math.toRadians(pointA._1)) * math.cos(math.toRadians(pointB._1)) * math.pow(math.sin(deltaLong / 2), 2)
  val greatCircleDistance = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
  3440.2753 * greatCircleDistance
}
// Summertown {"latitude":-2.01,"longitude":97.4}
// Qutyini {"latitude":66.91,"longitude":38.09}
val distance = haversineDistance(
  (-2.01,97.4), (66.91,38.09)
)
// Check length of one minute of arc in NM along longitude axis (should be 1)
val distanceOneNM = haversineDistanceNM(
  (0.0,97.4), (0.016666666666667,97.4)
)