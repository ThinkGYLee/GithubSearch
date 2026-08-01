---
id: GS-2026-0007
status: completed
base_commit: bcc9e0f
related_documents: [knowledge-operations, subagents, navigation-contracts, task-scope, quality-gates, history-readme]
related_code: [.codex/agents, docs/ai/registry, docs/ai, app, feature/detail]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# Custom agent 운영 전환과 navigation 계약 정립

## 요청

- 기존 4개 custom agent를 초안이 아닌 실제 운영 상태로 전환하고, `navigation-contract-reviewer`를 추가한다.
- 모든 agent가 결론 전에 관련 정본 문서와 영향 코드·테스트를 읽고 두 근거 경로를 결과에 남기도록 통일한다.
- 현재 구현을 근거로 navigation 계약 문서를 만들되, SmartTimer의 타이머·알림·진동 역할은 이식하지 않는다.

## 근거

- `app/src/main/java/com/gyleedev/githubsearch/ui/GithubSearchScreen.kt`의 `NavHost`는 `HOME`을 start destination으로 두고, Detail route를 `DETAIL/{id}?from={from}`으로 선언한다.
- 같은 파일에서 Home 일반 목록·검색 결과·Favorite가 각각 `home`·`search`·`favorite` 값을 전달하고, Detail은 `navigateUp()`으로 뒤로 간다.
- 하단 navigation은 Detail에서 숨기며, 탭 전환에 `saveState`, `launchSingleTop`, `restoreState`를 사용한다.
- `feature/detail/src/main/java/com/gyleedev/githubsearch/feature/detail/DetailViewModel.kt`와 해당 test는 `SavedStateHandle`의 `id` 수신·처리를 보여 준다. 현재 navigation 선언에서 deep link와 route 조립·back stack을 직접 검증하는 test는 찾지 못했다.
- SmartTimer의 navigation agent와 registry는 review-first, 근거 경로 반환, scope 등록이라는 운영 방향의 참고로만 사용했다. timer lifecycle 역할과 AlarmManager·notification·vibration 관련 문서는 이식하지 않았다.

## 결정

- 기존 4개 agent와 새 navigation agent를 실제 운영 상태로 정의하되, 기본 권한은 review-first·read-only로 고정한다.
- agent의 파일 수정은 주 작업자가 명시한 제한된 write scope에서만 허용한다. 최종 적용, 위험 판단, 검증, 보고 책임은 주 작업자에게 남긴다.
- 모든 agent는 관련 primary document와 현재 영향 코드 또는 테스트를 읽고 두 근거 경로를 결과에 남긴다.
- navigation 계약은 현재 route 문자열, argument, 호출자, back stack, 탭 상태 보존, 하단 바 표시 조건만 기록한다. type-safe navigation 및 deep link 도입은 구현하지 않는다.

## 변경

- 기존 4개 `.codex/agents/*.toml`에 공통 근거 경로 반환 규칙을 추가하고, 기존 review-first·제한된 write·Git·secret·위험 변경 guardrail을 유지했다.
- `.codex/agents/navigation-contract-reviewer.toml`을 추가했다. navigation route, argument, caller/destination compatibility, deep link, back stack, 상태 복원과 behavior test 필요성만 검토한다.
- `docs/ai/registry/agents.toml`의 모든 agent에 사람이 읽는 `scope`와 `knowledge-operations` 필수 문서를 추가하고, navigation agent를 등록했다.
- `docs/ai/navigation-contracts.md`와 registry document를 추가하고, `subagents.md`, `README.md`, generated knowledge Index를 실제 운영 상태로 갱신했다.

## 검증

- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과. shared 34개·local 3개 문서, agent 5개를 확인했다.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error, 0 warning 통과.
- `python3 -m unittest discover -s scripts/ai/tests -p 'test_*.py'`: 11 tests 통과.
- `git diff --check`: 통과.
- 문서·custom agent 설정·generated Index만 변경했으며 Android 코드, Gradle, navigation 동작을 변경하지 않아 Gradle 검증은 실행하지 않았다.

## 지시 이행

- 충족: 기존 4개 역할을 초안 표현에서 실제 운영 문서·registry 구성으로 전환했다.
- 충족: 모든 agent에 문서와 현재 코드/테스트 근거 경로 반환 규칙을 적용했다.
- 충족: navigation 계약 문서는 현재 구현 근거만 기록했고, deep link·route·Gradle 동작은 바꾸지 않았다.
- 충족: SmartTimer의 timer lifecycle, AlarmManager, notification, vibration 관련 역할과 문서는 가져오지 않았다.
- 충족: 사용자 untracked `docs/multi-module-migration-template.md`는 변경·stage·commit하지 않았다.
- 충족: 이번 작업에서는 commit·push를 수행하지 않았다.

## 스킬 평가

- `project-knowledge-grounding`: AGENTS, knowledge operations, 범위·품질 규칙, agent registry, 현재 navigation 코드·test를 함께 읽어 문서 정본의 근거를 제한하는 데 사용했다.
- `session-retrospective`: history 완료 기록, generated Index, graph·회귀 검사, diff, 사용자 local 파일 보존을 종료 전에 점검하는 데 사용했다.

## 개선 후보

- navigation 동작을 바꾸는 후속 작업에서는 route 조립, `from` 값, `navigateUp`, 탭 상태 복원을 직접 검증하는 behavior test의 도입 여부를 `test-strategy-reviewer`와 함께 판단한다.
