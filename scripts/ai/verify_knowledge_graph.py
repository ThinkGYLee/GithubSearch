#!/usr/bin/env python3
"""Verify shared documentation, project skills, agents, and task-history wiring."""

from __future__ import annotations

import argparse
import re
import subprocess
from pathlib import Path
from typing import Optional

from registry import load


MARKDOWN_LINK = re.compile(r"\[[^\]]*\]\(([^)\s]+)(?:\s+[^)]*)?\)")
FRONTMATTER = re.compile(r"\A---\n(.*?)\n---\n", re.DOTALL)
FIELD = re.compile(r"^([a-z_]+):\s*(.+)$", re.MULTILINE)
LIST = re.compile(r"^\[(.*)\]$")


class Reporter:
    def __init__(self) -> None:
        self.errors: list[str] = []
        self.warnings: list[str] = []

    def error(self, message: str) -> None:
        self.errors.append(message)

    def warning(self, message: str) -> None:
        self.warnings.append(message)

    def finish(self) -> int:
        for message in self.warnings:
            print(f"WARN  {message}")
        for message in self.errors:
            print(f"ERROR {message}")
        print(f"Knowledge graph: {len(self.errors)} error(s), {len(self.warnings)} warning(s)")
        return 1 if self.errors else 0


def require_fields(item: dict[str, object], label: str, reporter: Reporter, *fields: str) -> None:
    for field in fields:
        value = item.get(field)
        if value == "" or value == [] or value is None:
            reporter.error(f"{label} missing required field: {field}")


def unique_ids(items: list[dict[str, object]], label: str, reporter: Reporter) -> dict[str, dict[str, object]]:
    indexed: dict[str, dict[str, object]] = {}
    for item in items:
        item_id = str(item.get("id", ""))
        if not item_id:
            reporter.error(f"{label} registry has an entry without id")
        elif item_id in indexed:
            reporter.error(f"duplicate {label} id: {item_id}")
        else:
            indexed[item_id] = item
    return indexed


def parse_list(value: object) -> list[str]:
    if isinstance(value, list):
        return [str(item) for item in value]
    if not isinstance(value, str):
        return []
    match = LIST.match(value.strip())
    if not match or not match.group(1).strip():
        return []
    return [part.strip().strip('"') for part in match.group(1).split(",")]


def parse_frontmatter(path: Path) -> dict[str, str]:
    match = FRONTMATTER.match(path.read_text())
    return dict(FIELD.findall(match.group(1))) if match else {}


def parse_skill(path: Path, reporter: Reporter) -> tuple[str, str]:
    content = path.read_text()
    match = FRONTMATTER.match(content)
    if not match:
        reporter.error(f"skill frontmatter missing: {path}")
        return "", ""
    fields = dict(FIELD.findall(match.group(1)))
    name = fields.get("name", "").strip('"')
    description = fields.get("description", "").strip('"')
    if not name:
        reporter.error(f"skill name missing: {path}")
    if not description:
        reporter.error(f"skill description missing: {path}")
    if path.parent.name != name:
        reporter.error(f"skill directory/name mismatch: {path.parent.name} != {name}")
    if len(content.splitlines()) > 500:
        reporter.warning(f"skill exceeds 500 lines: {path}")
    if "[TODO" in content:
        reporter.error(f"skill contains unfinished TODO: {path}")
    if not content[match.end() :].strip():
        reporter.error(f"skill body missing: {path}")
    interface_path = path.parent / "agents/openai.yaml"
    if interface_path.exists():
        interface = interface_path.read_text()
        for key in ("display_name:", "short_description:", "default_prompt:"):
            if key not in interface:
                reporter.error(f"skill UI metadata missing {key}: {interface_path}")
        if f"${name}" not in interface:
            reporter.error(f"skill UI prompt does not name skill: {interface_path}")
    return name, description


def parse_agent(path: Path, reporter: Reporter) -> str:
    content = path.read_text()
    name_match = re.search(r'^name\s*=\s*"([^"]+)"$', content, re.MULTILINE)
    if not name_match:
        reporter.error(f"agent name missing: {path}")
        return ""
    if not re.search(r'^description\s*=\s*".+"$', content, re.MULTILINE):
        reporter.error(f"agent description missing: {path}")
    instructions = re.search(r'developer_instructions\s*=\s*"""(.*?)"""', content, re.DOTALL)
    if not instructions or not instructions.group(1).strip():
        reporter.error(f"agent developer_instructions missing: {path}")
    if content.count('"""') % 2:
        reporter.error(f"agent multiline string is unbalanced: {path}")
    return name_match.group(1)


def local_link_target(source: Path, target: str) -> Optional[Path]:
    target = target.strip("<>")
    if not target or target.startswith(("#", "http://", "https://", "mailto:", "data:")):
        return None
    return (source.parent / target.split("#", 1)[0]).resolve()


def validate_links(root: Path, sources: set[Path], reporter: Reporter) -> None:
    for source in sorted(sources):
        if not source.is_file():
            continue
        for target in MARKDOWN_LINK.findall(source.read_text()):
            destination = local_link_target(source, target)
            if destination is not None and not destination.exists():
                reporter.error(f"broken Markdown link: {source.relative_to(root)} -> {target}")


def validate_document_coverage(
    root: Path,
    shared_paths: set[str],
    local_paths: set[str],
    reporter: Reporter,
) -> None:
    registered_paths = shared_paths | local_paths
    for path in (root / "docs").rglob("*.md"):
        relative = str(path.relative_to(root))
        if relative.startswith("docs/history/records/"):
            continue
        if relative not in registered_paths:
            reporter.error(f"unregistered project document: {relative}")


def validate_catalog(root: Path, skill_paths: set[Path], reporter: Reporter) -> None:
    catalog = root / "docs/ai/SKILLS_CATALOG.md"
    if not catalog.is_file():
        reporter.error("project skill catalog missing: docs/ai/SKILLS_CATALOG.md")
        return
    catalog_paths: set[Path] = set()
    for target in MARKDOWN_LINK.findall(catalog.read_text()):
        destination = local_link_target(catalog, target)
        if destination is not None and destination.name == "SKILL.md" and ".codex/skills" in str(destination):
            catalog_paths.add(destination)
    for path in sorted(skill_paths - catalog_paths):
        reporter.error(f"registered skill missing from Catalog: {path.relative_to(root)}")
    for path in sorted(catalog_paths - skill_paths):
        reporter.error(f"Catalog references unregistered skill: {path.relative_to(root)}")


def git_commit_exists(root: Path, revision: str) -> bool:
    if not revision:
        return False
    try:
        return subprocess.run(
            ["git", "rev-parse", "--verify", f"{revision}^{{commit}}"],
            cwd=root,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            check=False,
        ).returncode == 0
    except OSError:
        return False


def validate_history(
    root: Path,
    shared_document_ids: set[str],
    skill_ids: set[str],
    reporter: Reporter,
) -> set[Path]:
    required_sections = ("## 요청", "## 근거", "## 결정", "## 변경", "## 검증", "## 지시 이행", "## 스킬 평가", "## 개선 후보")
    record_ids: set[str] = set()
    paths = set((root / "docs/history/records").glob("**/*.md"))
    for path in paths:
        metadata = parse_frontmatter(path)
        record_id = metadata.get("id")
        if not record_id:
            reporter.error(f"history record metadata missing: {path.relative_to(root)}")
            continue
        if record_id in record_ids:
            reporter.error(f"duplicate history record id: {record_id}")
        record_ids.add(record_id)
        status = metadata.get("status")
        if status not in {"in_progress", "completed", "interrupted"}:
            reporter.error(f"invalid history status: {record_id}")
        if not git_commit_exists(root, metadata.get("base_commit", "")):
            reporter.error(f"history base_commit is invalid: {record_id}")
        for document_id in parse_list(metadata.get("related_documents", "")):
            if document_id not in shared_document_ids:
                reporter.error(f"history references unknown shared document: {record_id} -> {document_id}")
        for skill_id in parse_list(metadata.get("skills_used", "")):
            if skill_id not in skill_ids:
                reporter.error(f"history references unknown skill: {record_id} -> {skill_id}")
        for code_path in parse_list(metadata.get("related_code", "")):
            if not (root / code_path).exists():
                reporter.error(f"history references missing code path: {record_id} -> {code_path}")
        if status == "completed":
            content = path.read_text()
            for section in required_sections:
                if section not in content:
                    reporter.error(f"completed history record lacks {section}: {record_id}")
    return paths


def verify(root: Path, print_coverage: bool = True) -> Reporter:
    root = root.resolve()
    reporter = Reporter()
    registry_root = root / "docs/ai/registry"
    document_registry = load(registry_root / "documents.toml")
    documents = document_registry.get("document", [])
    local_documents = document_registry.get("local_document", [])
    skills = load(registry_root / "skills.toml").get("skill", [])
    agents = load(registry_root / "agents.toml").get("agent", [])
    load(registry_root / "policies.toml")

    document_index = unique_ids(documents, "document", reporter)
    local_document_index = unique_ids(local_documents, "local document", reporter)
    skill_index = unique_ids(skills, "skill", reporter)
    agent_index = unique_ids(agents, "agent", reporter)
    for local_id in local_document_index:
        if local_id in document_index:
            reporter.error(f"duplicate document id across shared/local registry: {local_id}")

    shared_paths: set[str] = set()
    local_paths: set[str] = set()
    shared_sources: set[Path] = {root / "AGENTS.md"}
    for document_id, document in document_index.items():
        require_fields(document, f"document {document_id}", reporter, "id", "path", "kind", "authority", "code_paths")
        path = root / str(document.get("path", ""))
        shared_paths.add(str(document.get("path", "")))
        if not path.is_file():
            reporter.error(f"registered document missing: {document_id} -> {document.get('path', '')}")
        else:
            shared_sources.add(path)
        for code_path in parse_list(document.get("code_paths", [])):
            if not (root / code_path).exists():
                reporter.error(f"registered code path missing: {document_id} -> {code_path}")
    for document_id, document in local_document_index.items():
        require_fields(document, f"local document {document_id}", reporter, "id", "path", "purpose")
        local_paths.add(str(document.get("path", "")))
    validate_document_coverage(root, shared_paths, local_paths, reporter)

    discovered_skills: dict[str, Path] = {}
    for path in (root / ".codex/skills").glob("*/SKILL.md"):
        name, _ = parse_skill(path, reporter)
        if not name:
            continue
        if name in discovered_skills:
            reporter.error(f"duplicate discovered skill: {name}")
        discovered_skills[name] = path
        shared_sources.add(path)
    for skill_id, skill in skill_index.items():
        require_fields(skill, f"skill {skill_id}", reporter, "id", "path", "required_documents")
        path = root / str(skill.get("path", ""))
        if not path.is_file():
            reporter.error(f"registered skill missing: {skill_id} -> {skill.get('path', '')}")
        elif discovered_skills.get(skill_id) != path:
            reporter.error(f"registered skill path/name mismatch: {skill_id}")
        for document_id in parse_list(skill.get("required_documents", [])):
            if document_id not in document_index:
                reporter.error(f"skill references unknown shared document: {skill_id} -> {document_id}")
    for skill_id in discovered_skills:
        if skill_id not in skill_index:
            reporter.error(f"unregistered project skill: {skill_id}")
    validate_catalog(root, {root / str(skill.get("path", "")) for skill in skill_index.values()}, reporter)

    discovered_agents: dict[str, Path] = {}
    for path in (root / ".codex/agents").glob("*.toml"):
        name = parse_agent(path, reporter)
        if not name:
            continue
        if name in discovered_agents:
            reporter.error(f"duplicate discovered agent: {name}")
        discovered_agents[name] = path
    for agent_id, agent in agent_index.items():
        require_fields(agent, f"agent {agent_id}", reporter, "id", "path", "required_documents")
        path = root / str(agent.get("path", ""))
        if not path.is_file():
            reporter.error(f"registered agent missing: {agent_id} -> {agent.get('path', '')}")
        elif discovered_agents.get(agent_id) != path:
            reporter.error(f"registered agent name mismatch: {agent_id}")
        for document_id in parse_list(agent.get("required_documents", [])):
            if document_id not in document_index:
                reporter.error(f"agent references unknown shared document: {agent_id} -> {document_id}")
    for agent_id in discovered_agents:
        if agent_id not in agent_index:
            reporter.error(f"unregistered project agent: {agent_id}")

    history_paths = validate_history(root, set(document_index), set(skill_index), reporter)
    validate_links(root, shared_sources | history_paths, reporter)
    if print_coverage:
        print(
            f"Coverage: {len(document_index)} shared document(s), {len(local_document_index)} local document(s), "
            f"{len(skill_index)} skill(s), {len(agent_index)} agent(s), "
            f"{len(discovered_skills)} discovered project skill(s), {len(discovered_agents)} discovered project agent(s)"
        )
    return reporter


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--root", type=Path, default=Path.cwd())
    args = parser.parse_args()
    return verify(args.root).finish()


if __name__ == "__main__":
    raise SystemExit(main())
