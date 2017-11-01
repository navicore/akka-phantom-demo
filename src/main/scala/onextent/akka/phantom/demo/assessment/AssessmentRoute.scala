package onextent.akka.phantom.demo.assessment

import akka.actor.ActorRef
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.pattern.ask
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.phantom.demo.ErrorSupport
import onextent.akka.phantom.demo.assessment.AssessmentService.Get
import spray.json._

import scala.concurrent.Future

object AssessmentRoute
    extends AssessmentJsonSupport
    with LazyLogging
    with Directives
    with ErrorSupport {

  def apply(service: ActorRef): Route =
    path(urlpath / "assessment" / Segment) { name =>
      logRequest(urlpath) {
        handleErrors {
          cors(corsSettings) {
            get {
              val f: Future[Any] = service ask Get(name)
              onSuccess(f) { (r: Any) =>
                {
                  r match {
                    case Some(assessment: Assessment) =>
                      complete(HttpEntity(ContentTypes.`application/json`,
                                          assessment.toJson.prettyPrint))
                    case _ =>
                      complete(StatusCodes.NotFound)
                  }
                }
              }
            }
          }
        }
      }
    }
}
