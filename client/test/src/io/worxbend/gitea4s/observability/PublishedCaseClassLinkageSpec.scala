package io.worxbend.gitea4s.observability

import io.worxbend.gitea4s.http.{GiteaEndpoint, GiteaEndpoints, UserSearchParams}
import zio.test.*

import java.time.Duration

object PublishedCaseClassLinkageSpec extends ZIOSpecDefault:
  def spec =
    suite("1.0.0 case-class linkage")(
      test("UserSearchParams retains its three-argument JVM constructor, apply and copy") {
        val parameters = Array[Class[?]](classOf[Option[?]], classOf[Option[?]], classOf[Option[?]])
        val constructor = classOf[UserSearchParams].getConstructor(parameters*)
        val apply = UserSearchParams.getClass.getMethod("apply", parameters*)
        val staticApply = classOf[UserSearchParams].getMethod("apply", parameters*)
        val copy = classOf[UserSearchParams].getMethod("copy", parameters*)
        val arguments = Array[AnyRef](Some("alice"), Some(2), Some(10))

        val constructed = constructor.newInstance(arguments*)
        val applied = apply.invoke(UserSearchParams, arguments*).asInstanceOf[UserSearchParams]
        val staticallyApplied = staticApply.invoke(null, arguments*).asInstanceOf[UserSearchParams]
        val copied = copy.invoke(applied.copy(uid = Some(42L)), arguments*).asInstanceOf[UserSearchParams]

        assertTrue(
          constructed == applied,
          staticallyApplied == applied,
          applied.uid.isEmpty,
          copied.uid.contains(42L)
        )
      },
      test("RequestEvent retains its three-argument JVM constructor, apply and copy") {
        val parameters = Array[Class[?]](classOf[GiteaEndpoint], classOf[Duration], classOf[RequestOutcome])
        val constructor = classOf[RequestEvent].getConstructor(parameters*)
        val apply = RequestEvent.getClass.getMethod("apply", parameters*)
        val staticApply = classOf[RequestEvent].getMethod("apply", parameters*)
        val copy = classOf[RequestEvent].getMethod("copy", parameters*)
        val arguments = Array[AnyRef](GiteaEndpoints.userGetCurrent, Duration.ofMillis(5), RequestOutcome.Success)

        val constructed = constructor.newInstance(arguments*)
        val applied = apply.invoke(RequestEvent, arguments*).asInstanceOf[RequestEvent]
        val staticallyApplied = staticApply.invoke(null, arguments*).asInstanceOf[RequestEvent]
        val copied = copy.invoke(applied.copy(status = Some(200), attempts = 2), arguments*).asInstanceOf[RequestEvent]

        assertTrue(
          constructed == applied,
          staticallyApplied == applied,
          applied.status.isEmpty,
          applied.attempts == 1,
          copied.status.contains(200),
          copied.attempts == 2,
          RequestEvent.toString == "RequestEvent"
        )
      }
    )
