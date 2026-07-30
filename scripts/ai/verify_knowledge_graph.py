#!/usr/bin/env python3
"""Verify local document, project skill, custom agent, and history wiring."""

from __future__ import annotations

import argparse
import re
import subprocess
from pathlib import Path

from registry import load

LINK = re.compile(r"\[[^\]]*\]\(([^)\s]+)(?:\s+[^)]*)?\)")
FRONTMATTER = re.compile(r"\A---\n(.*?)\n---\n", re.DOTALL)
FIELD = re.compile(r"^([a-z_]+):\s*(.+)$", re.MULTILINE)


class Reporter:
    def __init__(self) -> None:
        self.errors: list[str] = []

    def error(self, message: str) -> None:
        self.errors.append(message)

    def finish(self) -> int:
        for error in self.errors:
            print(f"ERROR {error}")
        print(f"Knowledge graph: {len(self.errors)} error(s)")
        return 1 if self.errors else 0


def list_value(value: str) -> list[str]:
    return [item.strip().strip('"') for item in value.strip()[1:-1].split(",") if item.strip()] if value.startswith("[") else []


def metadata(path: Path) -> dict[str, str]:
    match = FRONTMATTER.match(path.read_text())
    return dict(FIELD.findall(match.group(1))) if match else {}


def valid_commit(root: Path, revision: str) -> bool:
    return subprocess.run(["git", "rev-parse", "--verify", f"{revision}^{{commit}}"], cwd=root, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL).returncode == 0


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, default=Path.cwd())
    args = parser.parse_args()
    root = args.root.resolve()
    reporter = Reporter()
    registry_root = root / "docs/ai/registry"
    documents = load(registry_root / "documents.toml").get("document", [])
    skills = load(registry_root / "skills.toml").get("skill", [])
    agents = load(registry_root / "agents.toml").get("agent", [])
    load(registry_root / "policies.toml")
    document_ids = {str(item.get("id")) for item in documents}
    registered_docs = {str(item.get("path")) for item in documents}
    for item in documents:
        if not (root / str(item["path"])).is_file():
            reporter.error(f"registered document missing: {item['path']}")
        for code_path in item.get("code_paths", []):
            if not (root / str(code_path)).exists():
                reporter.error(f"registered code path missing: {code_path}")
    for path in (root / "docs").rglob("*.md"):
        if not str(path.relative_to(root)).startswith("docs/history/records/") and str(path.relative_to(root)) not in registered_docs:
            reporter.error(f"unregistered project document: {path.relative_to(root)}")
    for source in [root / "AGENTS.md", *(root / "docs").rglob("*.md"), *(root / ".codex/skills").rglob("*.md")]:
        for target in LINK.findall(source.read_text()):
            if target.startswith(("#", "http://", "https://", "mailto:")):
                continue
            if not (source.parent / target.strip("<>").split("#", 1)[0]).resolve().exists():
                reporter.error(f"broken Markdown link: {source.relative_to(root)} -> {target}")
    skill_paths = {str(item["path"]): item for item in skills}
    for path in (root / ".codex/skills").glob("*/SKILL.md"):
        if str(path.relative_to(root)) not in skill_paths:
            reporter.error(f"unregistered project skill: {path.relative_to(root)}")
    for item in skills:
        path = root / str(item["path"])
        if not path.is_file():
            reporter.error(f"registered skill missing: {item['path']}")
        for document_id in item.get("required_documents", []):
            if document_id not in document_ids:
                reporter.error(f"skill references unknown document: {document_id}")
    agent_paths = {str(item["path"]): item for item in agents}
    for path in (root / ".codex/agents").glob("*.toml"):
        if str(path.relative_to(root)) not in agent_paths:
            reporter.error(f"unregistered project agent: {path.relative_to(root)}")
    for item in agents:
        if not (root / str(item["path"])).is_file():
            reporter.error(f"registered agent missing: {item['path']}")
        for document_id in item.get("required_documents", []):
            if document_id not in document_ids:
                reporter.error(f"agent references unknown document: {document_id}")
    required = ("## 요청", "## 근거", "## 결정", "## 변경", "## 검증", "## 지시 이행", "## 스킬 평가", "## 개선 후보")
    for path in (root / "docs/history/records").rglob("*.md"):
        data = metadata(path)
        if data.get("status") not in {"in_progress", "completed", "interrupted"} or not valid_commit(root, data.get("base_commit", "")):
            reporter.error(f"invalid history metadata: {path.relative_to(root)}")
        for document_id in list_value(data.get("related_documents", "")):
            if document_id not in document_ids:
                reporter.error(f"history references unknown document: {document_id}")
        if data.get("status") == "completed" and any(section not in path.read_text() for section in required):
            reporter.error(f"completed history record lacks required sections: {path.relative_to(root)}")
    return reporter.finish()


if __name__ == "__main__":
    raise SystemExit(main())
