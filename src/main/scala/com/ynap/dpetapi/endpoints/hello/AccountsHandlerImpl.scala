package com.ynap.dpetapi.endpoints.hello

import cats.Applicative
import cats.implicits._
import com.ynap.dpetapi.endpoints.accounts.{GetAccountsResponse, AccountsHandler}
import com.ynap.dpetapi.endpoints.definitions.AccountsResponse

class AccountsHandlerImpl[F[_] : Applicative]() extends AccountsHandler[F] {
  override def getAccounts(respond: GetAccountsResponse.type)(): F[GetAccountsResponse] = {
    for {
      message <- s"Hello...".pure[F]
    } yield respond.Ok(AccountsResponse(message))
  }
}