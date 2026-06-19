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
