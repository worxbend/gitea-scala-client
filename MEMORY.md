[pattern] Bodyless Gitea lifecycle POST commands should use the shared no-body request builder, `decodeUnit`, no `Content-Type`, non-retryable write semantics, and explicit 204 plus documented failure tests.
[learning] Pull-review comment resolve/unresolve endpoints are repository-scoped by owner/repo plus review-comment id; they are not nested under pull index or review id.
[process] Hand-written endpoint metadata now spans enough surface that a spec-to-`GiteaEndpoints` audit is high-value before adding many more slices.
[learning] Commit-status payload states and list-filter states are different contracts: payload/create models include `skipped`, but `repoListStatuses*` query filters omit it in Swagger.
[pattern] Endpoint metadata audits should include query enum values and documented non-2xx response statuses/refs, not just success refs, before write endpoints are considered hardened.
[learning] Pull-request merge endpoints document `405` and `423`; the generic non-5xx fallback maps them to `ServerError`, so resource-state statuses need explicit taxonomy or explicit tests.
