package com.jinhyuk.http4sdoobiecircetodo

import cats.effect._
import cats.implicits._
import cats.data.OptionT
import cats.data.EitherT

class TodoService(todoRepository: TodoRepository) {
  def getTodos() = todoRepository.findAll

  def getTodo(id: Long) = todoRepository.findById(id)

  def addTodo(todo: TodoRequest) = todoRepository.add(todo)

  def updateTodo(id: Long, todo: TodoRequest): IO[Either[TodoError, Int]] =
    EitherT.right[TodoError](todoRepository.findPreTodos(id))
      .map(_.filterNot(_.isDone))
      .ensureOr((preTodos: Seq[Todo]) => TodoHasPreTodosNotDoneYet(preTodos.map(_.id)))(_.isEmpty)
      .semiflatMap(_ => todoRepository.update(id, todo))
      .ensure(TodoNotFound(id))(_ == 1)
      .value

  def deleteTodo(id: Long) = todoRepository.delete(id)
}
