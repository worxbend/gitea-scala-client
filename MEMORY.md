[pattern] Bodyless Gitea lifecycle POST commands should use the shared no-body request builder, `decodeUnit`, no `Content-Type`, non-retryable write semantics, and explicit 204 plus documented failure tests.
[learning] Pull-review comment resolve/unresolve endpoints are repository-scoped by owner/repo plus review-comment id; they are not nested under pull index or review id.
[process] Hand-written endpoint metadata now spans enough surface that a spec-to-`GiteaEndpoints` audit is high-value before adding many more slices.
[learning] Commit-status payload states and list-filter states are different contracts: payload/create models include `skipped`, but `repoListStatuses*` query filters omit it in Swagger.
[pattern] Endpoint metadata audits should include query enum values and documented non-2xx response statuses/refs, not just success refs, before write endpoints are considered hardened.
[pattern] Resource-state HTTP statuses such as `405` and `423` are global mapper behavior once modeled, so cover them with generic mapper tests as well as endpoint-specific contract tests.
[pattern] Keep Swagger audit-only expectations in test scope; adding verification fields to public endpoint metadata creates avoidable ABI churn.
[process] Each endpoint added to an audited group must register expected documented non-2xx labels, and the audit should fail when that registration is missing.
[pattern] Documented conditional-write failures such as HTTP 412 should get explicit `GiteaError` taxonomy instead of falling through to `ServerError`, even when raw bodies are preserved.
[learning] `repoGetSingleCommit` documents three optional boolean query toggles: `stat`, `verification`, and `files`; include all three when modeling that endpoint.
[pattern] Endpoint metadata audits should compare optional query parameter names against Swagger so omissions and accidental public query drift fail early.
[learning] Commit diff/patch downloads use a dot-suffixed `{sha}.{diffType}` path and `text/plain`; model `diffType` as a typed path value, not as a query parameter.
[pattern] Path enum parameters should be compared against Swagger enum values in endpoint audits; local `pathValue` tests alone do not catch API contract drift.
[learning] `repoGetNote` reuses the documented `Commit` response shape but only exposes `verification` and `files` query toggles; do not copy `repoGetSingleCommit`'s `stat` parameter onto note lookups.
[learning] `GetTree` uses body-pagination fields (`page`, `total_count`, `truncated`) and should stay as a response object until live behavior justifies a stream abstraction.
[learning] `GetBlob` returns blob content as an encoded string plus optional LFS metadata; keep core/facade models lossless and defer byte decoding to a deliberate convenience layer.
[learning] `repoListGitRefs` accepts a part or full ref name, and real Git refs contain slashes; request tests should make encoded slash behavior explicit.
[process] For slash-bearing path parameters, pair unit-level encoded-segment assertions with opt-in live validation before generalizing the routing assumption to more endpoints.
[learning] `GetAnnotatedTag` only returns annotated tag objects by tag SHA and must stay distinct from repository tag-list/lightweight tag semantics.
[pattern] Test-side schema-field checklists complement endpoint audits by proving encoded fixture field names, but they should stay visibly anchored to Swagger definitions or be generated from the local spec.
[learning] `repoGetContents` has prose that mentions file-or-directory behavior, but the local Swagger response ref is a single `ContentsResponse`; keep this API anchored to the spec and reserve broader polymorphism for `contents-ext`.
[pattern] Raw/media file endpoints declare `application/octet-stream` `type: file`; decide byte-vs-text response semantics before wiring them through the existing string-oriented request abstraction.
[pattern] Octet-stream download endpoints should use a typed byte response parser through the executor boundary and expose buffered `Chunk[Byte]` unless a streaming slice is designed deliberately.
[pattern] The `GiteaRequest` execution boundary is stabilized: `request: Request[Body]` and `decode(Response[Body])` are `private[gitea4s]`; external callers must use `GiteaRequestExecutor` or the facade, not raw `request.send(...)`.
[learning] Widening `withJsonBody` from `Request[String] => Request[String]` to `Request[B] => Request[B]` compiles and works because sttp's `.body(string)` preserves response type `B`, but the generic form invites accidental misuse on byte-response builders.
[pattern] When removing a public API surface, update the api-snapshot immediately via `./mill compatibility.writeSnapshot` before writing tests, so the snapshot reflects the intended contract rather than the legacy one.
[learning] `repoGetArchive` documents an optional multi-value `path` query parameter for repository subpaths; a whole-archive facade is only a subset unless that query is modeled.
[learning] Archive downloads may need byte decoding in practice even when local Swagger records `produces: application/json` and a bare `200` response; audits and docs must separate Swagger facts from pragmatic client behavior.
[anti-pattern] Failed or abandoned endpoint work can leak into public ABI through source imports and snapshots; quarantine or complete accidental surface before compatibility snapshots are refreshed.
[pattern] Swagger array query parameters with `collectionFormat: multi` must be tested via ordered repeated pairs (`paramsSeq`); map-shaped assertions hide duplicate keys.
[learning] Release attachment GET endpoints return generic Swagger `Attachment` metadata; upload/edit/delete and any binary download behavior are separate contracts, not implied by `browser_download_url`.
[process] Documentation that claims Swagger audit coverage should name the exact audited endpoint group; broad labels like "release endpoints" can hide unaudited legacy metadata.
[learning] `repoListReleases` documents `draft` and `pre-release` filters in addition to pagination; release listing is not facade-complete until those filters are modeled.
[pattern] Live list probes are more useful when an optional configured detail id makes them assert list membership; otherwise empty-list success can be only a weak endpoint check.
[pattern] Stream facades driven by `Pagination.paginated` should treat `page` fields carefully: either keep page low-level-only or implement explicit start-page semantics so caller params are not silently overwritten.
[learning] `repoGetLatestRelease` returns the most recent non-prerelease, non-draft release sorted by `created_at`; live checks need an explicit latest-tag assertion instead of comparing to arbitrary release IDs.
[process] Credential-stripped live integration runs prove hermetic skipping only; record real routing confidence only after an enabled probe observes the configured endpoint behavior.
[process] Normal release-tag probe success is not slash-routing evidence; slash-bearing path behavior needs an enabled probe with a real slash-containing tag.
[learning] `repoCheckCollaborator` is a 204/404 membership probe; a boolean facade is ergonomic but cannot distinguish not-collaborator from every missing-resource 404 case.
[learning] `repoListTeams` omits `page`/`limit` in local Swagger even though the client streams it with pagination query params; live validation should decide whether that pragmatic contract holds.
[learning] Repository tag lookup (`repoGetTag` returning `Tag`) and release-by-tag lookup (`repoGetReleaseByTag` returning `Release`) are separate contracts; keep facade names, live variables, and routing evidence endpoint-specific.
[learning] `repoListTagProtection` is a non-paginated path-only list in local Swagger; do not infer `page`/`limit` from adjacent repository list endpoints.
[learning] `BranchProtection` is a broad metadata response compared with `TagProtection`; gate any read slice with explicit schema-field sizing before adding public ABI.
[learning] `repoListBranchProtection` is a non-paginated path-only list in local Swagger; do not infer `page`/`limit` from branch or protection-adjacent endpoints.
[pattern] Swagger `additionalProperties` response objects need map-shaped codecs that prove no wrapper field leaks onto the wire; extract a helper only after the pattern repeats.
[learning] Repository watchers are exposed by Swagger as `repoListSubscribers` under `/subscribers`; facade names can be ergonomic only when request names, docs, and tests preserve that traceability.
