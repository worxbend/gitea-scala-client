package io.worxbend.gitea4s.model

/** How a request authenticates against Gitea.
  *
  * The `toString` of every case is redacted. Credentials reach logs by the most
  * ordinary route there is — interpolating a value into a log line, or a
  * failure rendering the config that contains it — and the default `toString`
  * a `case` gets from the compiler would print the secret verbatim. Read the
  * field directly when you genuinely need the value.
  */
enum Auth:
  case Token(value: String)
  case Basic(username: String, password: String)
  case OAuth2(token: String)
  case Anonymous

  // Defined once on the enum rather than per case: an enum case that takes
  // parameters cannot carry its own body in Scala 3.
  override def toString: String = this match
    case Token(_) => "Auth.Token(***)"
    case Basic(username, _) => s"Auth.Basic($username, ***)"
    case OAuth2(_) => "Auth.OAuth2(***)"
    case Anonymous => "Auth.Anonymous"
