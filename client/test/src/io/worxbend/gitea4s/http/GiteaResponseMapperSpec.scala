package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.error.GiteaError
import sttp.client4.Response
import sttp.client4.testing.ResponseStub
import sttp.model.StatusCode
import zio.test.*

object GiteaResponseMapperSpec extends ZIOSpecDefault:
  def spec =
    suite("Gitea response mapper")(
      suite("global resource-state failures")(
        test("maps 405 JSON error bodies to MethodNotAllowed with the decoded Gitea message") {
          val body = """{"message":"merge method is not allowed"}"""
          val response = raw(body, StatusCode.MethodNotAllowed)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.MethodNotAllowed("merge method is not allowed", body)
          )
        },
        test("maps 405 empty bodies to MethodNotAllowed using the HTTP status text") {
          val response = raw("", StatusCode.MethodNotAllowed)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.MethodNotAllowed("Method Not Allowed", "")
          )
        },
        test("maps 405 non-JSON bodies to MethodNotAllowed while preserving the raw body") {
          val body = "method is disabled for this repository"
          val response = raw(body, StatusCode.MethodNotAllowed)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.MethodNotAllowed("Method Not Allowed", body)
          )
        },
        test("maps 412 JSON error bodies to PreconditionFailed with the decoded Gitea message") {
          val body = """{"message":"stale content version"}"""
          val response = raw(body, StatusCode(412))

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.PreconditionFailed("stale content version", body)
          )
        },
        test("maps 412 empty bodies to PreconditionFailed using the HTTP status text") {
          val response = raw("", StatusCode(412))

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.PreconditionFailed("Precondition Failed", "")
          )
        },
        test("maps 412 non-JSON bodies to PreconditionFailed while preserving the raw body") {
          val body = "stale content version"
          val response = raw(body, StatusCode(412))

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.PreconditionFailed("Precondition Failed", body)
          )
        },
        test("maps 423 JSON error bodies to Locked with the decoded Gitea message") {
          val body = """{"message":"repository is archived"}"""
          val response = raw(body, StatusCode.Locked)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.Locked("repository is archived", body)
          )
        },
        test("maps 423 empty bodies to Locked using the HTTP status text") {
          val response = raw("", StatusCode.Locked)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.Locked("Locked", "")
          )
        },
        test("maps 423 non-JSON bodies to Locked while preserving the raw body") {
          val body = "resource is locked by another operation"
          val response = raw(body, StatusCode.Locked)

          assertTrue(
            GiteaResponseMapper.toError(response) ==
              GiteaError.Locked("Locked", body)
          )
        }
      )
    )

  private def raw(body: String, statusCode: StatusCode): Response[String] =
    ResponseStub(body, statusCode)
