"""Minimal TOML reader for the flat AI reporting registry files."""

from __future__ import annotations

import ast
from pathlib import Path


def load(path: Path) -> dict[str, object]:
    result: dict[str, object] = {}
    current: dict[str, object] | None = None
    for raw in path.read_text().splitlines():
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        if line.startswith("[[") and line.endswith("]]" ):
            key = line[2:-2]
            entries = result.setdefault(key, [])
            current = {}
            entries.append(current)
            continue
        if line.startswith("[") and line.endswith("]"):
            current = result.setdefault(line[1:-1], {})
            continue
        key, separator, value = line.partition("=")
        if not separator:
            raise ValueError(f"{path}: invalid TOML line: {raw}")
        raw_value = value.strip()
        parsed = ast.literal_eval(raw_value) if raw_value.startswith(("\"", "[")) else raw_value
        (current if current is not None else result)[key.strip()] = parsed
    return result
