package io.worxbend.gitea4s.model

import zio.test.*

/** Credentials must never survive into a rendered string.
  *
  * These assert on the *absence* of the secret rather than on the exact
  * redacted wording, so the redaction format stays free to change.
  */
object AuthRedactionSpec extends ZIOSpecDefault:
  private val token = "ghp_realSecretValue"
  private val password = "correct-horse-battery-staple"

  def spec: Spec[Any, Any] = suite("Auth redaction")(
    test("a token does not appear in toString") {
      val rendered = Auth.Token(token).toString
      assertTrue(!rendered.contains(token), rendered.contains("Token"))
    },
    test("an OAuth2 token does not appear in toString") {
      val rendered = Auth.OAuth2(token).toString
      assertTrue(!rendered.contains(token), rendered.contains("OAuth2"))
    },
    test("a basic password does not appear in toString, but the username still does") {
      val rendered = Auth.Basic("alice", password).toString
      assertTrue(!rendered.contains(password), rendered.contains("alice"))
    },
    test("anonymous auth renders without a placeholder") {
      assertTrue(Auth.Anonymous.toString == "Auth.Anonymous")
    },
    test("a token does not leak through string interpolation of a containing structure") {
      // The realistic leak is not calling toString directly, it is putting a
      // value that holds an Auth into a log line.
      val rendered = s"connecting with ${Auth.Token(token)}"
      assertTrue(!rendered.contains(token))
    },
    test("a token does not leak through a collection's toString") {
      val rendered = List(Auth.Token(token), Auth.Basic("bob", password)).toString
      assertTrue(!rendered.contains(token), !rendered.contains(password))
    }
  )
