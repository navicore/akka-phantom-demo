package onextent.akka.phantom.demo.models.assessment.db

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.{Date, UUID}

import com.outworkers.phantom.dsl._
import onextent.akka.phantom.demo.models.assessment.Assessment

import scala.concurrent.Future

class AssessmentsModel extends CassandraTable[ConcreteAssessmentsModel, Assessment] {

  override def tableName: String = "assessments"

  object id extends StringColumn(this) with PartitionKey {
    override lazy val name = "assessment_id"
  }

  object name extends StringColumn(this)

  object value extends DoubleColumn(this)

  object datetime extends DateColumn(this)

  override def fromRow(r: Row): Assessment = Assessment(name(r), value(r), Some(UUID.fromString(id(r))), Some( ZonedDateTime.ofInstant(datetime(r).toInstant, ZoneOffset.UTC) ))
}

abstract class ConcreteAssessmentsModel extends AssessmentsModel with RootConnector {

  def getByAssessmentId(id: UUID): Future[Option[Assessment]] = {
    select
      .where(_.id eqs id.toString)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()
  }

  def store(assessment: Assessment): Future[ResultSet] = {
    insert
      .value(_.id, assessment.id.get.toString)
      .value(_.datetime, new Date(assessment.datetime.get.toInstant.toEpochMilli))
      .value(_.name, assessment.name)
      .value(_.value, assessment.value)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .ifNotExists
      .future()
  }

  def deleteById(id: UUID): Future[ResultSet] = {
    delete
      .where(_.id eqs id.toString)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }
}

