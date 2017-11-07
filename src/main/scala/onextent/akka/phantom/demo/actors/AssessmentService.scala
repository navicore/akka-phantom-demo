package onextent.akka.phantom.demo.actors

import akka.actor._
import akka.util.Timeout
import com.outworkers.phantom.dsl.ResultSet
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.phantom.demo.actors.AssessmentService.Get
import onextent.akka.phantom.demo.models.assessment.Assessment
import onextent.akka.phantom.demo.models.db.connector.Connector
import onextent.akka.phantom.demo.models.db.database.CassandraDatabase

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}

object AssessmentService {
  def props(implicit timeout: Timeout) = Props(new AssessmentService)
  def name = "assessmentService"

  final case class Get(name: String)
}
class AssessmentService(implicit timeout: Timeout)
    extends Actor
    with CassandraDatabase
    with Connector.connector.Connector
    with LazyLogging {

  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  database.create(5.seconds)

  override def receive: PartialFunction[Any, Unit] =
    hasState(Map[String, Assessment]())

  def store(assessment: Assessment): Future[ResultSet] = {
    for {
      _ <- database.assessmentsModel.store(assessment)
      byName <- database.assessmentsByNamesModel.store(assessment)
    } yield byName
  }

  def hasState(state: Map[String, Assessment]): PartialFunction[Any, Unit] = {

    case Get(name) =>
      sender() ! state.get(name)

    case Assessment(name, value, _, _) =>
      val newAssessment = Assessment(name, value)

      val sdr = sender()
      //store(newAssessment).onComplete(_ => logger.debug(s"stored")) //todo: how should this be coded in an actor?  re become and reply to sender AFTER db future completes?
      store(newAssessment).onComplete( _ => {
        sdr ! Some(newAssessment)
        context become hasState(state + (newAssessment.name -> newAssessment))
      })


    case _ => sender() ! "huh?"
  }

}
