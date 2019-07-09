package com.jinhyuk.http4sdoobiecircetodo

import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.generic.extras.Configuration

@ConfiguredJsonCodec case class Todo(id: Long, name: String, isDone: Boolean, isDeleted: Boolean)

object Todo {
  implicit private val config: Configuration = Configuration.default.withSnakeCaseMemberNames
}
