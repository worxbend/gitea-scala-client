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
    uid: Option[Long] = None
):
  // Preserve the signatures emitted for the three-field case class in 1.0.0.
  def this(q: Option[String], page: Option[Int], limit: Option[Int]) =
    this(q, page, limit, None)

  def copy(q: Option[String], page: Option[Int], limit: Option[Int]): UserSearchParams =
    new UserSearchParams(q, page, limit, uid)

object UserSearchParams:
  val default: UserSearchParams = UserSearchParams()

  def apply(q: Option[String], page: Option[Int], limit: Option[Int]): UserSearchParams =
    new UserSearchParams(q, page, limit, None)
