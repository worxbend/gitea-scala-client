package io.worxbend.gitea4s.http

final case class UserSearchParams(
    q: Option[String] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None
)

object UserSearchParams:
  val default: UserSearchParams = UserSearchParams()
