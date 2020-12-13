package com.none2clever.dapi.endpoints

import cats.effect.Sync
import fs2.concurrent.SignallingRef
import io.circe.literal._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import scala.util.Try
import cats.implicits._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
