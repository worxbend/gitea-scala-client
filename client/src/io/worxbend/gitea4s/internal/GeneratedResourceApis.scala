package io.worxbend.gitea4s.internal

import io.worxbend.gitea4s.GiteaConfig

private[gitea4s] final class SttpAdminApi(
    protected val config: GiteaConfig,
    protected val executor: GiteaRequestExecutor
) extends generated.LiveAdminOperations

private[gitea4s] final class SttpTeamsApi(
    protected val config: GiteaConfig,
    protected val executor: GiteaRequestExecutor
) extends generated.LiveTeamsOperations

private[gitea4s] final class SttpPackagesApi(
    protected val config: GiteaConfig,
    protected val executor: GiteaRequestExecutor
) extends generated.LivePackagesOperations

private[gitea4s] final class SttpCatalogApi(
    protected val config: GiteaConfig,
    protected val executor: GiteaRequestExecutor
) extends generated.LiveCatalogOperations
