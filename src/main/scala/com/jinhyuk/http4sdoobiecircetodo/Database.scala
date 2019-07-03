package com.jinhyuk.http4sdoobiecircetodo

import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.hikari._

object Database {
  implicit private val cs = IO.contextShift(ExecutionContexts.synchronous)

  val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      te <- ExecutionContexts.cachedThreadPool[IO]
      xa <- HikariTransactor.newHikariTransactor[IO](
          driverClassName = "com.mysql.cj.jdbc.Driver",
          url = "jdbc:mysql://172.18.0.1:3306/todo?nullNamePatternMatchesAll=true&useUnicode=true&characterEncoding=UTF-8",
          user = "root",
          pass = "test",
          connectEC = ce,
          transactEC = te
        )
    } yield xa
}
