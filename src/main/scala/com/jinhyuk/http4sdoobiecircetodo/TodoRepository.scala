package com.jinhyuk.http4sdoobiecircetodo

import doobie._
import doobie.implicits._
import cats.effect.IO

object TodoRepository {
  private val transactor = Database.transactor

  def findAll: IO[Seq[Todo]] =
    transactor.use { xa =>
      sql"select id, name, is_done, is_deleted from todo".query[Todo].to[Seq].transact(xa)
    }

  def findById(id: Long): IO[Option[Todo]] =
    transactor.use { xa =>
      sql"""select id, name, is_done, is_deleted from todo where id = $id""".query[Todo].option.transact(xa)
    }

  def add(todo: TodoRequest): IO[Int] =
    transactor.use { xa =>
      sql"insert into todo (name) values (${todo.name})".update.run.transact(xa)
    }
}
