---
id: GS-2026-0001
status: completed
base_commit: f8385fc
related_documents: [root-agents, ai-readme, workflows, quality-gates, change-report, knowledge-operations, reporting-system-migration-plan, history-readme, skills-catalog]
related_code: [AGENTS.md, docs/ai, docs/history, .codex/agents]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# AI 보고·지식 운영 체계 이식

## 요청

- SmartTimer의 AI 보고·지식 운영 체계를 GithubSearch에 맞게 이식한다.
- 이식 명세의 Phase 1~5를 순서대로 완료하고 각 exit gate로 검증한다.

## 근거

- 기존 Change Report는 변경·검증·리스크를 보고하지만, 근거·지시 이행·skill 평가와 영속 작업 이력은 남기지 않는다.
- GithubSearch에는 4개의 review-first custom agent와 기존 AI 문서가 있으며, 사용자 untracked migration template은 보존해야 한다.

## 결정

- Small 작업의 보고 부담을 유지하기 위해 history record는 Medium·Large·Risky 및 policy·skill·agent 변경에 필수로 제한한다.
- SmartTimer 도메인 전용 정책·agent와 system skill 전체는 이식하지 않는다.
- 앱 코드, Gradle, CI workflow, Git hook은 변경하지 않는다.

## 변경

- Change Report에 Evidence & Retrospective를 추가하고 커밋 Subject를 한국어 변경 요약으로 정렬했다.
- 작업 이력, knowledge operations, registry, generated knowledge index, graph verifier, project skill catalog를 추가했다.
- 기존 4개 custom agent는 수정하지 않고 registry와 운영 문서에 연결했다.
- `GEMINI.md`는 legacy adapter로 보존하고 새 정본 링크만 추가했다.

## 검증

- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error로 통과.
- `git diff --check`: 통과.
- `git status --short`: 사용자 untracked migration template은 유지했고 이식 관련 파일만 추가·수정된 것을 확인했다.

## 지시 이행

- 충족: 명세의 Phase 1~5를 순서대로 완료하고 각 phase의 조건을 문서와 verifier로 확인했다.
- 충족: 앱 코드, Gradle, CI workflow, Git hook과 기존 custom agent 파일은 변경하지 않았다.
- 충족: 기존 untracked migration template은 내용 변경·stage·commit 없이 registry에만 등록했다.

## 스킬 평가

- `android-docs-sync`: 기존 문서 보존, legacy adapter 유지, phase별 이식 계획을 판단하는 데 사용했다.
- `project-knowledge-grounding`: 문서·코드 최소 근거와 작업 이력 시작 규칙을 제공하도록 추가했다.
- `session-retrospective`: 종료 검증, 지시 이행, 개선 후보 기록 규칙을 제공하도록 추가했다.

## 개선 후보

- update: 다음 Medium/Risky 작업에서 history record를 실제로 한 번 사용해 registry code path와 보고 섹션의 과도함을 조정한다.
