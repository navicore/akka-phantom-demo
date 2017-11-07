package onextent.akka.phantom.demo.models.assessment.db

import java.time.{ZoneOffset, ZonedDateTime}
import java.util.{Date, UUID}

import com.outworkers.phantom.dsl._
import onextent.akka.phantom.demo.models.assessment.Assessment

import scala.concurrent.Future

class AssessmentsByNameModel extends CassandraTable[AssessmentsByNameModel, Assessment] {

  override def tableName: String = "assessments_by_name"

  object name extends StringColumn(this) with PartitionKey

  object value extends DoubleColumn(this)

  object id extends StringColumn(this)

  object datetime extends DateColumn(this) with ClusteringOrder {
    override lazy val name = "assessment_id"
  }

  override def fromRow(r: Row): Assessment = Assessment(name(r), value(r), Some(UUID.fromString(id(r))), Some( ZonedDateTime.ofInstant(datetime(r).toInstant, ZoneOffset.UTC) ))
}

abstract class ConcreteAssessmentsByNameModel extends AssessmentsByNameModel with RootConnector {

  def getByName(name: String): Future[List[Assessment]] = {
    select
      .where(_.name eqs name)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .fetch()
  }

  def store(assessment: Assessment): Future[ResultSet] = {
    insert
      .value(_.name, assessment.name)
      .value(_.value, assessment.value)
      .value(_.id, assessment.id.get.toString)
      .value(_.datetime, new Date(assessment.datetime.get.toInstant.toEpochMilli))
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .ifNotExists
      .future()
  }

  def deleteByNameAndDatetime(name: String, datetime: ZonedDateTime): Future[ResultSet] = {
    delete
      .where(_.name eqs name)
      .and(_.datetime eqs  new Date(datetime.toInstant.toEpochMilli))
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }
}
