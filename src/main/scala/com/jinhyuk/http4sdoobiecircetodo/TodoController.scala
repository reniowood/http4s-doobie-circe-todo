package com.jinhyuk.http4sdoobiecircetodo

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.syntax._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.MediaType
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import org.http4s.circe._
import org.http4s.Request

class TodoController(todoService: TodoService) {
  val routes = HttpRoutes.of[IO] {
    case GET -> Root / "api" / "todos" => getTodos
    case GET -> Root / "api" / "todos" / LongVar(id) => getTodo(id)
    case req @ POST -> Root / "api" / "todos" => addTodo(req)
    case req @ PUT -> Root / "api" / "todos" / LongVar(id) => updateTodo(id, req)
    case DELETE -> Root / "api" / "todos" / LongVar(id) => deleteTodo(id)
  }.orNotFound

  private def getTodos = 
    for {
      result <- todoService.getTodos()
      response <- Ok()
    } yield response

  private def getTodo(id: Long) = 
    for {
      result <- todoService.getTodo(id)
      response <- result match {
        case Some(todo) => Ok(todo)
        case None => NotFound()
      }
    } yield response

  private def addTodo(req: Request[IO]) =
    for {
      request <- req.as[TodoRequest]
      _ <- todoService.addTodo(request)
      response <- Ok()
    } yield response

  private def updateTodo(id: Long, req: Request[IO]) =
    for {
      request <- req.as[TodoRequest]
      result <- todoService.updateTodo(id, request)
      response <- result match {
        case Right(_) => Ok()
        case Left(TodoNotFound(_)) => NotFound()
        case Left(TodoHasPreTodosNotDoneYet(preTodoIds)) => BadRequest(preTodoIds)
        case _ => InternalServerError()
      }
    } yield response

  private def deleteTodo(id: Long) =
    for {
      result <- todoService.deleteTodo(id)
      response <- Ok()
    } yield response
}
