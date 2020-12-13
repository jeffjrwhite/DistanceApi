package com.none2clever.dapi.models

case class DistanceCalculation(from: Coordinate, to: Coordinate, units: GreatCircleRadiusEnum.Value) {

  def haversineDistanceNM(pointA: (Double, Double), pointB: (Double, Double)): Double = {
    val deltaLat = math.toRadians(pointB._1 - pointA._1)
    val deltaLong = math.toRadians(pointB._2 - pointA._2)
    val a = math.pow(math.sin(deltaLat / 2), 2) + math.cos(math.toRadians(pointA._1)) * math.cos(math.toRadians(pointB._1)) * math.pow(math.sin(deltaLong / 2), 2)
    val greatCircleDistance = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    units.radius * greatCircleDistance
  }

  def getDistance: Double = haversineDistanceNM((from.latitude, from.longitude), (to.latitude, to.longitude))
  def getUnits: String = units.toString
}
