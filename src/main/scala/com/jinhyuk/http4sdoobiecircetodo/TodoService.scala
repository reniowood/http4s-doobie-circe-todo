package com.jinhyuk.http4sdoobiecircetodo

import cats.effect._
import cats.implicits._

object TodoService {
  def getTodos() = TodoRepository.findAll

  def getTodo(id: Long) = TodoRepository.findById(id)

  def addTodo(todo: TodoRequest) = TodoRepository.add(todo)

  def updateTodo(id: Long, todo: TodoRequest) = TodoRepository.update(id, todo)

  def deleteTodo(id: Long) = TodoRepository.delete(id)
}
