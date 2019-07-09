package com.jinhyuk.http4sdoobiecircetodo

sealed trait TodoError extends Exception
case class TodoNotFound(id: Long) extends TodoError
case class TodoHasPreTodosNotDoneYet(preTodoIds: Seq[Long]) extends TodoError
