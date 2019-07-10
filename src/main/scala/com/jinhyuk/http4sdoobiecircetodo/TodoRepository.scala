package com.jinhyuk.http4sdoobiecircetodo

import doobie._
import doobie.implicits._
import cats.effect.IO
import cats.effect.Resource
import doobie.hikari.HikariTransactor

class TodoRepository(transactor: Resource[IO, HikariTransactor[IO]]) {
  def findAll: IO[Seq[Todo]] =
    transactor.use { xa =>
      sql"select id, name, is_done, is_deleted from todo".query[Todo].to[Seq].transact(xa)
    }

  def findById(id: Long): IO[Option[Todo]] =
    transactor.use { xa =>
      sql"select id, name, is_done, is_deleted from todo where id = $id".query[Todo].option.transact(xa)
    }

  def findPreTodos(id: Long): IO[Seq[Todo]] =
    transactor.use { xa =>
      sql"""
        select todo.id, todo.name, todo.is_done, todo.is_deleted
        from todo
        join pre_todo on pre_todo.pre_todo_id = todo.id
        where pre_todo.todo_id = $id
      """.query[Todo].to[Seq].transact(xa)
    }

  def add(todo: TodoRequest): IO[Long] =
    transactor.use { xa =>
      sql"insert into todo (name, is_done) values (${todo.name}, ${todo.isDone})".update.withUniqueGeneratedKeys[Long]("id").transact(xa)
    }

  def addPreTodo(id: Long, preTodoId: Long): IO[Int] =
    transactor.use { xa =>
      sql"insert into pre_todo (todo_id, pre_todo_id) values ($id, $preTodoId)".update.run.transact(xa)
    }

  def update(id: Long, todo: TodoRequest): IO[Int] =
    transactor.use { xa =>
      sql"update todo set name = ${todo.name}, is_done = ${todo.isDone} where id = $id".update.run.transact(xa)
    }

  def delete(id: Long): IO[Int] =
    transactor.use { xa =>
      sql"update todo set is_deleted = true where id = $id".update.run.transact(xa)
    }
}
