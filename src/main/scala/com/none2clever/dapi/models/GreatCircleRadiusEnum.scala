package com.none2clever.dapi.models

object GreatCircleRadiusEnum extends Enumeration {
  protected case class Val(radius: Double) extends super.Val
  import scala.language.implicitConversions
  implicit def valueToVal(x: Value): Val = x.asInstanceOf[Val]
  val KM = Val(6378.1370)
  val SMI = Val(3963.191) //3958.761
  val NMI = Val(3443.918) //3440.2753
}
