package com.ynap.dpetapi.endpoints

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.util.Try

class SysOpsHandler[F[IO]: Sync](signal: SignallingRef[F, Boolean]) extends Http4sDsl[F] {
  val format = "yyyy-MM-dd'T'HH:mm:ss"
  private def routes: HttpRoutes[F] = HttpRoutes.of[F] {
      case GET -> Root / "shutdown" / shutdown =>
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))
        for {
          _ <- signal.set(Try(shutdown.toBoolean).getOrElse(false))
          response = json"""{"shutdown": $time}"""
          result <- Ok(response)
        } yield result
      case GET -> Root / "timestamp" =>
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern(format))
        val response = json"""{"Timestamp": $time}"""
        for {
            result <- Ok(response)
          } yield result
    }
}

object SysOpsHandler {
  def routes[F[IO]: Sync](signal: SignallingRef[F, Boolean]): HttpRoutes[F] =
    new SysOpsHandler(signal).routes
}
