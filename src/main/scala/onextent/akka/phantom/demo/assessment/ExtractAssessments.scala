package onextent.akka.phantom.demo.assessment

import akka.NotUsed
import akka.stream.scaladsl.Flow
import onextent.akka.phantom.demo.models.Message
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, _}

object ExtractAssessments {

  implicit val formats: DefaultFormats.type = DefaultFormats

  def apply(): Flow[String, (String, Assessment), NotUsed] =
    Flow[String].map(s => parse(s).extract[Message[Assessment]].body).map(a => (a.name, a))
}

