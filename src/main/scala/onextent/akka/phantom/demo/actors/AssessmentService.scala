package onextent.akka.phantom.demo.actors

import akka.actor._
import akka.util.Timeout
import com.outworkers.phantom.dsl.ResultSet
import com.typesafe.scalalogging.LazyLogging
import onextent.akka.phantom.demo.actors.AssessmentService.Get
import onextent.akka.phantom.demo.models.assessment.Assessment
import onextent.akka.phantom.demo.models.assessment.db.CassandraDatabase

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
    with LazyLogging {

  implicit val executionContext: ExecutionContextExecutor = context.dispatcher

  database.create(5.seconds)

  def store(assessment: Assessment): Future[ResultSet] = {
    for {
      _ <- database.assessmentsModel.store(assessment)
      byName <- database.assessmentsByNamesModel.store(assessment)
    } yield byName
  }

  override def receive: PartialFunction[Any, Unit] = {

    case Get(name) =>
      val sdr = sender()
      database.assessmentsByNamesModel
        .getByName(name)
        .onComplete(r => {
          if (r.get.isEmpty) {
            sdr ! None
          } else {
            sdr ! Some(r.get.head)
          }
        })

    case Assessment(name, value, _, _) =>
      val sdr = sender()
      val newAssessment = Assessment(name, value)
      store(newAssessment).onComplete(_ => {
        sdr ! Some(newAssessment)
      })

    case _ => sender() ! "huh?"
  }

}
