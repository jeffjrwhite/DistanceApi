package com.ynap.dpetapi.database

import java.sql.ResultSet

import scala.util.Try

trait RepositoryClient[T] {

  def connect: Try[_]

  def disconnect: Try[_]

  def test: Try[T]

  def executeQuery(query: String): Try[ResultSet]

  def executeUpdate(query: String): Try[Int]

  def run[I, R](query: I, fn: (T) => Try[R]): Try[R]

  def run(query: String): Try[_]

}
