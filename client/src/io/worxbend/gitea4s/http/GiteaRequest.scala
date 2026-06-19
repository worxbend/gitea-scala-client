package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.error.GiteaError
import sttp.client4.{Request, Response}

sealed trait GiteaRequest[A]:
  type Body

  def endpoint: GiteaEndpoint
  private[gitea4s] def request: Request[Body]
  private[gitea4s] def decode(response: Response[Body]): Either[GiteaError, A]
  def retryable: Boolean

  def copy(retryable: Boolean = this.retryable): GiteaRequest[A] =
    GiteaRequest.withBody(endpoint, request, decode, retryable)

object GiteaRequest:
  private val readOnlyMethods = Set("GET", "HEAD")

  def apply[A](
      endpoint: GiteaEndpoint,
      request: Request[String],
      decode: Response[String] => Either[GiteaError, A],
      retryable: Boolean
  ): GiteaRequest[A] { type Body = String } =
    new StringImpl(endpoint, request, decode, retryable)

  def withBody[A, B](
      endpoint: GiteaEndpoint,
      request: Request[B],
      decode: Response[B] => Either[GiteaError, A],
      retryable: Boolean
  ): GiteaRequest[A] { type Body = B } =
    new Impl(endpoint, request, decode, retryable)

  def isReadOnly(endpoint: GiteaEndpoint): Boolean =
    readOnlyMethods.contains(endpoint.method.toUpperCase)

  private final class Impl[A, B](
      val endpoint: GiteaEndpoint,
      val request: Request[B],
      decodeResponse: Response[B] => Either[GiteaError, A],
      val retryable: Boolean
  ) extends GiteaRequest[A]:
    type Body = B

    def decode(response: Response[B]): Either[GiteaError, A] =
      decodeResponse(response)

  private final class StringImpl[A](
      val endpoint: GiteaEndpoint,
      val request: Request[String],
      decodeResponse: Response[String] => Either[GiteaError, A],
      val retryable: Boolean
  ) extends GiteaRequest[A]:
    type Body = String

    def decode(response: Response[String]): Either[GiteaError, A] =
      decodeResponse(response)
