package onextent.akka.phantom.demo.assessment

import onextent.akka.phantom.demo.models.{JsonSupport, Message}
import spray.json._

trait AssessmentJsonSupport extends JsonSupport {

  implicit val assessmentFormat: RootJsonFormat[Assessment] = jsonFormat2(
    Assessment)
  implicit val assessmentMessageFormat: RootJsonFormat[Message[Assessment]] =
    jsonFormat4(Message[Assessment])
}
