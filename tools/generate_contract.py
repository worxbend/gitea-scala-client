#!/usr/bin/python3
"""Generate wire models from the vendored Gitea v1.27.3 Swagger contract.

The generated package is separate from hand-written models so the generator can
be rerun without overwriting application code. All fields, including optional
ones, retain their exact JSON names. Use /usr/bin/python3 (PyYAML is installed
there in the project environment).
"""

from __future__ import annotations

import re
import json
from pathlib import Path

import yaml


ROOT = Path(__file__).resolve().parent.parent
SPEC = yaml.safe_load((ROOT / "gitea-v1.27.3.yaml").read_text())
MODELS = ROOT / "core/src/io/worxbend/gitea4s/model/contract"
CLIENT = ROOT / "client/src/io/worxbend/gitea4s"
EXCEPTIONAL_OPERATIONS = {
    "downloadArtifact",
    "issueCreateIssueCommentAttachment",
    "issueCreateIssueAttachment",
    "repoCreateReleaseAttachment",
}
QUERY_ENUMS = {
    ("adminListHooks", "type"): "AdminHookType",
    ("listPackages", "type"): "PackageType",
    ("issueSearchIssues", "state"): "IssueSearchState",
    ("issueSearchIssues", "type"): "IssueSearchType",
    ("notifyGetRepoList", "subject-type"): "NotificationSubjectType",
    ("repoCompareDiff", "output"): "CompareOutput",
}
SCALA_KEYWORDS = {
    "abstract", "case", "catch", "class", "def", "do", "else", "enum",
    "export", "extends", "false", "final", "finally", "for", "forSome",
    "given", "if", "implicit", "import", "lazy", "match", "new", "null",
    "object", "override", "package", "private", "protected", "return",
    "sealed", "super", "then", "this", "throw", "trait", "true", "try",
    "type", "val", "var", "while", "with", "yield", "end", "inline",
    "opaque", "open", "transparent", "using", "extension", "infix",
}
SENSITIVE_FIELD_NAMES = {"remote_password", "password", "mirror_password", "mirror_token", "auth_password", "auth_token", "aws_access_key_id", "aws_secret_access_key", "client_secret"}


def identifier(value: str) -> str:
    words = re.split(r"[^A-Za-z0-9]+", value)
    result = words[0] + "".join(word[:1].upper() + word[1:] for word in words[1:])
    return f"`{result}`" if result in SCALA_KEYWORDS else result


def scala_type(schema: dict) -> str:
    if "$ref" in schema:
        return schema["$ref"].split("/")[-1]
    kind = schema.get("type")
    if kind == "string":
        return "java.time.Instant" if schema.get("format") == "date-time" else "String"
    if kind == "integer":
        return "Long" if schema.get("format") in ("int64", "uint64") else "Int"
    if kind == "number":
        return "Double"
    if kind == "boolean":
        return "Boolean"
    if kind == "array":
        return f"List[{scala_type(schema.get('items', {}))}]"
    if kind == "object" and schema.get("additionalProperties"):
        return f"Map[String, {scala_type(schema['additionalProperties'])}]"
    if kind == "object" and "additionalProperties" in schema:
        return "Map[String, zio.json.ast.Json]"
    return "zio.json.ast.Json"


def model(name: str, schema: dict) -> str:
    if schema.get("type") != "object" or not schema.get("properties"):
        return alias_model(name, schema)

    required = set(schema.get("required", ()))
    members = []
    for field, definition in schema["properties"].items():
        tpe = scala_type(definition)
        optional = field not in required
        annotation = f'@jsonField("{field}") '
        declared = f"{identifier(field)}: {'Option[' + tpe + ']' if optional else tpe}"
        members.append(f"    {annotation}{declared}{' = None' if optional else ''}")
    params = ",\n".join(members)
    redaction = (
        f'  override def toString: String = "{name}(<redacted>)"\n'
        if set(schema["properties"]) & SENSITIVE_FIELD_NAMES else ""
    )
    return (
        f"final case class {name}(\n{params}\n){':' if redaction else ''}\n{redaction}\n"
        f"object {name}:\n"
        f"  given JsonCodec[{name}] = DeriveJsonCodec.gen[{name}]\n"
    )


def alias_model(name: str, schema: dict) -> str:
    value_type = scala_type(schema)
    if schema.get("type") == "object" and schema.get("additionalProperties"):
        value_type = scala_type(schema)
    return (
        f"final case class {name}(value: {value_type})\n\n"
        f"object {name}:\n"
        f"  given JsonCodec[{name}] = JsonCodec(\n"
        f"    summon[JsonEncoder[{value_type}]].contramap(_.value),\n"
        f"    summon[JsonDecoder[{value_type}]].map({name}.apply)\n"
        f"  )\n"
    )


def generate_models() -> None:
    MODELS.mkdir(parents=True, exist_ok=True)
    for name, schema in SPEC["definitions"].items():
        text = (
            "package io.worxbend.gitea4s.model.contract\n\n"
            "import zio.json.*\n\n"
            f"{model(name, schema)}"
        )
        (MODELS / f"{name}.scala").write_text(text)


def generate_query_enums() -> None:
    lines = ["package io.worxbend.gitea4s.model.contract", ""]
    for (operation_id, key), enum_name in QUERY_ENUMS.items():
        if enum_name == "CompareOutput":
            continue
        operation = next(op for _, _, op in remaining_operations() if op["operationId"] == operation_id)
        parameter = next(p for p in operation["parameters"] if p["name"] == key)
        lines.append(f"enum {enum_name}(val value: String):")
        for value in parameter.get("enum", parameter.get("items", {}).get("enum", [])):
            case_name = "".join(part.capitalize() for part in re.split(r"[-_]", value))
            lines.append(f'  case {case_name} extends {enum_name}("{value}")')
        lines.append("")
    (MODELS / "QueryEnums.scala").write_text("\n".join(lines))


def sample_value(schema: dict, visited: frozenset[str] = frozenset()) -> object:
    if schema.get("enum"):
        return schema["enum"][0]
    if "$ref" in schema:
        name = schema["$ref"].split("/")[-1]
        if name in visited:
            return {}
        return sample_value(SPEC["definitions"][name], visited | {name})
    kind = schema.get("type")
    if kind == "object":
        if schema.get("additionalProperties") is not None:
            return {"example": sample_value(schema["additionalProperties"], visited)}
        return {
            key: sample_value(value, visited)
            for key, value in schema.get("properties", {}).items()
        }
    if kind == "array":
        item = schema.get("items", {})
        if "$ref" in item and item["$ref"].split("/")[-1] in visited:
            return []
        return [sample_value(schema.get("items", {}), visited)]
    if kind == "string":
        return "2026-09-29T00:00:00Z" if schema.get("format") == "date-time" else "example"
    if kind == "integer":
        return 3
    if kind == "number":
        return 3.5
    if kind == "boolean":
        return False
    return None


def generate_model_tests() -> None:
    tests = ROOT / "core/test/src/io/worxbend/gitea4s/model/contract"
    tests.mkdir(parents=True, exist_ok=True)
    lines = [
        "package io.worxbend.gitea4s.model.contract",
        "",
        "import zio.json.*",
        "import zio.test.*",
        "",
        "object GeneratedModelsSpec extends ZIOSpecDefault:",
        "  def spec =",
        '    suite("every current-contract model round-trips all declared fields")(',
    ]
    cases = []
    for name, schema in SPEC["definitions"].items():
        fixture = json.dumps(sample_value(schema, frozenset({name})), separators=(",", ":"))
        literal = json.dumps(fixture)
        cases.append(
            f'      test("{name}") {{\n'
            f'        val decoded = {literal}.fromJson[{name}]\n'
            f'        assertTrue(decoded.isRight, decoded.flatMap(value => value.toJson.fromJson[{name}]) == decoded)\n'
            f'      }}'
        )
    lines.append(",\n".join(cases))
    lines.append("    )\n")
    (tests / "GeneratedModelsSpec.scala").write_text("\n".join(lines))

    legacy_cases = []
    for name in ("User", "Repository", "Issue", "PullRequest", "Comment", "Release", "Team", "PRBranchInfo"):
        schema = SPEC["definitions"][name]
        fixture = json.dumps(json.dumps(sample_value(schema, frozenset({name})), separators=(",", ":")))
        expected = "Set(" + ", ".join(json.dumps(field) for field in schema["properties"]) + ")"
        legacy_cases.append(
            f'      test("{name}") {{\n'
            f'        val decoded = {fixture}.fromJson[{name}]\n'
            f'        val fields = decoded.toOption.flatMap(_.toJson.fromJson[Json].toOption).collect {{\n'
            f'          case Json.Obj(values) => values.map(_._1).toSet\n'
            f'        }}\n'
            f'        assertTrue(fields.contains({expected}))\n'
            f'      }}'
        )
    legacy_text = (
        "package io.worxbend.gitea4s.model\n\n"
        "import zio.json.*\n"
        "import zio.json.ast.Json\n"
        "import zio.test.*\n\n"
        "object ExistingContractModelsSpec extends ZIOSpecDefault:\n"
        "  def spec =\n"
        '    suite("handwritten response models retain every current field")('\
        "\n" + ",\n".join(legacy_cases) + "\n    )\n"
    )
    (tests.parent / "ExistingContractModelsSpec.scala").write_text(legacy_text)


def remaining_operations() -> list[tuple[str, str, dict]]:
    implemented = (CLIENT / "http/GiteaEndpoint.scala").read_text()
    return [
        (path, method.upper(), operation)
        for path, methods in SPEC["paths"].items()
        for method, operation in methods.items()
        if isinstance(operation, dict)
        and "operationId" in operation
        and f'"{operation["operationId"]}"' not in implemented
        and operation["operationId"] not in EXCEPTIONAL_OPERATIONS
    ]


def group_for(path: str) -> str:
    if "/actions/" in path:
        return "Actions"
    if "/notifications" in path:
        return "Notifications"
    if path.startswith("/repos/"):
        for part, name in (
            ("/issues", "Issues"), ("/pulls", "Pulls"),
            ("/releases", "Releases"),
        ):
            if part in path:
                return name
        return "Repos"
    root = path.split("/")[1]
    return {
        "user": "Users", "users": "Users", "org": "Orgs",
        "orgs": "Orgs", "admin": "Admin", "teams": "Teams",
        "packages": "Packages", "notifications": "Notifications",
    }.get(root, "Catalog")


def parameters(operation: dict) -> tuple[list[dict], list[dict], dict | None, list[dict]]:
    all_parameters = operation.get("parameters", ())
    return (
        [p for p in all_parameters if p["in"] == "path"],
        [p for p in all_parameters if p["in"] == "query"],
        next((p for p in all_parameters if p["in"] == "body"), None),
        [p for p in all_parameters if p["in"] == "formData"],
    )


def argument_type(p: dict) -> str:
    tpe = scala_type(p.get("schema", p))
    if tpe in SPEC["definitions"]:
        tpe = "contract." + tpe
    return tpe


def args_for(operation: dict) -> list[tuple[str, str]]:
    paths, queries, body, form = parameters(operation)
    args = [(identifier(p["name"]), argument_type(p)) for p in paths]
    if body is not None:
        args.append(("body", argument_type(body)))
    args.extend((identifier(p["name"]), argument_type(p)) for p in form)
    args.extend((
        identifier(p["name"]),
        (f"Option[{'List[' if p.get('type') == 'array' else ''}contract.{QUERY_ENUMS[(operation['operationId'], p['name'])]}{']' if p.get('type') == 'array' else ''}] = None"
         if (operation["operationId"], p["name"]) in QUERY_ENUMS
         else argument_type(p) if p.get("required") else f"Option[{argument_type(p)}] = None")
    ) for p in queries)
    return args


def success_schema(response: dict) -> dict | None:
    if "$ref" in response:
        return SPEC["responses"][response["$ref"].split("/")[-1]].get("schema")
    return response.get("schema")


def success(operation: dict) -> tuple[str, str]:
    if operation["operationId"] == "repoCompareDiff":
        return "Either[contract.Compare, String]", (
            "response => if output.isDefined then "
            "(if response.code.code == 200 then GiteaResponseMapper.decodeString(response).map(Right(_)) "
            "else Left(GiteaResponseMapper.toError(response))) "
            "else GiteaResponseMapper.decodeJsonAt[contract.Compare](response, sttp.model.StatusCode.Ok).map(Left(_))"
        )
    if operation["operationId"] == "teamSearch":
        return "contract.TeamSearchResult", "response => GiteaResponseMapper.decodeJsonAt[contract.TeamSearchResult](response, sttp.model.StatusCode.Ok)"
    if operation["operationId"] == "downloadActionsRunJobLogs":
        return "zio.Chunk[Byte]", ""
    if operation["operationId"] == "repoGetEditorConfig":
        return "String", "response => if response.code.code == 200 then GiteaResponseMapper.decodeString(response) else Left(GiteaResponseMapper.toError(response))"
    codes = sorted((int(status), response) for status, response in operation["responses"].items() if status.startswith("2"))
    typed = [(status, success_schema(response)) for status, response in codes]
    nonempty = [(status, schema) for status, schema in typed if schema is not None]
    if not nonempty:
        code_check = " || ".join(f"response.code.code == {code}" for code, _ in codes)
        return "Unit", f"response => if {code_check} then Right(()) else Left(GiteaResponseMapper.toError(response))"
    code, schema = nonempty[0]
    if schema.get("type") == "string":
        code_check = f"response.code.code == {code}"
        decoder = f"response => if {code_check} then GiteaResponseMapper.decodeString(response) else Left(GiteaResponseMapper.toError(response))"
        return "String", decoder
    tpe = argument_type({"schema": schema})
    if schema.get("type") == "array":
        tpe = f"zio.Chunk[{argument_type({'schema': schema['items']})}]"
    allowed = " || ".join(f"response.code.code == {status}" for status, _ in nonempty)
    if len(typed) != len(nonempty):
        no_content = " || ".join(f"response.code.code == {status}" for status, s in typed if s is None)
        decoder = f"response => if {no_content} then Right(None) else if {allowed} then GiteaResponseMapper.decodeJson[{tpe}](response).map(Some(_)) else Left(GiteaResponseMapper.toError(response))"
        return f"Option[{tpe}]", decoder
    if len(nonempty) > 1:
        return tpe, f"response => if {allowed} then GiteaResponseMapper.decodeJson[{tpe}](response) else Left(GiteaResponseMapper.toError(response))"
    return tpe, f"response => GiteaResponseMapper.decodeJsonAt[{tpe}](response, sttp.model.StatusCode({code}))"


def request_method(path: str, method: str, op: dict) -> str:
    method_name = identifier(op["operationId"])
    args = args_for(op)
    signature = ", ".join(f"{n}: {t}" for n, t in args)
    path_params, queries, body, form = parameters(op)
    path_values = []
    for segment in path.lstrip("/").split("/"):
        path_values.append(f"{identifier(segment[1:-1])}.toString" if segment.startswith("{") else f'"{segment}"')
    path_expr = "List(" + ", ".join(path_values) + ")"
    query_values = []
    for query in queries:
        name = identifier(query["name"])
        key = query["name"]
        if query.get("type") == "array":
            field = "value.value" if (op["operationId"], key) in QUERY_ENUMS else "value.toString"
            expr = f"{name}.toList.flatMap(_.map(value => (\"{key}\", {field})))"
        elif (op["operationId"], key) in QUERY_ENUMS:
            expr = f'{name}.toList.map(value => ("{key}", value.value))'
        elif query.get("required"):
            expr = f'List(("{key}", {name}.toString))'
        else:
            expr = f'{name}.toList.map(value => ("{key}", value.toString))'
        query_values.append(expr)
    query_expr = " ++ ".join(query_values) if query_values else "Nil"
    raw_text = op["operationId"] == "renderMarkdownRaw"
    body_expr = "None" if body is None else ("Some(body)" if raw_text else "Some(body.toJson)")
    result_type, decoder = success(op)
    if op["operationId"] == "downloadActionsRunJobLogs":
        return (
            f"  def {method_name}(config: GiteaConfig{', ' if signature else ''}{signature}): GiteaRequest[{result_type}] =\n"
            f"    GiteaRequests.binaryFromContract(config, GeneratedEndpoints.{method_name}, {path_expr}, {query_expr})\n"
        )
    produces = op.get("produces", [])
    accept = (
        "if output.isDefined then Accept.TextPlain else Accept.Json" if op["operationId"] == "repoCompareDiff"
        else "Accept.Html" if produces == ["text/html"]
        else "Accept.TextPlain" if produces == ["text/plain"]
        else "Accept.Json"
    )
    content_type = ", sttp.model.MediaType.TextPlain" if raw_text else ", sttp.model.MediaType.ApplicationJson"
    return (
        f"  def {method_name}(config: GiteaConfig{', ' if signature else ''}{signature}): GiteaRequest[{result_type}] =\n"
        f"    GiteaRequests.fromContract(config, GeneratedEndpoints.{method_name}, {path_expr},\n"
        f"      {query_expr}, {body_expr}, {decoder}{content_type}, {accept})\n"
    )


def endpoint(path: str, method: str, op: dict) -> str:
    name = identifier(op["operationId"])
    params = op.get("parameters", [])
    declared = ", ".join(
        f'GiteaParameter("{p["name"]}", "{p["in"]}", required = {str(p.get("required", False)).lower()})'
        for p in params
    )
    status, response = next((s, r) for s, r in op["responses"].items() if s.startswith("2"))
    label = response.get("$ref") or (
        "type:" + response["schema"]["type"]
        if response.get("schema", {}).get("type") else "description: " + response.get("description", "").splitlines()[0]
    )
    label = label.replace("\n", " ").replace('"', '\\"')
    return (
        f'  val {name}: GiteaEndpoint = GiteaEndpoint("{method}", "{path}", "{op["operationId"]}", '
        f'List({declared}), "{label}")\n'
    )


def generate_operations() -> None:
    operations = remaining_operations()
    assert len(operations) == 308, f"expected 308 generated operations, found {len(operations)}"
    http = CLIENT / "http"
    metadata = "package io.worxbend.gitea4s.http\n\nobject GeneratedEndpoints:\n"
    metadata += "\n".join(endpoint(p, m, o) for p, m, o in operations)
    metadata += "\n  val all: List[GiteaEndpoint] = List(\n" + ",\n".join(
        f"    {identifier(o['operationId'])}" for _, _, o in operations
    ) + "\n  )\n"
    (http / "GeneratedEndpoints.scala").write_text(metadata)
    groups: dict[str, list[tuple[str, str, dict]]] = {}
    for path, method, op in operations:
        groups.setdefault(group_for(path), []).append((path, method, op))
    for group, entries in groups.items():
        requests = (
            "package io.worxbend.gitea4s.http\n\n"
            "import io.worxbend.gitea4s.{Accept, GiteaConfig}\n"
            "import io.worxbend.gitea4s.model.contract\n"
            "import zio.json.*\n\n"
            f"object Generated{group}Requests:\n"
        )
        requests += "\n".join(request_method(p, m, o) for p, m, o in entries)
        (http / f"Generated{group}Requests.scala").write_text(requests)
        trait = (
            "package io.worxbend.gitea4s.api.generated\n\n"
            "import io.worxbend.gitea4s.error.GiteaError\n"
            "import io.worxbend.gitea4s.model.contract\n"
            "import zio.IO\n\n"
            f"trait {group}Operations:\n"
        )
        live = (
            "package io.worxbend.gitea4s.internal.generated\n\n"
            "import io.worxbend.gitea4s.GiteaConfig\n"
            f"import io.worxbend.gitea4s.api.generated.{group}Operations\n"
            "import io.worxbend.gitea4s.error.GiteaError\n"
            f"import io.worxbend.gitea4s.http.Generated{group}Requests\n"
            "import io.worxbend.gitea4s.internal.GiteaRequestExecutor\n"
            "import io.worxbend.gitea4s.model.contract\n"
            "import zio.IO\n\n"
            f"trait Live{group}Operations extends {group}Operations:\n"
            "  protected def config: GiteaConfig\n"
            "  protected def executor: GiteaRequestExecutor\n\n"
        )
        for _, _, op in entries:
            name = identifier(op["operationId"])
            args = args_for(op)
            sig = ", ".join(f"{n}: {t}" for n, t in args)
            tpe, _ = success(op)
            trait += f"  def {name}({sig}): IO[GiteaError, {tpe}]\n"
            live += f"  override def {name}({sig}): IO[GiteaError, {tpe}] =\n"
            live += f"    executor.send(Generated{group}Requests.{name}(config{', ' if args else ''}{', '.join(n for n, _ in args)}))\n\n"
        api_dir = CLIENT / "api/generated"
        live_dir = CLIENT / "internal/generated"
        api_dir.mkdir(parents=True, exist_ok=True)
        live_dir.mkdir(parents=True, exist_ok=True)
        (api_dir / f"{group}Operations.scala").write_text(trait)
        (live_dir / f"Live{group}Operations.scala").write_text(live.rstrip() + "\n")


def test_argument(parameter: dict, operation_id: str) -> str:
    schema = parameter.get("schema", parameter)
    if parameter["in"] == "body":
        value = sample_value(schema)
        tpe = argument_type(parameter)
        if tpe == "String":
            return json.dumps(value)
        literal = json.dumps(json.dumps(value, separators=(",", ":")))
        return f"{literal}.fromJson[{tpe}].toOption.get"
    enum_name = QUERY_ENUMS.get((operation_id, parameter["name"]))
    if enum_name:
        values = parameter.get("enum", parameter.get("items", {}).get("enum", []))
        case_name = "".join(part.capitalize() for part in re.split(r"[-_]", values[0]))
        value = f"contract.{enum_name}.{case_name}"
        return f"Some(List({value}))" if parameter.get("type") == "array" else f"Some({value})"
    if schema.get("type") == "array":
        value = "List(\"one\", \"two\")"
    elif schema.get("type") == "boolean":
        value = "true"
    elif schema.get("type") == "integer":
        value = "11L" if schema.get("format") in ("int64", "uint64") else "11"
    elif schema.get("format") == "date-time":
        value = 'java.time.Instant.parse("2026-09-29T00:00:00Z")'
    else:
        value = '"space name"' if parameter["in"] == "path" else '"example"'
    return value if parameter.get("required") else f"Some({value})"


def wire_case(path: str, method: str, op: dict) -> str:
    name = identifier(op["operationId"])
    group = group_for(path)
    args = [*parameters(op)[0], *([parameters(op)[2]] if parameters(op)[2] else []), *parameters(op)[3], *parameters(op)[1]]
    args_text = ", ".join(test_argument(p, op["operationId"]) for p in args)
    by_name = {p["name"]: p for p in parameters(op)[0]}
    expected_path = re.sub(
        r"\{([^}]+)\}",
        lambda match: "11" if by_name[match.group(1)].get("type") == "integer" else "space%20name",
        path,
    )
    responses = sorted((int(code), r) for code, r in op["responses"].items() if code.startswith("2"))
    success_with_schema = [(code, success_schema(r)) for code, r in responses if success_schema(r) is not None]
    if success_with_schema:
        code, schema = success_with_schema[0]
        fixture = json.dumps(sample_value(schema), separators=(",", ":"))
        if schema.get("type") == "string":
            fixture = sample_value(schema)
    else:
        code = responses[0][0]
        fixture = ""
    if op["operationId"] == "repoCompareDiff":
        fixture = "diff --git a/file b/file\n"
    if op["operationId"] == "repoGetEditorConfig":
        fixture = "root = true\n"
    if op["operationId"] == "downloadActionsRunJobLogs":
        return (
            f'      test("{op["operationId"]}") {{\n'
            f'        val built = Generated{group}Requests.{name}(config{", " if args_text else ""}{args_text})\n'
            f'        val request = built.request\n'
            '        val bytes = Array[Byte](0, 1, -1, 80)\n'
            '        val backend = BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(bytes))\n'
            '        val decoded = built.decode(request.send(backend))\n'
            f'        assertTrue(request.method == sttp.model.Method.GET, request.uri.toString == "https://gitea.example/root/api/v1{expected_path}",\n'
            '          request.header("Accept").contains("application/octet-stream"), decoded == Right(zio.Chunk.fromArray(bytes)), built.retryable)\n'
            '      }'
        )
    payload = json.dumps(fixture)
    assertions = [
        f'request.method == sttp.model.Method.{method}',
        f'request.uri.toString.startsWith("https://gitea.example/root/api/v1{expected_path}")',
        'request.header("Authorization").contains("token secret")',
        f'built.retryable == {str(method == "GET").lower()}',
        "decoded.isRight",
        "missing.isLeft",
    ]
    failures = [int(status) for status in op["responses"] if not status.startswith("2")]
    failures_expr = "List(" + ", ".join(str(status) for status in failures) + ")"
    assertions.append("documentedFailures")
    alternate_successes = []
    for status, response in responses:
        if status == code:
            continue
        schema = success_schema(response)
        if schema is not None:
            body_for_status = json.dumps(json.dumps(sample_value(schema), separators=(",", ":")))
        else:
            body_for_status = '""'
        alternate_successes.append(
            f'built.decode(request.send(respond({body_for_status}, sttp.model.StatusCode({status})))).isRight'
        )
    alternate_expr = " && ".join(alternate_successes) if alternate_successes else "true"
    assertions.append("otherSuccesses")
    queries = parameters(op)[1]
    expected_query = []
    for query in queries:
        key = query["name"]
        schema = query.get("schema", query)
        if schema.get("type") == "array":
            expected_query.extend([(key, schema["items"]["enum"][0])] if (op["operationId"], key) in QUERY_ENUMS else [(key, "one"), (key, "two")])
        elif schema.get("type") == "boolean":
            expected_query.append((key, "true"))
        elif schema.get("type") == "integer":
            expected_query.append((key, "11"))
        elif schema.get("format") == "date-time":
            expected_query.append((key, "2026-09-29T00:00:00Z"))
        elif (op["operationId"], key) in QUERY_ENUMS:
            expected_query.append((key, query["enum"][0]))
        else:
            expected_query.append((key, "example"))
    query_expr = "Seq(" + ", ".join(f'("{key}", "{value}")' for key, value in expected_query) + ")"
    assertions.append(f"request.uri.paramsSeq == {query_expr}")
    produces = op.get("produces", [])
    if produces == ["text/html"]:
        assertions.append('request.header("Accept").contains("text/html")')
    elif produces == ["text/plain"] or op["operationId"] == "repoCompareDiff":
        assertions.append('request.header("Accept").contains("text/plain")')
    body = parameters(op)[2]
    if body is not None:
        body_value = json.dumps(sample_value(body.get("schema", {})), separators=(",", ":"))
        if op["operationId"] == "renderMarkdownRaw":
            assertions.append('request.header("Content-Type").exists(_.startsWith("text/plain"))')
            assertions.append(f"stringBody(request) == {json.dumps(sample_value(body['schema']))}")
        else:
            assertions.append('request.header("Content-Type").exists(_.startsWith("application/json"))')
            assertions.append(f"stringBody(request).fromJson[zio.json.ast.Json] == {json.dumps(body_value)}.fromJson[zio.json.ast.Json]")
    else:
        assertions.append('request.body == NoBody')
    return (
        f'      test("{op["operationId"]}") {{\n'
        f'        val built = Generated{group}Requests.{name}(config{", " if args_text else ""}{args_text})\n'
        f'        val request = built.request\n'
        f'        val decoded = built.decode(request.send(respond({payload}, sttp.model.StatusCode({code}))))\n'
        f'        val missing = built.decode(request.send(respond("", sttp.model.StatusCode.NotFound)))\n'
        f'        val documentedFailures = {failures_expr}.forall(status => built.decode(request.send(respond("", sttp.model.StatusCode(status)))).isLeft)\n'
        f'        val otherSuccesses = {alternate_expr}\n'
        f'        assertTrue({", ".join(assertions)})\n'
        f'      }}'
    )


def generate_wire_tests() -> None:
    tests = ROOT / "client/test/src/io/worxbend/gitea4s/http/contract"
    tests.mkdir(parents=True, exist_ok=True)
    entries = remaining_operations()
    for index in range(0, len(entries), 30):
        chunk = entries[index:index + 30]
        name = f"GeneratedWire{index // 30:02d}Spec"
        header = (
            "package io.worxbend.gitea4s.http.contract\n\n"
            "import io.worxbend.gitea4s.GiteaConfig\n"
            "import io.worxbend.gitea4s.http.*\n"
            "import io.worxbend.gitea4s.model.Auth\n"
            "import io.worxbend.gitea4s.model.contract\n"
            "import sttp.client4.*\n"
            "import sttp.client4.testing.{BackendStub, ResponseStub}\n"
            "import sttp.model.StatusCode\n"
            "import zio.json.*\n"
            "import zio.test.*\n\n"
            f"object {name} extends ZIOSpecDefault:\n"
            '  private val config = GiteaConfig.default(uri"https://gitea.example/root", Auth.Token("secret"))\n'
            "  private def respond(body: String, status: StatusCode) =\n"
            "    BackendStub.synchronous.whenAnyRequest.thenRespond(ResponseStub.adjust(body, status))\n\n"
            "  private def stringBody(request: Request[?]): String =\n"
            "    request.body match\n"
            "      case StringBody(value, _, _) => value\n"
            "      case other => other.toString\n\n"
            "  def spec =\n"
            '    suite("generated contract wire requests")('\
            "\n"
        )
        text = header + ",\n".join(wire_case(p, m, o) for p, m, o in chunk) + "\n    )\n"
        (tests / f"{name}.scala").write_text(text)


def response_label(response: dict) -> str:
    if "$ref" in response:
        return response["$ref"]
    schema = response.get("schema", {})
    if "type" in schema:
        return "type:" + schema["type"]
    if "$ref" in schema:
        return schema["$ref"]
    return "description: " + response.get("description", "").splitlines()[0]


def generate_audit_expectations() -> None:
    operations = remaining_operations() + [
        (path, method.upper(), operation)
        for path, methods in SPEC["paths"].items()
        for method, operation in methods.items()
        if isinstance(operation, dict) and operation.get("operationId") in EXCEPTIONAL_OPERATIONS
    ]
    entries = []
    for _, _, op in operations:
        labels = ", ".join(
            f"({json.dumps(status)}, {json.dumps(response_label(response))})"
            for status, response in op["responses"].items()
        )
        entries.append(f'    {json.dumps(op["operationId"])} -> List({labels})')
    source = (
        "package io.worxbend.gitea4s.http.contract\n\n"
        "object GeneratedAuditExpectations:\n"
        "  val responseLabels: Map[String, List[(String, String)]] = Map(\n"
        + ",\n".join(entries) + "\n  )\n"
    )
    out = ROOT / "client/test/src/io/worxbend/gitea4s/http/contract/GeneratedAuditExpectations.scala"
    out.write_text(source)


def render_type(schema: dict) -> str:
    if "$ref" in schema:
        return "`contract." + schema["$ref"].split("/")[-1] + "`"
    if schema.get("type") == "array":
        return "list of " + render_type(schema.get("items", {}))
    if schema.get("type") == "object" and schema.get("additionalProperties"):
        return "map of " + render_type(schema["additionalProperties"])
    if schema.get("format") == "date-time":
        return "timestamp"
    return schema.get("type", "unspecified JSON value")


def generate_docs() -> None:
    docs = ROOT / "docs"
    operations = remaining_operations() + [
        (path, method.upper(), operation)
        for path, methods in SPEC["paths"].items()
        for method, operation in methods.items()
        if isinstance(operation, dict) and operation.get("operationId") in EXCEPTIONAL_OPERATIONS
    ]
    groups: dict[str, list[tuple[str, str, dict]]] = {}
    for path, method, op in operations:
        groups.setdefault(group_for(path), []).append((path, method, op))
    text = [
        "# Gitea 1.27.3 operation reference",
        "",
        "This page covers the 312 operations added after the initial handwritten client surface.",
        "Every method is available on the ordinary `GiteaClient` namespace named below.",
        "The wire models live in `io.worxbend.gitea4s.model.contract`; see [contract models](contract-models.md).",
        "Path arguments are escaped as individual segments. Optional query arguments default to `None`.",
        "Mutating requests are not automatically retried. Multipart uploads require `AttachmentUpload`.",
        "",
    ]
    namespace = {
        "Actions": "actions", "Admin": "admin", "Catalog": "catalog",
        "Issues": "issues", "Notifications": "notifications", "Orgs": "orgs",
        "Packages": "packages", "Releases": "releases", "Repos": "repos",
        "Teams": "teams", "Users": "users",
    }
    manual_names = {
        "downloadArtifact": "downloadArtifact(owner: String, repo: String, artifactId: String): IO[GiteaError, Chunk[Byte]]",
        "issueCreateIssueCommentAttachment": "createCommentAttachment(owner: String, repo: String, id: Long, upload: AttachmentUpload, name: Option[String] = None): IO[GiteaError, contract.Attachment]",
        "issueCreateIssueAttachment": "createIssueAttachment(owner: String, repo: String, index: Long, upload: AttachmentUpload, name: Option[String] = None): IO[GiteaError, contract.Attachment]",
        "repoCreateReleaseAttachment": "createAttachment(owner: String, repo: String, id: Long, upload: AttachmentUpload, name: Option[String] = None): IO[GiteaError, contract.Attachment]",
    }
    for group, entries in groups.items():
        text.extend([f"## {group}", ""])
        for path, method, op in entries:
            op_id = op["operationId"]
            if op_id in manual_names:
                sig = manual_names[op_id]
            else:
                params = ", ".join(f"{name}: {tpe}" for name, tpe in args_for(op))
                result, _ = success(op)
                sig = f"{identifier(op_id)}({params}): IO[GiteaError, {result}]"
            text.extend([
                f"### `{op_id}`",
                "",
                f"**{method} `{path}`** — ``client.{namespace[group]}.{sig}``",
                "",
            ])
            for p in op.get("parameters", []):
                description = p.get("description", "").strip().replace("\n", " ")
                required = "required" if p.get("required") else "optional"
                shape = render_type(p.get("schema", p))
                text.append(f"- `{p['name']}` ({p['in']}, {required}, {shape})" + (f": {description}" if description else ""))
            for status, response in op["responses"].items():
                text.append(f"- HTTP {status}: `{response_label(response)}`")
            text.append("")
    (docs / "contract-operations.md").write_text("\n".join(text))

    model_lines = [
        "# Gitea 1.27.3 wire models", "",
        "Generated Scala models for every schema definition in `gitea-v1.27.3.yaml`.",
        "Wire names are preserved with `@jsonField`; absent optional fields remain `None`.",
        "Schema fields without a declared type use `zio.json.ast.Json` rather than guessing a shape.",
        "",
    ]
    for name, definition in SPEC["definitions"].items():
        model_lines.extend([f"## `{name}`", ""])
        if not definition.get("properties"):
            model_lines.extend([f"Value: {render_type(definition)}.", ""])
            continue
        required = set(definition.get("required", ()))
        for field, shape in definition["properties"].items():
            optional = "required" if field in required else "optional"
            model_lines.append(f"- `{field}` — {render_type(shape)} ({optional})")
        model_lines.append("")
    (docs / "contract-models.md").write_text("\n".join(model_lines))



if __name__ == "__main__":
    generate_models()
    generate_query_enums()
    generate_model_tests()
    generate_operations()
    generate_wire_tests()
    generate_audit_expectations()
    generate_docs()
