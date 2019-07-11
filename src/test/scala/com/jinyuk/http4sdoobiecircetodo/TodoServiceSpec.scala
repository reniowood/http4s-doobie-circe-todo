package com.jinhyuk.http4sdoobiecircetodo

import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import cats.effect.IO
import scala.concurrent.duration._
import org.scalatest.BeforeAndAfterEach

class TodoServiceSpec extends FlatSpec with MockFactory with BeforeAndAfterEach {
  private val todoRepository: TodoRepository = new TodoRepository(TestDatabase.transactor)
  private val todoService: TodoService = new TodoService(todoRepository)

  override def beforeEach(): Unit = {
    TestDatabase.truncate.unsafeRunSync
  }

  override def afterEach(): Unit = {
    TestDatabase.truncate.unsafeRunSync
  }

  "getTodo" should "get todo already added" in {
    // given
    val todo = TodoRequest(name = "test_todo", isDone = false)
    val id = todoRepository.add(todo).unsafeRunTimed(1.seconds).get

    // when
    val result = (for {
      result <- todoService.getTodo(id)
    } yield result).unsafeRunTimed(1.seconds).get
    
    // then
    assert(result.isDefined)
    assert(result.get.name == todo.name)
    assert(result.get.isDone == todo.isDone)
  }

  it should "return None if there is no todo with given id" in {
    // given
    val id = 3L

    // when
    val result = (for {
      result <- todoService.getTodo(id)
    } yield result).unsafeRunTimed(1.seconds).get

    // then
    assert(result.isEmpty)
  }

  "addTodo" should "add todo" in {
    // given
    val todo = TodoRequest(name = "test_todo", isDone = false)

    // when
    val added = (for {
      id <- todoService.addTodo(todo)
      added <- todoRepository.findById(id)
    } yield added).unsafeRunTimed(1.seconds).get
    
    // then
    assert(added.isDefined)
    assert(added.get.name == todo.name)
    assert(added.get.isDone == todo.isDone)
    assert(!added.get.isDeleted)
  }

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

  "deleteTodo" should "set todo as deleted" in {
    // given
    val todo = TodoRequest(name = "test_todo", isDone = false)
    val id = todoRepository.add(todo).unsafeRunTimed(1.seconds).get
    
    // when
    val (result, updated) = (for {
      result <- todoService.deleteTodo(id)
      updated <- todoRepository.findById(id)
    } yield (result, updated)).unsafeRunTimed(1.seconds).get

    // then
    assert(result == 1)

    assert(updated.isDefined)
    assert(updated.get.isDeleted)
  }

  it should "return 0 if there is no todo with given id" in {
    // given
    val id = 3L

    // when
    val result = (for {
      result <- todoService.deleteTodo(id)
    } yield result).unsafeRunTimed(1.seconds).get

    // then
    assert(result == 0)
  }
}
