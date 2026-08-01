# Codex Subagents

이 문서는 `GithubSearch`에서 Codex subagent를 단계적으로 도입하기 위한 적용 초안이다. subagent는 독립적으로 판단을 위임하는 도구가 아니라, 주 작업자가 놓치기 쉬운 검토 축을 병렬로 확인하는 보조 역할로 사용한다.

## 공통 운영 규칙

- 주 작업자는 요청 범위, 위험 변경 여부, 최종 적용 여부를 계속 책임진다.
- subagent에는 한 번에 하나의 명확한 산출물을 요청한다.
- subagent가 파일 수정을 맡는 경우 write scope를 명시하고, 다른 subagent와 같은 파일을 동시에 수정하지 않는다.
- `local.properties`, keystore, signing config, API key, token, password, `google-services.json` 내용은 읽거나 출력하지 않는다.
- Room schema, navigation route, public API, Gradle dependency, DI graph 변경은 subagent 결과만으로 바로 적용하지 않고 주 작업자가 별도 위험 변경으로 보고한다.
- 최종 응답과 검증 보고는 항상 `docs/ai/change-report-template.md`와 `docs/ai/quality-gates.md`를 따른다.
- 테스트 전략 판단은 `test-strategy-reviewer`가 우선 담당하며, 다른 subagent는 자기 영역의 구조적 위험만 보고한다.
- 주 작업자는 `knowledge-operations.md`에 따라 근거·작업 이력·최종 보고를 책임지며, subagent는 그 책임을 대체하지 않는다.

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

## Initial Rollout Plan

1. 문서 초안 단계에서는 위 4개 역할만 사용한다.
2. 실제 작업에서 subagent를 호출할 때는 요청 문장에 `Use compose-ui-reviewer`, `Use state-flow-architect`, `Use data-domain-boundary-guard`, `Use test-strategy-reviewer`처럼 역할명을 명시한다.
3. 3~5회 작업 후 중복되는 지시와 빠진 guardrail을 `docs/ai/subagents.md`에 반영한다.
4. 안정화되면 repository 전용 `.codex` 또는 `.agents` 설정 파일로 승격할지 별도 작업으로 판단한다.
