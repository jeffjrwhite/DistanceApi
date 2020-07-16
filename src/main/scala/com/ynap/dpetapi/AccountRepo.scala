package com.ynap.dpetapi

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor

// account model
case class Account(id: String, name: String, timestamp: Long)

trait AccountRepo {
  def createAccount(account: Account): IO[Int]

  def updateAccount(id: String, account: Account): IO[Int]

  def getAccount(id: String): IO[Option[Account]]

  def getAccounts(): IO[List[Account]]
}

class AccountRepoImpl(xa: Transactor[IO]) extends AccountRepo {
  override def createAccount(account: Account) = {
    AccountQuery.insert(account).run.transact(xa)
  }

  override def updateAccount(id: String, account: Account) = {
    AccountQuery.update(account.id, account.name).run.transact(xa)
  }

  override def getAccount(id: String) = {
    AccountQuery.searchWithId(id).option.transact(xa)
  }

  override def getAccounts() = {
    AccountQuery.searchWithRange(0, 10).to[List].transact(xa)
  }
}