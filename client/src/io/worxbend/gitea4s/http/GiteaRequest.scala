package io.worxbend.gitea4s.http

import io.worxbend.gitea4s.error.GiteaError
import sttp.client4.{Request, Response}

sealed trait GiteaRequest[A]:
  type Body

  def endpoint: GiteaEndpoint
  def request: Request[String]
  private[gitea4s] def typedRequest: Request[Body]
  def decode(response: Response[String]): Either[GiteaError, A]
  private[gitea4s] def decodeTyped(response: Response[Body]): Either[GiteaError, A]
  def retryable: Boolean

  def copy(retryable: Boolean = this.retryable): GiteaRequest[A] =
    GiteaRequest.withBody(endpoint, typedRequest, decodeTyped, retryable)

object GiteaRequest:
  private val readOnlyMethods = Set("GET", "HEAD")

  def apply[A](
      endpoint: GiteaEndpoint,
      request: Request[String],
      decode: Response[String] => Either[GiteaError, A],
      retryable: Boolean
  ): GiteaRequest[A] =
    new StringImpl(endpoint, request, decode, retryable)

  def withBody[A, B](
      endpoint: GiteaEndpoint,
      request: Request[B],
      decode: Response[B] => Either[GiteaError, A],
      retryable: Boolean
  ): GiteaRequest[A] =
    new Impl(endpoint, request, decode, retryable)

  def isReadOnly(endpoint: GiteaEndpoint): Boolean =
    readOnlyMethods.contains(endpoint.method.toUpperCase)

  private final class Impl[A, B](
      val endpoint: GiteaEndpoint,
      val typedRequest: Request[B],
      decodeResponse: Response[B] => Either[GiteaError, A],
      val retryable: Boolean
  ) extends GiteaRequest[A]:
    type Body = B

    def request: Request[String] =
      typedRequest.asInstanceOf[Request[String]]

    def decode(response: Response[String]): Either[GiteaError, A] =
      decodeTyped(response.asInstanceOf[Response[B]])

    def decodeTyped(response: Response[B]): Either[GiteaError, A] =
      decodeResponse(response)

  private final class StringImpl[A](
      val endpoint: GiteaEndpoint,
      val request: Request[String],
      decodeResponse: Response[String] => Either[GiteaError, A],
      val retryable: Boolean
  ) extends GiteaRequest[A]:
    type Body = String

    def typedRequest: Request[String] =
      request

    def decode(response: Response[String]): Either[GiteaError, A] =
      decodeResponse(response)

    def decodeTyped(response: Response[String]): Either[GiteaError, A] =
      decodeResponse(response)
