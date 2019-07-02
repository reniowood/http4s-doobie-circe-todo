package com.jinhyuk.http4sdoobiecircetodo

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.syntax._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._

object TodoService {
  val service = HttpRoutes.of[IO] {
    case GET -> Root / "api" / "todos" => Ok()
  }.orNotFound
}
