package onextent.akka.phantom.demo.assessment

import akka.actor._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.phantom.demo.assessment.AssessmentService.Get
import onextent.akka.phantom.demo.streams.db.Holder

object AssessmentService {
  def props(implicit timeout: Timeout) = Props(new AssessmentService)
  def name = "assessmentService"

  final case class Get(name: String)
}

class AssessmentService(implicit timeout: Timeout)
    extends Actor
    with LazyLogging {

  override def receive: PartialFunction[Any, Unit] = {
    case Get(name) =>
      def notFound(): Unit = sender() ! None
      context.child(name).fold(notFound())(_ forward Holder.Get())
  }

}
