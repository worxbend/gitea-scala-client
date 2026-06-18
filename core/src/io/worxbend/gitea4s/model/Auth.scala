package io.worxbend.gitea4s.model

enum Auth:
  case Token(value: String)
  case Basic(username: String, password: String)
  case OAuth2(token: String)
  case Anonymous
