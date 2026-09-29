#!/usr/bin/python3
"""Add newly declared response fields to the handwritten core models.

This runs only for the seven models that predate the full-contract generator.
It is idempotent and never removes existing fields or changes their types.
"""

from __future__ import annotations

import re
from pathlib import Path

from generate_contract import SPEC, identifier, scala_type


ROOT = Path(__file__).resolve().parent.parent
SOURCE = ROOT / "core/src/io/worxbend/gitea4s/model/GiteaModels.scala"
TARGETS = ("User", "Repository", "Issue", "PullRequest", "Comment", "Release", "Team")


def model_bounds(source: str, name: str) -> tuple[int, int]:
    match = re.search(r"final case class " + name + r"\(", source)
    if match is None:
        raise ValueError(f"Missing case class {name}")
    start, index, depth = match.end(), match.end(), 1
    while depth:
        if source[index] == "(":
            depth += 1
        elif source[index] == ")":
            depth -= 1
        index += 1
    return start, index - 1


def field_type(field: dict) -> str:
    tpe = scala_type(field)
    if "$ref" in field:
        name = field["$ref"].split("/")[-1]
        if name == "Repository" or (name not in TARGETS and name not in ("Organization", "Permission", "UserMeta", "Identity")):
            return "contract." + name
    if field.get("type") == "array" and "$ref" in field.get("items", {}):
        name = field["items"]["$ref"].split("/")[-1]
        if name not in TARGETS:
            return f"List[contract.{name}]"
    return tpe


def add_fields(source: str, name: str) -> str:
    start, end = model_bounds(source, name)
    body = source[start:end]
    wire_names = set(re.findall(r'@jsonField\("([^\"]+)"\)', body))
    wire_names.update(re.findall(r"(?m)^\s+(?!@jsonField)([a-z][A-Za-z0-9]*)\s*:", body))
    definitions = SPEC["definitions"][name]["properties"]
    missing = [(key, definition) for key, definition in definitions.items() if key not in wire_names]
    if not missing:
        return source
    additions = ",\n".join(
        f'    @jsonField("{key}") {identifier(key)}: Option[{field_type(definition)}] = None'
        for key, definition in missing
    )
    return source[:end].rstrip() + ",\n" + additions + "\n" + source[end:]


def main() -> None:
    for name in TARGETS:
        path = SOURCE.parent / "Repository.scala" if name == "Repository" else SOURCE
        source = path.read_text()
        updated = add_fields(source, name)
        if source != updated:
            path.write_text(updated)


if __name__ == "__main__":
    main()
