package onextent.akka.phantom.demo.actors

import akka.actor._
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.phantom.demo.actors.AssessmentService.Get
import onextent.akka.phantom.demo.models.assessment.Assessment

object AssessmentService {
  def props(implicit timeout: Timeout) = Props(new AssessmentService)
  def name = "assessmentService"

  final case class Get(name: String)
}

class AssessmentService(implicit timeout: Timeout)
    extends Actor
    with LazyLogging {

  override def receive: PartialFunction[Any, Unit] = hasState(Map[String, Assessment]())

  def hasState(state: Map[String, Assessment]): PartialFunction[Any, Unit] = {
    case Get(name) =>
      sender() ! state.get(name)
    case Assessment(name, value, _, _) =>
      val newAssessment = Assessment(name, value)
      sender() ! Some(newAssessment)
      context become hasState(state + (newAssessment.name -> newAssessment))
    case _ => sender() ! "huh?"
  }

}
