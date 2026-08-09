package io.worxbend.gitea4s.examples

import io.worxbend.gitea4s.backend.zio.ZioGiteaBackend
import io.worxbend.gitea4s.error.GiteaError
import io.worxbend.gitea4s.model.{ApiReference, Branch, NotificationThread, PullRequest, Release, Repository, Tag, User}
import io.worxbend.gitea4s.{GiteaClient, GiteaConfig, GiteaConfigError}
import zio.{Console, ZIO}

private[examples] object ExampleSupport:
  val referenceLine: String = s"gitea4s targets Gitea API ${ApiReference.gitea1262.version}"

  val credentialsHint: String =
    "Set GITEA_URL with GITEA_TOKEN or GITEA_USERNAME/GITEA_PASSWORD to run the live example."

  /** Runs one example against a live server, or explains why it cannot.
    *
    * Every main here had its own copy of the same twenty lines: match on the
    * config, print the reference line on all three branches, build the backend
    * layer, fold the failure into a message and re-fail, then print the result.
    * That ceremony was most of each file, which buried the two or three lines
    * that actually demonstrate the library — and these examples exist to be
    * read.
    *
    * `use` returns the lines to print, so an example describes *what* it wants
    * shown and never repeats *how* to show it. `hints` are printed instead when
    * no credentials are configured, so each example can say which extra
    * environment variables it needs.
    */
  def runExample(failureLabel: String, hints: String*)(
      use: GiteaClient => ZIO[Any, GiteaError, Seq[String]]
  ): ZIO[Any, Any, Unit] =
    liveConfigFromEnv match
      case Right(None) =>
        printLines(referenceLine +: hints.prepended(credentialsHint))

      case Left(error) =>
        Console.printLine(referenceLine) *>
          Console.printLineError(s"Cannot read live Gitea config: ${error.message}") *>
          ZIO.fail(error)

      case Right(Some(config)) =>
        Console.printLine(referenceLine) *>
          ZIO
            .serviceWithZIO[GiteaClient](use)
            .provideLayer(ZioGiteaBackend.configured(config))
            .foldZIO(
              error =>
                Console.printLineError(s"$failureLabel failed: ${describeFailure(error)}") *> ZIO.fail(error),
              printLines
            )

  /** Reads an environment variable that an example needs, ignoring blanks. */
  def optionalEnv(name: String): Option[String] =
    nonBlank(sys.env.toMap, name)

  private def printLines(lines: Seq[String]): ZIO[Any, Nothing, Unit] =
    ZIO.foreachDiscard(lines)(line => Console.printLine(line).orDie)

  def liveConfigFromEnv: Either[GiteaConfigError, Option[GiteaConfig]] =
    val env = sys.env.toMap
    if nonBlank(env, GiteaConfig.Env.url).isEmpty then Right(None)
    else if hasNoCredentials(env) then Right(None)
    else GiteaConfig.fromEnv(env).map(Some(_))

  def describeFailure(error: GiteaError | Throwable): String =
    error match
      case giteaError: GiteaError => s"${giteaError.message}${detail(giteaError)}"
      case throwable: Throwable => s"backend initialization failed: ${throwable.getMessage}"

  def repositoryName(repository: Repository): String =
    repository.fullName
      .orElse(repository.name)
      .orElse(repository.id.map(id => s"repository#$id"))
      .getOrElse("<unknown repository>")

  def userName(user: User): String =
    user.login
      .orElse(user.loginName)
      .orElse(user.fullName)
      .orElse(user.id.map(id => s"user#$id"))
      .getOrElse("<unknown user>")

  def notificationSummary(thread: NotificationThread): String =
    val id = thread.id.map(_.toString).getOrElse("?")
    val state = if thread.unread.contains(true) then "unread" else "read"
    val repo = thread.repository.map(repositoryName).getOrElse("<unknown repository>")
    val title = thread.subject.flatMap(_.title).getOrElse("<untitled notification>")
    s"#$id [$state] $repo - $title"

  def releaseSummary(release: Release): String =
    val title =
      release.name
        .orElse(release.tagName)
        .orElse(release.id.map(id => s"release#$id"))
        .getOrElse("<untitled release>")
    val tag = release.tagName.filterNot(_ == title).fold("")(value => s" ($value)")
    val state =
      if release.draft.contains(true) then "draft"
      else if release.prerelease.contains(true) then "prerelease"
      else "published"
    val published = release.publishedAt.fold("")(instant => s" at $instant")
    s"$title$tag [$state$published]"

  def pullRequestSummary(pullRequest: PullRequest): String =
    val number =
      pullRequest.number
        .map(value => s"#$value")
        .orElse(pullRequest.id.map(value => s"pull#$value"))
        .getOrElse("#?")
    val title = pullRequest.title.getOrElse("<untitled pull request>")
    val state = pullRequest.state.fold("unknown")(_.jsonValue)
    val draft = if pullRequest.draft.contains(true) then " draft" else ""
    val branches =
      for
        head <- pullRequest.head.flatMap(branch => branch.label.orElse(branch.ref))
        base <- pullRequest.base.flatMap(branch => branch.label.orElse(branch.ref))
      yield s" $head -> $base"
    s"$number [$state$draft]$branches $title"

  def branchSummary(branch: Branch): String =
    val name = branch.name.getOrElse("<unnamed branch>")
    val commit = branch.commit.flatMap(_.id).fold("")(sha => s" @ ${shortSha(sha)}")
    val protectedLabel = if branch.isProtected.contains(true) then " protected" else ""
    s"$name$commit$protectedLabel"

  def tagSummary(tag: Tag): String =
    val name = tag.name.orElse(tag.id).getOrElse("<unnamed tag>")
    val commit = tag.commit.flatMap(_.sha).fold("")(sha => s" @ ${shortSha(sha)}")
    s"$name$commit"

  private def hasNoCredentials(env: Map[String, String]): Boolean =
    nonBlank(env, GiteaConfig.Env.token).isEmpty &&
      nonBlank(env, GiteaConfig.Env.username).isEmpty &&
      nonBlank(env, GiteaConfig.Env.password).isEmpty

  private def nonBlank(env: Map[String, String], name: String): Option[String] =
    env.get(name).map(_.trim).filter(_.nonEmpty)

  /** What a `ServerError` says beyond its status.
    *
    * `GiteaError.message` renders a 5xx as `HTTP 500`, which is all it can say
    * without inventing one — the status is the whole message. The body is the
    * only diagnostic such a response carries, so a short prefix of it is worth
    * showing rather than discarding.
    */
  private def detail(error: GiteaError): String =
    error match
      case GiteaError.ServerError(_, body) =>
        val text = body.trim
        if text.isEmpty then "" else s": ${text.take(200)}"
      case _ => ""

  private def shortSha(value: String): String =
    value.take(12)
