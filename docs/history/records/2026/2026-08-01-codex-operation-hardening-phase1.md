---
id: GS-2026-0010
status: completed
base_commit: 5a9f45d
related_documents: [knowledge-operations, task-scope, quality-gates, subagents, codex-operation-hardening-plan, history-readme]
related_code: [.codex/config.toml, .codex/agents, docs/ai, AGENTS.md]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# Codex 운영 보강 Phase 1 reviewer 권한과 위임 정책

## 요청

- Phase 0 커밋 후 reviewer를 hard read-only로 고정하고, bounded subagent 위임 정책을 적용한다.

## 근거

- Phase 0 명세는 reviewer를 hard read-only로 고정하고, 구현은 main agent 또는 별도 worker만 담당하도록 결정했다.
- 현재 5개 reviewer TOML은 review-first·근거 경로·Git·secret·위험 변경 guardrail을 이미 가졌지만 `sandbox_mode`를 명시하지 않았다.
- 공식 Codex custom agent 설정은 agent TOML의 `sandbox_mode`와 project config의 `[agents]` 동시 실행 수를 지원한다. project에는 모델·reasoning·connector 같은 개인 설정을 고정하지 않는다.

## 결정

- 5개 reviewer를 `sandbox_mode = "read-only"`로 고정한다. parent의 제한된 write 위임 예외는 reviewer에서 제거하고, 구현은 main agent 또는 별도 worker로 분리한다.
- project config에는 `max_concurrent_threads_per_session = 3`만 설정한다. 모델·reasoning·connector·승인 설정은 사용자 전역 config에 남긴다.
- Small 변경은 main agent 단독으로 처리하고, 독립된 read-only 조사만 최대 3개 병렬화한다. 같은 파일·동작의 병렬 write는 금지한다.

## 변경

- `.codex/config.toml`을 추가해 reviewer 최대 병렬 수를 3으로 제한했다.
- 5개 reviewer TOML에 hard read-only sandbox와 공통 evidence/finding 결과 형식을 추가했다.
- `AGENTS.md`, `subagents.md`에 main agent/worker/reviewer 책임, 명시적 위임, delegation matrix, 공통 결과 형식을 반영했다.
- 운영 보강 명세의 Phase 1을 applied 상태로 갱신했다.

## 검증

- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과. shared 35개·local 3개 문서를 확인했다.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error, 0 warning 통과.
- `python3 -m unittest discover -s scripts/ai/tests -p 'test_*.py'`: 11 tests 통과.
- `rg -n '^sandbox_mode = "read-only"$' .codex/agents/*.toml`: 5개 reviewer 설정을 확인했다.
- `rg -n '^max_concurrent_threads_per_session = 3$' .codex/config.toml`: project 병렬 수 설정을 확인했다.
- `git diff --check`: 통과.
- `compose-ui-reviewer`를 명시 호출해 `Verdict`, 문서·코드 evidence 경로, findings, follow-up, uncertainty를 반환하는 것을 확인했다. reviewer 실행 전후 working tree에 reviewer의 추가 파일 변경이 없었다.
- Android code·Gradle·CI runtime은 바꾸지 않아 Gradle 검증은 실행하지 않았다.

## 지시 이행

- 충족: Phase 0 문서를 커밋한 뒤 별도 Phase 1 작업으로 reviewer 권한·위임 정책을 적용했다.
- 충족: reviewer read-only sandbox, 최대 병렬 수, main agent/worker 책임, evidence 중심 결과 형식을 분리했다.
- 충족: `compose-ui-reviewer` runtime 호출이 파일 수정 없이 공통 결과 형식과 근거 경로를 반환하는 것을 확인했다.
- 충족: 사용자 untracked `docs/multi-module-migration-template.md`를 변경·stage·commit하지 않았다.

## 스킬 평가

- `project-knowledge-grounding`: Phase 0 결정과 현재 TOML·운영 문서의 차이를 확인해 최소 설정 변경으로 한정했다.
- `session-retrospective`: generated Index, graph·회귀 검사, 설정 line 확인과 runtime 검증의 남은 조건을 종료 전에 구분했다.

## 개선 후보

- runtime reviewer는 `SettingScreen` 하단 overlay 여백, Liquid navigation의 `selectableGroup`, Light/Dark Preview·UI regression fixture를 개선 후보로 보고했다. 이는 Phase 1 설정과 분리된 UI 변경이므로 별도 작업에서 검토한다.
