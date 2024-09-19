package dinosaur.game.model

import dinosaur.game.util.Database
import scalafx.beans.property.StringProperty
import scalikejdbc._


class Person(val usernameS: String, val scoreS: String) extends Database{
  def this() = this(null, null)
  private val username = new StringProperty(Option(usernameS).getOrElse("Unknown"))
  private val score = new StringProperty(Option(scoreS).getOrElse("0"))

  def save(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
           INSERT INTO Person (username, score)
           VALUES (${username.value}, ${score.value})
           """.update.apply()
    }
  }
}

object Person extends Database{
  def apply (
              usernameS : String,
              scoreS : String
            ) : Person = {
    new Person(usernameS, scoreS) {
    }
  }

  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
      CREATE TABLE Person (
        id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
        username VARCHAR(255),
        score INTEGER,
        PRIMARY KEY (id)
      )
    """.execute.apply()
    }
  }

  def getTopScores(limit: Int = 5): List[Person] = {
    DB readOnly { implicit session =>
      sql"""
      SELECT username, score
      FROM Person
      ORDER BY score DESC
      FETCH FIRST $limit ROWS ONLY
    """.map(rs => new Person(rs.string("username"), rs.string("score")))
        .list.apply()
    }
  }

}