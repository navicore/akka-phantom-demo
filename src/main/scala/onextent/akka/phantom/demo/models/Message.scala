package onextent.akka.phantom.demo.models

import java.util.{Date, UUID}

final case class Message[T](
    id: UUID = java.util.UUID.randomUUID(),
    kind: String = "unknown",
    datetime: Date = new Date(),
    body: T
)

