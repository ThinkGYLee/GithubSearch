#!/usr/bin/env python3
"""Generate the human-readable knowledge index from the document registry."""

from __future__ import annotations

import argparse
from collections import defaultdict
from pathlib import Path

from registry import load


def render(root: Path) -> str:
    # `local_document` entries deliberately stay outside the shared index.
    documents = load(root / "docs/ai/registry/documents.toml").get("document", [])
    groups: dict[str, list[dict[str, object]]] = defaultdict(list)
    for document in documents:
        groups[str(document["kind"])].append(document)
    lines = [
        "# 프로젝트 지식 인덱스",
        "",
        "> 이 파일은 `docs/ai/registry/documents.toml`에서 생성됩니다. 직접 수정하지 마세요.",
        "",
        "작업 시작 시 요청과 겹치는 문서 및 관련 코드 경로를 함께 읽는다. 문서와 코드가 충돌하면 현재 코드와 Git 이력을 확인하고, 근거가 충분해질 때까지 결론을 보류한다.",
        "",
    ]
    for kind in sorted(groups):
        lines.extend([f"## {kind}", "", "| ID | 문서 | 권한 | 관련 코드 |", "|---|---|---|---|"])
        for document in sorted(groups[kind], key=lambda item: str(item["id"])):
            paths = ", ".join(str(item) for item in document["code_paths"])
            path = str(document["path"])
            lines.append(f"| `{document['id']}` | [{path}](../../{path}) | {document['authority']} | `{paths}` |")
        lines.append("")
    return "\n".join(lines)


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, default=Path.cwd())
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    root = args.root.resolve()
    target = root / "docs/knowledge/INDEX.md"
    expected = render(root)
    if args.check:
        print("Knowledge index is current." if target.exists() and target.read_text() == expected else "Knowledge index is stale.")
        return 0 if target.exists() and target.read_text() == expected else 1
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(expected)
    print(f"Wrote {target.relative_to(root)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
