package com.jinhyuk.http4sdoobiecircetodo

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.syntax._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.EntityDecoder
import org.http4s.MediaType
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import org.http4s.circe._

object TodoService {
  implicit private val todoEncoder = jsonEncoderOf[IO, Todo]
  implicit private val todoListEncoder = jsonEncoderOf[IO, Seq[Todo]]
  implicit private val todoRequestDecoder = jsonOf[IO, TodoRequest]

  val service = HttpRoutes.of[IO] {
    case GET -> Root / "api" / "todos" => TodoRepository.findAll.flatMap(Ok(_))
    case GET -> Root / "api" / "todos" / IntVar(id) =>
      TodoRepository.findById(id).flatMap {
        case Some(todo) => Ok(todo)
        case None => NotFound()
      }
    case req @ POST -> Root / "api" / "todos" =>
      req.as[TodoRequest].flatMap(TodoRepository.add).flatMap(result => Ok())
  }.orNotFound
}
