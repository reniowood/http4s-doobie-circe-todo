package com.jinhyuk.http4sdoobiecircetodo

import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import cats.effect.IO
import scala.concurrent.duration._

class TodoServiceSpec extends FlatSpec with MockFactory {
  private val todoRepository: TodoRepository = new TodoRepository(TestDatabase.transactor)
  private val todoService: TodoService = new TodoService(todoRepository)

  "updateTodo" should "update todo done if there is no pre todo" in {
    // given
    val todo = TodoRequest(name = "test_todo", isDone = false)
    val update = TodoRequest(name = "test_todo", isDone = true)
    val id = todoRepository.add(todo).unsafeRunTimed(1.seconds).get

    // when
    val (result, updated) = (for {
      result <- todoService.updateTodo(id, update)
      updated <- todoRepository.findById(id)
    } yield (result, updated)).unsafeRunTimed(1.seconds).get

    // then
    assert(result.isRight)
    assert(result.right.get == 1)

    assert(updated.isDefined)
    assert(updated.get.isDone)
  }

  it should "update todo done if there are pre todos and they are done" in {
    // given
    val todo = TodoRequest(name = "test_todo", isDone = false)
    val preTodo1 = TodoRequest(name = "pre_todo_1", isDone = true)
    val preTodo2 = TodoRequest(name = "pre_todo_2", isDone = true)
    val update = TodoRequest(name = "test_todo", isDone = true)

    val id = (for {
      id <- todoRepository.add(todo)
      preTodo1Id <- todoRepository.add(preTodo1)
      _ <- todoRepository.addPreTodo(id, preTodo1Id)
      preTodo2Id <- todoRepository.add(preTodo2)
      _ <- todoRepository.addPreTodo(id, preTodo2Id)
    } yield id).unsafeRunTimed(1.seconds).get

    // when
    val (result, updated) = (for {
      result <- todoService.updateTodo(id, update)
      updated <- todoRepository.findById(id)
    } yield (result, updated)).unsafeRunTimed(1.seconds).get

    // then
    assert(result.isRight)
    assert(result.right.get == 1)
    
    assert(updated.isDefined)
    assert(updated.get.isDone)
  }

  it should "not update todo done if there are pre todos not done yet" in {
    // given
    val todo = TodoRequest(name = "test_todo", isDone = false)
    val preTodo1 = TodoRequest(name = "pre_todo_1", isDone = true)
    val preTodo2 = TodoRequest(name = "pre_todo_2", isDone = false)
    val update = TodoRequest(name = "test_todo", isDone = true)

    val (id, preTodo2Id) = (for {
      id <- todoRepository.add(todo)
      preTodo1Id <- todoRepository.add(preTodo1)
      _ <- todoRepository.addPreTodo(id, preTodo1Id)
      preTodo2Id <- todoRepository.add(preTodo2)
      _ <- todoRepository.addPreTodo(id, preTodo2Id)
    } yield (id, preTodo2Id)).unsafeRunTimed(1.seconds).get

    // when
    val (result, updated) = (for {
      result <- todoService.updateTodo(id, update)
      updated <- todoRepository.findById(id)
    } yield (result, updated)).unsafeRunTimed(1.seconds).get

    // then
    assert(result.isLeft)
    assert(result.left.get == TodoHasPreTodosNotDoneYet(Seq(preTodo2Id)))

    assert(updated.isDefined)
    assert(!updated.get.isDone)
  }
}
