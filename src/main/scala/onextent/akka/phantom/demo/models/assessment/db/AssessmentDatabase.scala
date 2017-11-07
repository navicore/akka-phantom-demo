package onextent.akka.phantom.demo.models.assessment.db

import com.outworkers.phantom.dsl._
import onextent.akka.phantom.demo.models.assessment.db.Connector._

class AssessmentsDatabase(override val connector: KeySpaceDef)
    extends Database[AssessmentsDatabase](connector) {
  object assessmentsModel
      extends ConcreteAssessmentsModel
      with connector.Connector
  object assessmentsByNamesModel
      extends ConcreteAssessmentsByNameModel
      with connector.Connector
}

object Db extends AssessmentsDatabase(connector)

trait DbProvider {
  def database: AssessmentsDatabase
}

trait CassandraDatabase extends DbProvider {
  override val database: Db.type = Db
}
