---
id: GS-2026-0014
status: completed
base_commit: 918653b
related_documents: [root-agents, ai-readme, workflows, subagents, knowledge-operations, codex-operation-hardening-plan, code-review, history-readme]
related_code: [.codex/agents, .githooks, .github, docs/ai]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# Codex 운영 보강 Phase 4 공통 Code Review 기준과 lifecycle hook 경계

## 요청

- Phase 4로 진행해 custom reviewer, Codex `/review`, 사람 PR review가 공통 결과 기준을 사용하도록 하고 hook 책임 경계를 문서화한다.

## 근거

- 현재 custom reviewer는 Verdict·문서/코드 근거·finding·follow-up 형식을 사용하지만 P0~P2 severity와 review scope를 공통 정본으로 정의하지 않았다.
- Git pre-commit hook과 Knowledge CI가 formatter·Index·graph의 기계적 검증을 이미 담당한다.
- 공식 Codex hook은 세션·도구 사용 주기 확장 수단이며 project-local hook은 신뢰 검토가 필요하다.

## 결정

- review scope, P0~P2, evidence, false-positive, test-gap, common output을 별도 primary policy로 정의한다.
- 같은 검증을 lifecycle hook으로 중복하지 않고, 실제 반복 누락 사례가 확인될 때만 별도 승인 작업으로 hook을 도입한다.

## 변경

- `docs/ai/code-review.md`에 review scope, P0~P2, evidence, false-positive, test-gap, common output, hook 경계를 작성했다.
- `AGENTS.md`, Review Workflow, subagent 문서와 다섯 reviewer TOML에 공통 review scope·P0~P2 결과 형식을 연결했다.
- lifecycle hook 설정이나 실행 스크립트는 추가하지 않았다.

## 검증

- `compose-ui-reviewer`가 Liquid navigation 후보를 read-only로 검토해 Verdict·scope·문서/코드 근거·P2 finding·후속 조치·uncertainty를 반환했다.
- reviewer 실행 뒤 main agent가 의도한 policy·agent 문서 외 reviewer가 만든 파일 변경이 없음을 확인했다.
- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error, 0 warning.
- `git diff --check`: 통과.
- `.codex/hooks.json`은 존재하지 않아 lifecycle hook 설정을 추가하지 않았음을 확인했다.
- 현재 `python3`은 3.9.6으로 표준 `tomllib`가 없어 agent TOML의 별도 parser 검증은 실행하지 못했다. 의존성을 추가하지 않고 graph 검증과 변경 diff 검토로 확인했다.
- 문서·agent 지시 변경이므로 Gradle 검증은 실행하지 않는다.

## 지시 이행

- fulfilled: custom reviewer, `/review`, 사람 review의 공통 severity·evidence·test-gap 기준을 문서화했다.
- fulfilled: existing Git hook·CI와 같은 검사를 lifecycle hook으로 중복하지 않았고, 반복 누락 근거가 없어 hook을 추가하지 않았다.

## 스킬 평가

- `project-knowledge-grounding`: 기존 reviewer 형식, Git hook·CI 책임, history 근거를 함께 확인하는 데 사용했다.
- `session-retrospective`: reviewer 결과·working tree·registry 관계를 완료 기준으로 적용한다.
- `openai-docs`: 최신 Codex lifecycle hook의 신뢰 검토와 이벤트 경계를 확인하는 데 사용했다.

## 개선 후보

- Phase 5에서 실제 PR review 3~5건을 회고해 P0~P2 일관성, false-positive, test-gap 품질과 lifecycle hook 필요성을 다시 판단한다.
