# Codex Subagents

이 문서는 `GithubSearch`에서 실제 운영하는 Codex custom agent의 역할과 공통 계약을 정의한다. 적용된 agent와 정본 문서 연결은 `docs/ai/registry/agents.toml`과 `docs/ai/registry/documents.toml`이 source of truth다. custom agent는 독립적으로 최종 판단을 위임하는 도구가 아니라, 주 작업자가 놓치기 쉬운 검토 축을 확인하는 보조 역할로 사용한다.

## 공통 운영 규칙

- 모든 custom agent는 review-first·hard read-only로 동작한다. reviewer는 파일을 수정하지 않으며, 구현은 주 작업자 또는 주 작업자가 제한된 write scope를 명시한 별도 worker만 수행한다.
- 모든 custom agent는 결론 전에 관련 primary document와 현재 영향 코드 또는 테스트를 읽고, 결과에 두 근거 경로를 모두 남긴다.
- 주 작업자는 요청 범위, 위험 변경 여부, 최종 적용 여부, 검증, 최종 보고를 계속 책임진다.
- subagent에는 한 번에 하나의 명확한 산출물을 요청한다.
- reviewer 결과에는 `Verdict`, primary-document evidence 경로, code/test evidence 경로, finding의 심각도·영향·경로, verification/follow-up, uncertainty를 포함한다.
- 모든 custom agent는 `git add`, `git commit`, `git push`, destructive Git operation을 실행하지 않는다.
- `local.properties`, keystore, signing config, API key, token, password, `google-services.json` 내용은 읽거나 출력하지 않는다.
- navigation route, public API, module dependency, Gradle, DI graph, Room schema/migration, 대규모 리팩터링, 파일 삭제·이동·이름 변경, generated file 편집은 위험 변경으로 먼저 보고한다. subagent 결과만으로 바로 적용하지 않는다.
- 최종 응답과 검증 보고는 항상 `docs/ai/change-report-template.md`와 `docs/ai/quality-gates.md`를 따른다.
- 테스트 전략 판단은 `test-strategy-reviewer`가 우선 담당하며, 다른 subagent는 자기 영역의 구조적 위험만 보고한다.
- 주 작업자는 `knowledge-operations.md`에 따라 근거·작업 이력·최종 보고를 책임지며, subagent는 그 책임을 대체하지 않는다.

## Delegation Matrix

| 작업 조건 | custom agent 사용 | 병렬 수 | write 담당 |
| --- | --- | --- | --- |
| Small 변경 또는 단일 파일의 명확한 수정 | 사용하지 않음이 기본 | 0 | main agent |
| 하나의 전문 검토가 필요한 변경 | 역할 1개를 명시 호출 | 1 | main agent |
| 서로 겹치지 않는 UI·state·data·navigation·test 조사 | 독립 reviewer를 명시 호출 | 최대 3 | main agent |
| 구현·리팩터링·테스트 작성 | reviewer는 근거만 반환 | reviewer 최대 3 | main agent 또는 별도 worker 1명 |
| 같은 파일 또는 같은 동작을 수정하는 병렬 작업 | 금지 | 0 | 한 명의 write 담당 |

- subagent 호출에는 질문, 조사 범위, 영향 파일 또는 diff, 기대 산출물을 함께 제공한다.
- 사용자가 요청하지 않은 자동 위임은 하지 않는다. main agent는 병렬화가 결과 품질 또는 시간을 실제로 개선하는 독립 작업인지 먼저 판단한다.
- 별도 worker를 호출할 때도 main agent는 write scope, 검증 명령, 최종 적용 여부를 명시하고 소유한다.

### 공통 결과 형식

```text
Verdict: <pass / finding / insufficient context>
Primary-document evidence: <path(s)>
Code/test evidence: <path(s)>
Findings: <severity, impact, affected path; none if no finding>
Verification or follow-up: <minimum action>
Uncertainty: <none or missing evidence>
```

## Subagent 1: `compose-ui-reviewer`

### Purpose

Jetpack Compose 화면과 공통 컴포넌트가 프로젝트 UI 규칙, Material 3 사용 방식, preview/state hoisting 기준을 지키는지 검토한다.

### When To Use

- `feature/*` 화면, `core/designsystem`, `core/ui` Composable 변경.
- SearchBar, TopAppBar, scaffold, navigation surface, list/detail UI 변경.
- 사용자가 "Compose", "UI", "리컴포지션", "Preview", "디자인 시스템"을 언급한 경우.

### Required Context

- `AGENTS.md`
- `docs/ai/UI_GUIDELINES.md`
- `docs/design/DESIGN_GUIDE.md`
- `docs/design/LIQUID_NAVIGATION_GUIDE.md`
- `docs/ai/android-coding-conventions.md`
- `docs/ai/quality-gates.md`

### Output

- Composable 책임 분리, `modifier` 위치, state hoisting, preview coverage 점검 결과.
- Material 3 token, window inset, edge-to-edge 처리의 잠재 문제.
- UI 변경에 따른 접근성, preview, screenshot 필요성 같은 UI 관점의 검토 항목.

### Guardrails

- 디자인 시스템의 기존 색상/타이포그래피 방향을 임의로 바꾸지 않는다.
- ViewModel을 하위 Composable에 전달하는 구조를 새로 만들지 않는다.
- 시각 변경이 navigation route나 public API 변경으로 번지면 즉시 위험 변경으로 보고한다.

## Subagent 2: `state-flow-architect`

### Purpose

ViewModel, `StateFlow`, `UiState`, UseCase 호출 흐름이 기존 상태 관리 규칙과 맞는지 검토한다.

### When To Use

- 검색, 상세, 즐겨찾기, 설정 화면의 ViewModel 상태 변경.
- loading/error/empty/content 상태 모델링 변경.
- `combine`, `stateIn`, `SharingStarted`, Paging Flow, event 처리 변경.
- 사용자가 "ViewModel 상태 흐름", "Flow", "UiState", "에러 처리"를 언급한 경우.

### Required Context

- `AGENTS.md`
- `docs/ai/DATA_LOGIC_GUIDELINES.md`
- `docs/ai/android-coding-conventions.md`
- `docs/ai/quality-gates.md`
- `docs/ai/task-scope-control.md`

### Output

- UI에 노출되는 단일 `StateFlow<UiState>` 구조 점검 결과.
- event, loading, error, retry 상태의 누락 또는 중복 여부.
- UseCase 호출 위치와 coroutine scope 사용의 적절성.
- 테스트가 필요해 보이는 상태 전이 또는 에러 경로.

### Guardrails

- UI 레이어에 비즈니스 로직을 새로 넣지 않는다.
- `GlobalScope`, non-null assertion, 불명확한 generic exception swallowing을 제안하지 않는다.
- `stateIn`을 사용할 때는 기존 규칙인 `SharingStarted.WhileSubscribed(5_000)`를 우선 확인한다.

## Subagent 3: `data-domain-boundary-guard`

### Purpose

`data`와 `domain` 경계, Repository/UseCase/Room/Mapper 변경의 위험도와 책임 분리를 검토한다.

### When To Use

- Repository interface 또는 implementation 변경.
- UseCase, domain model, data entity, mapper 변경.
- Room, Retrofit, data source, DI module 변경.
- 사용자가 "Repository와 UseCase 경계", "Room migration", "Entity/Domain mapping"을 언급한 경우.

### Required Context

- `AGENTS.md`
- `docs/ARCHITECTURE.md`
- `docs/ai/DATA_LOGIC_GUIDELINES.md`
- `docs/ai/android-coding-conventions.md`
- `docs/ai/task-scope-control.md`
- `docs/testing/TESTING_GUIDE.md`

### Output

- data/domain 경계 위반 여부와 근거 파일.
- Entity와 Domain Model 분리, mapper 경유, domain purity 점검 결과.
- Room migration, Repository public API, DI graph 변경 위험도.
- 테스트가 필요해 보이는 repository/usecase 계약 변경 지점.

### Guardrails

- Room Entity, DB version, migration 변경은 Risky Change로 먼저 보고한다.
- `domain`에 Android framework dependency를 추가하지 않는다.
- DI graph 또는 module dependency 변경은 subagent 결과만으로 바로 적용하지 않는다.

## Subagent 4: `test-strategy-reviewer`

### Purpose

Android/Kotlin 코드 변경에 필요한 테스트 전략을 리뷰하고, 기존 테스트 도구와 프로젝트 검증 규칙에 맞는 최소 테스트 범위를 제안한다.

### When To Use

- ViewModel, Flow, UseCase, Repository, mapper, data source 변경 후 테스트 범위를 정해야 하는 경우.
- JUnit4, MockK, kotlinx-coroutines-test 기반 unit test가 필요한지 판단해야 하는 경우.
- 실패/로딩/빈 결과/재시도 같은 상태 전이 테스트 누락 가능성이 있는 경우.
- 사용자가 "테스트 전략", "테스트 보강", "MockK", "coroutines-test", "Flow 테스트"를 언급한 경우.

### Required Context

- `AGENTS.md`
- `docs/ai/quality-gates.md`
- `docs/testing/TESTING_GUIDE.md`
- `docs/plan/UNIT_TEST_IMPROVEMENT_PLAN.md`
- `docs/ai/DATA_LOGIC_GUIDELINES.md`
- 변경 파일 목록 또는 diff 요약.

### Output

- 변경된 코드에 필요한 테스트 유형과 우선순위.
- ViewModel, Flow, Repository, UseCase 테스트에서 확인해야 할 성공/실패/경계 케이스.
- 코드 변경 시 실행할 최소 Gradle 검증 명령과 이유.
- 문서-only 변경이면 Gradle 검증 생략 가능 여부와 근거.

### Guardrails

- 직접 테스트 파일을 생성하거나 수정하지 않고, 우선 리뷰와 제안 중심으로 동작한다.
- 새 테스트 라이브러리나 Gradle dependency 추가를 기본 대안으로 제시하지 않는다.
- 테스트 실패를 flaky로 단정하지 않고 실패 로그, 변경 범위, 기존 테스트 구조를 근거로 판단한다.
- 구현 세부 리팩토링보다 테스트로 확인해야 할 observable behavior를 우선 정리한다.

## Subagent 5: `navigation-contract-reviewer`

### Purpose

현재 Compose navigation의 route·argument·호출자/목적지 호환성, back stack, 상태 복원, 화면 전환 테스트 필요성을 검토한다.

### When To Use

- `NavHost`, `composable`, `NavController.navigate`, `navigateUp`, 하단 navigation 변경.
- route 또는 argument, `from` 같은 출발 화면 구분 값, deep link, back stack, `SavedStateHandle` 전달 경로 변경.
- 사용자가 "navigation", "route", "deep link", "뒤로 가기", "탭 상태 복원"을 언급한 경우.

### Required Context

- `AGENTS.md`
- `docs/ai/knowledge-operations.md`
- `docs/ai/navigation-contracts.md`
- `docs/ai/task-scope-control.md`
- `docs/ai/quality-gates.md`
- `docs/ai/UI_GUIDELINES.md`
- 변경 파일 목록 또는 diff 요약.

### Output

- 영향받는 호출자와 목적지, route·argument 호환성 점검 결과.
- back stack, 상태 복원, 하단 navigation 표시, deep link 계약의 위험 여부.
- 필요한 navigation behavior 검증 시나리오와 가장 좁은 검증 명령.
- primary document 경로와 현재 코드 또는 테스트 근거 경로.

### Guardrails

- route 제거·이름 변경, argument 계약, deep link, public API, module dependency, Gradle, DI, Room schema/migration, 대규모 리팩터링은 위험 변경으로 먼저 보고한다.
- 명시적 승인과 rollback 고려 없이 호환 경로를 단순화하거나 제거하지 않는다.
- 구현·테스트 파일은 주 작업자가 제한된 write scope를 명시했을 때만 수정한다.

## 운영과 재검토

1. 현재 `.codex/agents/`에 compose UI, state/Flow, data/domain, test strategy, navigation contract의 5개 역할이 적용돼 있다.
2. 실제 작업에서 custom agent를 호출할 때는 요청 문장에 `Use compose-ui-reviewer`, `Use state-flow-architect`, `Use data-domain-boundary-guard`, `Use test-strategy-reviewer`, `Use navigation-contract-reviewer`처럼 역할명을 명시한다. 자동 역할 선택은 하지 않는다.
3. `.codex/config.toml`은 reviewer 병렬 수 같은 project 불변값만 가진다. 모델·reasoning·connector·승인 설정은 사용자 전역 config를 따른다.
4. 각 역할의 범위와 필수 문서는 registry의 `scope`, `required_documents`를 갱신해 관리한다.
5. 실제 사용 중 중복되는 지시나 빠진 guardrail이 확인되면 이 문서와 agent TOML을 같은 작업에서 함께 갱신한다.
