---
id: GS-2026-0008
status: completed
base_commit: 2147a3e
related_documents: [knowledge-operations, task-scope, quality-gates, subagents, codex-operation-hardening-plan, history-readme]
related_code: [.codex/agents, .githooks, .github/workflows, build-logic, app, data]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# Codex 운영 정책 보강 명세

## 요청

- GithubSearch의 현재 Codex 운영 정책을 공식 Codex 가이드와 비교하고, 부족한 부분을 단계별 실행 명세와 체크리스트로 문서화한다.

## 근거

- 최신 공식 Codex manual에서 `AGENTS.md`, custom agent·subagent, hook, worktree, configuration의 운영 원칙을 확인했다. 전역 `openai-docs` system skill은 project skill registry 대상이 아니므로 frontmatter의 `skills_used`에는 기록하지 않고 이 본문에만 사용 근거를 남긴다.
- `AGENTS.md`, `knowledge-operations.md`, `task-scope-control.md`, `quality-gates.md`, agent registry와 현재 5개 agent TOML을 함께 읽었다.
- `.github/workflows/build.yml`은 build·coverage comment·Pages deploy를 하나의 job에서 실행하며 `contents: write`, `pull-requests: write`, `pages: write`, `id-token: write`를 모두 부여한다. 반면 `knowledge.yml`은 `contents: read`만 사용한다.
- `build-logic`의 `getApiKey`는 local properties, Gradle property, 환경 변수 순으로 값을 찾고, app·data BuildConfig가 OAuth 값을 요구한다. 따라서 CI secret 제거는 non-secret placeholder build 검증과 함께 계획해야 한다.
- `local.properties`는 존재하지만 Git ignore 대상이며 `.worktreeinclude`는 없다. 파일 내용은 읽거나 기록하지 않았다.

## 결정

- 구현 변경 없이 policy·phase·승인 지점을 먼저 고정한다.
- reviewer는 hard read-only로, 구현은 main agent 또는 명시적인 worker로 분리하는 방향을 권장한다.
- CI 권한·secret·Pages 변경은 Risky Change로 별도 phase와 관리자 승인을 요구한다. `pull_request_target`은 대안으로 사용하지 않는다.
- worktree에는 local credential을 기본 복사하지 않고 Local handoff 또는 별도 보안 승인을 사용한다.

## 변경

- `docs/ai/codex-operation-hardening-plan.md`에 Phase 0~5의 수정 대상, 적용 정책, 체크리스트, exit gate, 위험과 승인 경계를 작성했다.
- AI Docs README와 document registry에 새 primary roadmap을 등록하고 generated knowledge Index를 갱신했다.

## 검증

- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과. shared 35개·local 3개 문서를 확인했다.
- `python3 scripts/ai/verify_knowledge_graph.py`: 완료 record의 전역 skill 참조를 project registry 형식으로 정정한 뒤 재실행한다.
- `python3 -m unittest discover -s scripts/ai/tests -p 'test_*.py'`: 11 tests 통과.
- `git diff --check`: 통과.
- 문서·registry·generated Index만 변경했으므로 Gradle 검증은 실행하지 않았다.

## 지시 이행

- 충족: 공식 Codex guide와 현재 project policy·CI·agent·worktree 조건을 함께 근거로 삼았다.
- 충족: 수정이 필요한 모든 영역을 phase별 변경 대상, 체크리스트, exit gate, 승인 경계로 문서화했다.
- 충족: OAuth secret, `local.properties`, keystore의 내용을 읽거나 문서화하지 않았다.
- 충족: 구현·commit·push·GitHub 설정 변경은 수행하지 않았다.
- 충족: 사용자 untracked `docs/multi-module-migration-template.md`는 변경·stage·commit하지 않았다.

## 스킬 평가

- `project-knowledge-grounding`: 현재 운영 문서와 실제 CI·Gradle 설정을 함께 선택해 정책이 추측이 되지 않도록 했다.
- `session-retrospective`: 작업 이력, generated Index, graph verifier와 registry 참조의 오류를 종료 전에 발견·정정하는 데 사용했다.
- 전역 `openai-docs` system skill: 최신 Codex manual의 공식 원칙을 확인하는 데 사용했다. project skill registry에는 등록 대상이 아니므로 frontmatter 목록에서는 제외했다.

## 개선 후보

- Phase 1을 실제 적용할 때 custom agent read-only sandbox가 parent runtime permission과 충돌하지 않는지, reviewer 1개를 대상으로 수동 검증한다.
- Phase 2의 non-secret OAuth placeholder가 현재 전체 Gradle command를 통과하지 않으면 credential-free BuildConfig 분리를 별도 Risky Change로 승격한다.
