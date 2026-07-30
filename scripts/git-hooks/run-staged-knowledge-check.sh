#!/usr/bin/env sh
# Check generated knowledge artifacts without modifying the working tree or index.
set -eu

repo_root=$(git rev-parse --show-toplevel)
cd "$repo_root"

if ! git diff --cached --name-only --diff-filter=ACMR | grep -Eq \
    '^(AGENTS\.md|docs/.*\.md|docs/ai/registry/.*\.toml|\.codex/skills/|\.codex/agents/|scripts/ai/)'; then
    exit 0
fi

printf '%s\n' 'GithubSearch: staged knowledge-operation changes detected; verifying generated Index and graph.'
python3 scripts/ai/generate_knowledge_index.py --check
python3 scripts/ai/verify_knowledge_graph.py
