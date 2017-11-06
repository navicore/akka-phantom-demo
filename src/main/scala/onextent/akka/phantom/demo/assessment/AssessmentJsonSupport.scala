package onextent.akka.phantom.demo.assessment

import onextent.akka.phantom.demo.models.JsonSupport
import spray.json._

trait AssessmentJsonSupport extends JsonSupport {

  implicit val assessmentFormat: RootJsonFormat[Assessment] = jsonFormat4(
    Assessment)
}
