package com.none2clever.dapi.endpoints

import java.util.concurrent.Executors
import cats.data.NonEmptyList
import cats.effect.{Async, ContextShift}
import org.http4s.{Status => _, _}
import scala.language.higherKinds
import scala.language.implicitConversions
import org.http4s.dsl.Http4sDsl

class StaticContentHandler[F[_]]()(implicit F: Async[F]) extends Http4sDsl[F] {

  import org.http4s.CacheDirective.`no-cache`
  import org.http4s._
  import org.http4s.headers.`Cache-Control`
  import scala.concurrent.ExecutionContext

  val blockingEc = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
  val supportedStaticExtensions =
    List(".html", ".js", ".map", ".css", ".png", ".ico", ".jpg", ".jpeg", ".otf", ".ttf")

  def routes(cShift: ContextShift[F]): HttpRoutes[F] = HttpRoutes.of {
    {
      case req @ GET -> Root =>
        implicit val cs: ContextShift[F] = cShift
        val target = s"staticpages/index.html"
        StaticFile.fromString(target, blockingEc, Some(req))
          .map(_.putHeaders())
          .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
          .getOrElseF(NotFound())
      case req @ GET -> Root / _ if req.pathInfo.endsWith(".html") =>
        implicit val cs: ContextShift[F] = cShift
        val target = s"staticpages${req.pathInfo}"
        StaticFile.fromString(target, blockingEc, Some(req))
          .map(_.putHeaders())
          .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
          .getOrElseF(NotFound())
      // Loads Swagger UI
      case req @ GET -> Root / "swagger-ui" =>
         implicit val cs: ContextShift[F] = cShift
        val target = s"staticpages/swagger-ui/index.html"
          StaticFile.fromString(target, blockingEc, Some(req))
            .map(_.putHeaders())
            .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
            .getOrElseF(NotFound())
      // Loads Swagger YAML
      case req @ GET -> _ / "api.yaml" =>
        implicit val cs: ContextShift[F] = cShift
        val target = s"src/main/resources/api.yaml"
        StaticFile.fromString(target, blockingEc, Some(req))
          .map(_.putHeaders())
          .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
          .getOrElseF(NotFound())
      // Loads SwaggerUI Static Resources as Called
      case req @ GET -> _ if req.pathInfo.startsWith("/scripts") && supportedStaticExtensions.exists(req.pathInfo.endsWith) =>
        implicit val cs: ContextShift[F] = cShift
        val target = s"staticpages${req.pathInfo}"
        StaticFile
          .fromString(target, blockingEc, Some(req))
          .map(_.putHeaders())
          .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
          .getOrElseF(NotFound())
      // Loads Any Static Resources as Called
      case req if supportedStaticExtensions.exists(req.pathInfo.endsWith) =>
        implicit val cs: ContextShift[F] = cShift
        val target = s"staticpages/swagger-ui${req.pathInfo}"
        StaticFile
          .fromString(target, blockingEc, Some(req))
          .map(_.putHeaders())
          .map(_.putHeaders(`Cache-Control`(NonEmptyList.of(`no-cache`()))))
          .getOrElseF(NotFound())

    }
  }
}
