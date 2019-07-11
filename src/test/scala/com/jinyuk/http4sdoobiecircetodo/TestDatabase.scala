package com.jinhyuk.http4sdoobiecircetodo

import doobie._
import doobie.implicits._
import cats.effect.Resource
import doobie.hikari.HikariTransactor
import cats.effect.IO
import doobie.util.ExecutionContexts

object TestDatabase {
  implicit private val cs = IO.contextShift(ExecutionContexts.synchronous)

  val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32)
      te <- ExecutionContexts.cachedThreadPool[IO]
      xa <- HikariTransactor.newHikariTransactor[IO](
          driverClassName = "org.h2.Driver",
          url = "jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:schema.sql';",
          user = "sa",
          pass = "",
          connectEC = ce,
          transactEC = te
        )
    } yield xa

    def truncate = {
      transactor.use { xa =>
        sql"truncate table todo".update.run.transact(xa)
      }
    }
}
