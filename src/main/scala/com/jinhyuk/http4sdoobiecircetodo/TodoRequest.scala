package com.jinhyuk.http4sdoobiecircetodo

import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.generic.extras.Configuration

@ConfiguredJsonCodec case class TodoRequest(name: String, isDone: Boolean = false)

object TodoRequest {
  implicit private val config: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults
}
