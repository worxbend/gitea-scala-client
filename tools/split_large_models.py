#!/usr/bin/python3
"""Keep large contract models in separate compilation units for codec derivation."""

from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent
DIRECTORY = ROOT / "core/src/io/worxbend/gitea4s/model"
SOURCE = DIRECTORY / "GiteaModels.scala"


def split(name: str, next_name: str) -> None:
    source = SOURCE.read_text()
    if f"final case class {name}(" not in source:
        return
    beginning = source.index(f"final case class {name}(")
    ending = source.index(f"final case class {next_name}(", beginning)
    section = source[beginning:ending].rstrip()
    destination = DIRECTORY / f"{name}.scala"
    destination.write_text(
        "package io.worxbend.gitea4s.model\n\n"
        "import java.time.Instant\n"
        "import zio.json.*\n"
        "\n"
        + section + "\n"
    )
    SOURCE.write_text(source[:beginning] + source[ending:])


if __name__ == "__main__":
    split("Repository", "TopicNames")
