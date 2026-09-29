# Full Gitea v1.27.3 Coverage

The completion target is **every operation and every request/response shape in `gitea-v1.27.3.yaml`**, not merely the operations introduced since v1.26.2. This is a tracking plan, not a claim of completion. Do not call the client fully v1.27.3-compliant while any category below remains open.

## Baseline

The tagged v1.27.3 Swagger template declares 482 operation IDs and 222 definitions. At the start of this plan, `GiteaEndpoints.all` contains 130 operations; **352 are missing**. Eleven of those 352 operations are new since v1.26.2. The other 341 were already missing from the older client. Nine schema definitions are new: `CreatePullReviewCommentReplyOptions`, `CurrentAccessToken`, `IssueAssigneesOption`, `Project`, `PullRequestMinimal`, `PullRequestMinimalHead`, `PullRequestMinimalHeadRepo`, `TopicListResponse`, and `UserMeta`. Existing definitions may also have added or changed properties.

Missing operations by first path component at baseline: `/repos` 157, `/user` 60, `/orgs` 47, `/admin` 32, `/teams` 12, `/users` 12, `/packages` 9, `/settings` 4, and 19 across the remaining roots. The counts come from comparing operation IDs in the vendored spec to the explicit `operationId` entries in `client/src/io/worxbend/gitea4s/http/GiteaEndpoint.scala`. Recompute them after each batch.

**First batch:** the catalog now has 141 endpoints, including all eleven introduced by v1.27.3. **341 older operations remain missing.** Some endpoint constants reuse metadata with `copy`, so count `GiteaEndpoints.all` rather than merely counting literal `operationId =` expressions. `GiteaClientV1273.fromBackend` exposes the additive methods without adding abstract members to the published `GiteaClient` trait.

**Repository lifecycle batch:** the catalog now has **152 of 482 operations, with 330 missing**. Eleven additional operations cover current-user and organization repository creation (including the legacy organization path), repository edit/delete, fork, branch create/delete, and ownership transfer request/accept/reject. The accompanying write models account for every field in their v1.27.3 schemas. No live or destructive request was sent during testing.

## Implementation Order

- [x] Complete the eleven newly introduced operations: token metadata/revocation; issue and repository assignees; organization repository deletion; pull-review comment replies; and workflow runs/attempt jobs. Each has a typed facade method, request builder, contract audit, and hermetic wire tests. The workflow run and job list methods expose one requested page plus its `total_count`; callers supply `page` and `limit` explicitly.
- [ ] Reconcile **all existing 130 operations** with the new contract, including query/body/response semantics and every added response property. Preserve 1.0.0 JVM members; add overloads, separate read models, or explicitly version an incompatible change rather than silently changing published case-class arity.
- [ ] Add the missing `/repos` operations by domain: repository lifecycle and settings, branches/Git, issues and comments, pull requests and reviews, actions/workflows, packages, and remaining reads/writes.
- [x] Repository lifecycle slice: create/edit/delete/fork; branch create/delete; ownership transfer request/accept/reject; modern and deprecated organization creation paths. Repository settings, migration, mirrors, and the rest of `/repos` remain open.
- [ ] Add the missing `/user`, `/users`, `/orgs`, `/teams`, `/admin`, `/packages`, and remaining root operations. Put each in an appropriate typed namespace, creating additive namespaces where needed.
- [ ] Audit all 222 definitions, reusable responses, enum values, pagination formats, request bodies (including multipart and binary), HTTP statuses, and authentication requirements. Model optional/unknown server fields without discarding required information or exposing credentials in logs.
- [ ] Split the monolithic `GiteaRequests.scala` and endpoint catalog into resource-focused files while preserving existing public JVM forwarders.
- [ ] Update examples, README/site documentation, and coverage notes for each completed group; clarify high-risk operations such as administration, token revocation, and deletion.

## Completion Gate For Each Operation

1. A typed public facade method, explicit endpoint metadata, and a request builder all correspond to the same operation ID in the vendored spec. New APIs must not remove v1.0.0 members or add abstract obligations to published traits without a compatible default.
2. Focused hermetic tests exercise method, escaped path, query, authorization, exact JSON/multipart/binary body, success decoding (including pagination), and documented failure statuses. The endpoint audit verifies the operation against the vendored spec. Opt-in live tests run only with credentials.
3. Response models preserve every modeled v1.27.3 field, including nested and optional fields. Changes to previously published models pass the release-tag compatibility guard.
4. `./mill --no-server __.compile __.test compatibility.check` and `./mill --no-server compatibility.testReleaseGuard` pass. Refresh working snapshots only for additive API changes.

## Final Acceptance

The set of operation IDs in `GiteaEndpoints.all` equals the set of all 482 v1.27.3 Swagger operation IDs, with no duplicates or omissions. Every operation meets the gate above, model/schema coverage has been reviewed, documentation describes the actual supported surface, and the full hermetic suite passes. **An audit-only descriptor or a generic untyped HTTP escape hatch does not count as an implemented operation.**
