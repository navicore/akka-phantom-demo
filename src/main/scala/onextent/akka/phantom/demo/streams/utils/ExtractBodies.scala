package onextent.akka.phantom.demo.streams.utils

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.microsoft.azure.reactiveeventhubs.EventHubsMessage
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, _}

object ExtractBodies {

  implicit val formats: DefaultFormats.type = DefaultFormats
  def apply(contains: String): Flow[EventHubsMessage, String, NotUsed] =
    Flow[EventHubsMessage]
      .map(e => e.contentAsString)
      .filter(_.contains(contains))
}
