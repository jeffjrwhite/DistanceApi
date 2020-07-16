package com.ynap.dpetapi

import doobie.implicits._

object AccountQuery {

  def createDb = {
    sql"""
         |CREATE DATABASE IF NOT EXISTS mystiko
       """
      .update
  }

  def createTable = {
    sql"""
         |CREATE TABLE IF NOT EXISTS accounts (
         |  id VARCHAR(100) PRIMARY KEY,
         |  name VARCHAR(100),
         |  timestamp Long
         |)
       """.stripMargin
      .update
  }

  // insert query
  def insert(account: Account): doobie.Update0 = {
    sql"""
         |INSERT INTO accounts (
         |  id,
         |  name,
         |  timestamp
         |)
         |VALUES (
         |  ${account.id},
         |  ${account.name},
         |  ${account.timestamp}
         |)
        """.stripMargin
      .update
  }

  // update query
  def update(id: String, name: String): doobie.Update0 = {
    sql"""
         |UPDATE accounts
         |SET name = $name
         |WHERE id = $id
       """.stripMargin
      .update
  }

  // search account
  def search(name: String): doobie.Query0[Account] = {
    sql"""
         |SELECT * FROM accounts
         |WHERE name = $name
       """.stripMargin
      .query[Account]
  }

  // search with id
  def searchWithId(id: String): doobie.Query0[Account] = {
    sql"""
         |SELECT * FROM accounts
         |WHERE id = $id
         |LIMIT 1
       """.stripMargin
      .query[Account]
  }

  // search range
  def searchWithRange(offset: Int, limit: Int): doobie.Query0[Account] = {
    sql"""
         |SELECT * FROM accounts
         |LIMIT $limit
         |OFFSET $offset
       """.stripMargin
      .query[Account]
  }

  // search fragments
  def searchWithFragment(name: String, asc: Boolean): doobie.Query0[Account] = {
    val f1 = fr"SELECT id, name, timestamp FROM accounts"
    val f2 = fr"WHERE name = $name"
    val f3 = fr"ORDER BY timestamp" ++ (if (asc) fr"ASC" else fr"DESC")
    val q = (f1 ++ f2 ++ f3).query[Account]
    q
  }

  // delete query
  def delete(id: String): doobie.Update0 = {
    sql"""
         |DELETE FROM accounts
         |WHERE id=$id
       """.stripMargin
      .update
  }

}