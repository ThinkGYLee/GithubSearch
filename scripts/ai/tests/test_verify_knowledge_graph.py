from __future__ import annotations

import subprocess
import sys
import tempfile
import unittest
from shutil import copy2
from pathlib import Path


SCRIPT_ROOT = Path(__file__).resolve().parents[1]
PROJECT_ROOT = SCRIPT_ROOT.parents[1]
sys.path.insert(0, str(SCRIPT_ROOT))

from generate_knowledge_index import render
from verify_knowledge_graph import verify


class KnowledgeGraphVerifierTest(unittest.TestCase):
    def make_root(self) -> Path:
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        root = Path(temporary.name)
        self.write(root / "AGENTS.md", "# Agent guide\n")
        self.write(root / "src/.keep", "")
        self.write(root / "scripts/ai/.keep", "")
        self.write(root / "docs/guide.md", "# Shared guide\n")
        self.write(
            root / "docs/ai/SKILLS_CATALOG.md",
            "# Catalog\n\n[test skill](../../.codex/skills/test-skill/SKILL.md)\n",
        )
        self.write(
            root / "docs/ai/registry/documents.toml",
            """schema_version = 1

[[document]]
id = "root-agents"
path = "AGENTS.md"
kind = "instruction"
authority = "primary"
code_paths = ["src"]

[[document]]
id = "guide"
path = "docs/guide.md"
kind = "policy"
authority = "primary"
code_paths = ["src"]

[[document]]
id = "skills-catalog"
path = "docs/ai/SKILLS_CATALOG.md"
kind = "index"
authority = "primary"
code_paths = [".codex/skills"]

[[document]]
id = "knowledge-index"
path = "docs/knowledge/INDEX.md"
kind = "index"
authority = "generated"
code_paths = ["scripts/ai"]

[[local_document]]
id = "local-notes"
path = "docs/local-notes.md"
purpose = "개인 메모"
""",
        )
        self.write(
            root / "docs/ai/registry/skills.toml",
            """schema_version = 1

[[skill]]
id = "test-skill"
path = ".codex/skills/test-skill/SKILL.md"
required_documents = ["guide"]
""",
        )
        self.write(
            root / "docs/ai/registry/agents.toml",
            """schema_version = 1

[[agent]]
id = "test-agent"
path = ".codex/agents/test-agent.toml"
required_documents = ["guide"]
""",
        )
        self.write(root / "docs/ai/registry/policies.toml", "schema_version = 1\n")
        self.write(
            root / ".codex/skills/test-skill/SKILL.md",
            """---
name: test-skill
description: Test skill.
---

# Test skill
""",
        )
        self.write(
            root / ".codex/agents/test-agent.toml",
            '''name = "test-agent"
description = "Test agent."
developer_instructions = """
Review only.
"""
''',
        )
        self.write(root / "docs/knowledge/INDEX.md", render(root))
        subprocess.run(["git", "init", "-q"], cwd=root, check=True)
        subprocess.run(["git", "add", "."], cwd=root, check=True)
        subprocess.run(
            ["git", "-c", "user.name=Verifier", "-c", "user.email=verifier@example.com", "commit", "-qm", "fixture"],
            cwd=root,
            check=True,
        )
        self.write(
            root / "docs/history/records/2026/record.md",
            """---
id: GS-2026-0001
status: completed
base_commit: HEAD
related_documents: [guide]
related_code: [src]
skills_used: [test-skill]
---

# Record

## 요청

## 근거

## 결정

## 변경

## 검증

## 지시 이행

## 스킬 평가

## 개선 후보
""",
        )
        return root

    @staticmethod
    def write(path: Path, content: str) -> None:
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content)

    def assert_error(self, root: Path, expected: str) -> None:
        self.assertIn(expected, "\n".join(verify(root, print_coverage=False).errors))

    def test_valid_registry_and_missing_local_document_pass(self) -> None:
        root = self.make_root()

        self.assertEqual([], verify(root, print_coverage=False).errors)
        self.assertNotIn("local-notes", render(root))

    def test_local_document_content_is_excluded_from_shared_link_checks(self) -> None:
        root = self.make_root()
        self.write(root / "docs/local-notes.md", "[local broken link](missing.md)\n")

        self.assertEqual([], verify(root, print_coverage=False).errors)

    def test_duplicate_shared_and_local_document_id_fails(self) -> None:
        root = self.make_root()
        documents = root / "docs/ai/registry/documents.toml"
        documents.write_text(documents.read_text().replace('id = "local-notes"', 'id = "guide"'))

        self.assert_error(root, "duplicate document id across shared/local registry: guide")

    def test_unregistered_document_and_broken_link_fail(self) -> None:
        root = self.make_root()
        self.write(root / "docs/unregistered.md", "[missing](missing.md)\n")
        self.write(root / "docs/guide.md", "[missing](missing.md)\n")

        errors = "\n".join(verify(root, print_coverage=False).errors)
        self.assertIn("unregistered project document: docs/unregistered.md", errors)
        self.assertIn("broken Markdown link: docs/guide.md -> missing.md", errors)

    def test_skill_frontmatter_and_catalog_relationship_fail(self) -> None:
        root = self.make_root()
        self.write(root / ".codex/skills/test-skill/SKILL.md", "# Missing frontmatter\n")
        self.write(root / "docs/ai/SKILLS_CATALOG.md", "# Catalog\n")

        errors = "\n".join(verify(root, print_coverage=False).errors)
        self.assertIn("skill frontmatter missing", errors)
        self.assertIn("registered skill missing from Catalog", errors)

    def test_agent_contract_failure_is_reported(self) -> None:
        root = self.make_root()
        self.write(root / ".codex/agents/test-agent.toml", 'name = "different-agent"\n')

        errors = "\n".join(verify(root, print_coverage=False).errors)
        self.assertIn("agent description missing", errors)
        self.assertIn("agent developer_instructions missing", errors)
        self.assertIn("registered agent name mismatch: test-agent", errors)

    def test_history_references_and_completed_sections_fail(self) -> None:
        root = self.make_root()
        self.write(
            root / "docs/history/records/2026/record.md",
            """---
id: GS-2026-0001
status: completed
base_commit: HEAD
related_documents: [local-notes]
related_code: [missing]
skills_used: [missing-skill]
---

# Record

## 요청
""",
        )

        errors = "\n".join(verify(root, print_coverage=False).errors)
        self.assertIn("history references unknown shared document: GS-2026-0001 -> local-notes", errors)
        self.assertIn("history references unknown skill: GS-2026-0001 -> missing-skill", errors)
        self.assertIn("history references missing code path: GS-2026-0001 -> missing", errors)
        self.assertIn("completed history record lacks ## 근거: GS-2026-0001", errors)

    def test_duplicate_registry_ids_and_missing_code_path_fail(self) -> None:
        root = self.make_root()
        self.write(
            root / "docs/ai/registry/documents.toml",
            (root / "docs/ai/registry/documents.toml")
            .read_text()
            .replace('code_paths = ["src"]', 'code_paths = ["missing"]', 1)
            + """
[[document]]
id = "guide"
path = "docs/guide.md"
kind = "policy"
authority = "primary"
code_paths = ["src"]
""",
        )
        self.write(
            root / "docs/ai/registry/skills.toml",
            (root / "docs/ai/registry/skills.toml").read_text()
            + """
[[skill]]
id = "test-skill"
path = ".codex/skills/test-skill/SKILL.md"
required_documents = ["guide"]
""",
        )
        self.write(
            root / "docs/ai/registry/agents.toml",
            (root / "docs/ai/registry/agents.toml").read_text()
            + """
[[agent]]
id = "test-agent"
path = ".codex/agents/test-agent.toml"
required_documents = ["guide"]
""",
        )

        errors = "\n".join(verify(root, print_coverage=False).errors)
        self.assertIn("duplicate document id: guide", errors)
        self.assertIn("duplicate skill id: test-skill", errors)
        self.assertIn("duplicate agent id: test-agent", errors)
        self.assertIn("registered code path missing: root-agents -> missing", errors)

    def test_skill_agent_and_duplicate_history_references_fail(self) -> None:
        root = self.make_root()
        self.write(
            root / "docs/ai/registry/skills.toml",
            (root / "docs/ai/registry/skills.toml").read_text().replace('["guide"]', '["local-notes"]'),
        )
        self.write(
            root / "docs/ai/registry/agents.toml",
            (root / "docs/ai/registry/agents.toml").read_text().replace('["guide"]', '["local-notes"]'),
        )
        self.write(
            root / "docs/history/records/2026/duplicate.md",
            """---
id: GS-2026-0001
status: in_progress
base_commit: HEAD
related_documents: [guide]
related_code: [src]
skills_used: [test-skill]
---

# Duplicate
""",
        )

        errors = "\n".join(verify(root, print_coverage=False).errors)
        self.assertIn("skill references unknown shared document: test-skill -> local-notes", errors)
        self.assertIn("agent references unknown shared document: test-agent -> local-notes", errors)
        self.assertIn("duplicate history record id: GS-2026-0001", errors)

    def test_hook_skips_unrelated_changes_and_checks_relevant_changes(self) -> None:
        root = self.make_root()
        self.write(root / "docs/knowledge/INDEX.md", render(root))
        hook = root / "scripts/git-hooks/run-staged-knowledge-check.sh"
        hook.parent.mkdir(parents=True)
        copy2(PROJECT_ROOT / "scripts/git-hooks/run-staged-knowledge-check.sh", hook)
        for name in ("registry.py", "generate_knowledge_index.py", "verify_knowledge_graph.py"):
            target = root / "scripts/ai" / name
            target.parent.mkdir(parents=True, exist_ok=True)
            copy2(PROJECT_ROOT / "scripts/ai" / name, target)
        self.write(root / "README.txt", "unrelated\n")
        subprocess.run(["git", "add", "README.txt"], cwd=root, check=True)

        unrelated = subprocess.run(["sh", str(hook)], cwd=root, text=True, capture_output=True, check=False)
        self.assertEqual(0, unrelated.returncode, unrelated.stderr)
        self.assertEqual("", unrelated.stdout)

        self.write(root / "docs/guide.md", "# Updated shared guide\n")
        subprocess.run(["git", "add", "docs/guide.md"], cwd=root, check=True)
        relevant = subprocess.run(["sh", str(hook)], cwd=root, text=True, capture_output=True, check=False)
        self.assertEqual(0, relevant.returncode, f"{relevant.stderr}\n{relevant.stdout}")
        self.assertIn("staged knowledge-operation changes detected", relevant.stdout)

        registry = root / "docs/ai/registry/documents.toml"
        registry.write_text(registry.read_text().replace('authority = "primary"', 'authority = "auxiliary"', 1))
        subprocess.run(["git", "add", "docs/ai/registry/documents.toml"], cwd=root, check=True)
        stale_index = subprocess.run(["sh", str(hook)], cwd=root, text=True, capture_output=True, check=False)
        self.assertNotEqual(0, stale_index.returncode)
        self.assertIn("Knowledge index is stale.", stale_index.stdout)
