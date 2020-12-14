package com.none2clever.process

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object Helpers {

  @tailrec
  final def retryForSecondsUntilSuccess[T](fn: => Try[T], seconds: Int = 30, sleep: Int = 5, since: Long = System.currentTimeMillis()): T = {
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

}
