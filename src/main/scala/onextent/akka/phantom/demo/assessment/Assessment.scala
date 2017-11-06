package onextent.akka.phantom.demo.assessment

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.UUID

case class Assessment(name: String,
                      value: Double,
                      id: Option[UUID] = Some(UUID.randomUUID()),
                      datetime: Option[ZonedDateTime] = Some(
                        ZonedDateTime.now(ZoneOffset.UTC)))
