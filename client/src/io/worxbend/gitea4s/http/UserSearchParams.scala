package io.worxbend.gitea4s.http

/** Filters for `GET /users/search`.
  *
  * @param q free-text query matched against username, full name and email
  * @param page 1-based page to start from
  * @param limit items per page; Gitea clamps this to its own maximum
  * @param uid look up the user with this numeric id
  */
final case class UserSearchParams(
    q: Option[String] = None,
    page: Option[Int] = None,
    limit: Option[Int] = None,
    // Added after the fields above so that existing positional constructions
    // keep compiling. The endpoint has always declared this parameter; the
    // params type was the only one in this package that did not match its
    // endpoint, which left no way to look a user up by id.
    uid: Option[Long] = None
)

object UserSearchParams:
  val default: UserSearchParams = UserSearchParams()
