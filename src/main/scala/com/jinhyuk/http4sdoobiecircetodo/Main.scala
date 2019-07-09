package com.jinhyuk.http4sdoobiecircetodo

import cats.effect._
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.syntax._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._

object Main extends IOApp {
  private lazy val todoRepository = new TodoRepository(Database.transactor)
  private lazy val todoService = new TodoService(todoRepository)
  private lazy val todoController = new TodoController(todoService)

  private lazy val routes = todoController.routes

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(routes)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
