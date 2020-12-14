package com.none2clever.process

import com.none2clever.dapi.AppConfig
import com.none2clever.dapi.models.Coordinate
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

object LocationCache extends LazyLogging {

  var cacheFull = false
  def isFull: Boolean = cacheFull
  val maxCacheSize: Int = AppConfig.getConfigOrElseDefault("geocodingservice.cachemaxsize", "1000").toInt
  val locationCache: mutable.HashMap[String, Coordinate] = new mutable.HashMap()

  def cacheLocations(cityLocations: Seq[(String, Coordinate)]):Unit = {
    for (location <- cityLocations) {
      location._1 match {
        case name if locationCache.contains(name) == false =>
          synchronized {
            if (locationCache.size >= maxCacheSize) {
              if (cacheFull == false)
                logger.warn(s"Location cache has reached maximum size [$maxCacheSize]")
              cacheFull = true
            } else {
              locationCache.put(name, location._2)
              logger.info(s"City $name added to location cache ${location._2}")
            }
          }
        case name =>
          logger.info(s"City $name already in location cache ${location._2}")
      }
    }
  }

  def getCachedLocation(city: String): Option[Coordinate] = {
    locationCache.get(city)
  }
}
