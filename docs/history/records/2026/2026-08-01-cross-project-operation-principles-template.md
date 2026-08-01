---
id: GS-2026-0012
status: completed
base_commit: b6bba07
related_documents: [ai-readme, knowledge-operations, codex-operation-hardening-plan, cross-project-operation-principles-template, history-readme]
related_code: [docs/ai, .codex, .githooks, .github]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# Cross-project Codex 운영 원칙 초안

## 요청

- 현재 운영 보강 작업에서 다른 repository에도 재사용할 수 있는 공통 원칙을 docs에 기록하고, 모든 phase 완료 후 다시 다듬을 수 있게 한다.

## 근거

- Phase 0~2에서 정책·agent read-only·CI 최소 권한과 secret-free build 원칙을 GithubSearch 적용본으로 구현했다.
- branch, Gradle task, OAuth 입력, deployment은 repository별로 달라 그대로 복사하면 안 된다.

## 결정

- 공통 원칙과 project-specific 조정 항목을 분리한 provisional template를 만든다.
- Phase 3~5의 worktree, review, 운영 결과를 반영하기 전에는 shared bootstrap 또는 global skill로 승격하지 않는다.

## 변경

- `docs/ai/cross-project-operation-principles-template.md`에 최소 권한, secret-free PR 검증, reviewer/write 분리, worktree, 보고, phase 운영 원칙을 기록했다.
- AI docs 목차와 document registry에 템플릿을 등록했다.

## 검증

- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error, 0 warning.
- `git diff --check`: 통과.
- 문서-only 변경이므로 Gradle 검증은 실행하지 않았다.

## 지시 이행

- fulfilled: 공통 원칙을 provisional template로 기록했고, 후속 phase 완료 뒤 재검토할 항목을 명시했다.

## 스킬 평가

- `project-knowledge-grounding`: 현재 적용본과 registry 경계를 확인해 공통·project-specific 내용을 분리하는 데 충분했다.
- `session-retrospective`: 문서 관계와 작업 이력 검증을 종료 기준으로 적용한다.

## 개선 후보

- Phase 3~5 완료 후 실제 원격 CI·worktree·review 결과를 반영해 이 템플릿의 항목을 승격, 완화, 제거한다.
