# AGENTS.md

## Source Of Truth

이 파일은 `android-docs-bootstrap` skill 템플릿에서 생성된 프로젝트 적용본이다. 생성 후에는 현재 repository의 실제 구조, 모듈, Gradle task, 팀 규칙에 맞게 수정될 수 있다.

- Template: `android-docs-bootstrap`
- Template Version: `1.0.2`
- Applied To: `GithubSearch`
- Note: 세부 metadata와 sync 정책은 `docs/ai/README.md`와 `docs/ai/template-sync-policy.md`를 따른다.

- 전역 `AGENTS.md`: 개인 공통 작업 스타일만 관리한다.
- `android-docs-bootstrap` skill: 새 Android 프로젝트 세팅용 보일러플레이트 원본을 관리한다.
- 프로젝트 `AGENTS.md`와 `docs/`: 현재 repository에 실제 적용되는 규칙을 관리한다.

## 프로젝트 개요

`GithubSearch`는 GitHub 사용자와 repository 검색, 즐겨찾기, GitHub OAuth 인증, paging 기반 목록 처리를 다루는 Android/Kotlin 멀티 모듈 프로젝트다.

## 저장소 구조

- `app/`: 앱 진입점, `MainActivity`, application 설정, 전역 navigation wiring.
- `feature/home/`: GitHub 사용자 검색 화면과 ViewModel.
- `feature/detail/`: 사용자 상세와 repository 목록 화면.
- `feature/favorite/`: 즐겨찾기 화면.
- `feature/setting/`: 설정 화면.
- `domain/`: UseCase, domain model, repository interface를 담는 Kotlin/JVM module.
- `data/`: Room, Retrofit, repository implementation, data source, mapper, DI module.
- `core/common/`: 공통 Android utility와 DI helper.
- `core/designsystem/`: Compose design system, theme, shared UI component.
- `core/ui/`: UI layer 공통 ViewModel/base/lifecycle utility.
- `core/testing/`: 테스트 fixture, coroutine rule, dummy data.
- `build-logic/`: Gradle convention plugin.
- `gradle/libs.versions.toml`: dependency와 plugin version catalog.
- `docs/`: 프로젝트 문서와 AI 작업 가이드.

## 주요 Android/Kotlin Stack

- Kotlin `2.3.21`, Android Gradle Plugin `9.2.1`, target SDK `36`.
- Gradle Kotlin DSL, Version Catalog, included build 기반 `build-logic`.
- Jetpack Compose, Material 3, Navigation Compose, Lifecycle, ViewModel.
- Hilt, Room, Retrofit, OkHttp, Gson.
- Coroutines, Flow, Paging 3.
- JUnit4, MockK, kotlinx-coroutines-test. Compose screenshot test는 도입 계획만 확정됐으며, 실제 plugin 적용 전까지 현재 stack에 포함하지 않는다.
- Spotless와 ktlint `1.8.0`.
- JaCoCo 통합 coverage task: `jacocoFullReport`, `jacocoFullAndroidTestReport`.

## 빌드와 검증 명령

현재 프로젝트와 CI에서 확인된 명령이다. 작업 범위에 맞게 가장 좁은 명령을 선택한다.

- `./gradlew spotlessCheck`: Kotlin formatting 검증.
- `./gradlew spotlessApply`: Kotlin formatting 적용.
- `./gradlew lintDebug`: Android lint 실행.
- `./gradlew assembleDebug`: debug APK 빌드.
- `./gradlew assembleDebugAndroidTest`: instrumentation test APK 빌드.
- `./gradlew testDebugUnitTest`: Android module unit test 실행.
- `./gradlew test`: JVM module unit test 실행.
- `./gradlew jacocoFullReport`: 전체 unit test coverage report 생성.
- CI full command: `./gradlew spotlessCheck lintDebug assembleDebug assembleDebugAndroidTest testDebugUnitTest jacocoFullReport --parallel --build-cache --configure-on-demand`.

Screenshot test 도입 후의 module별 `updateDebugScreenshotTest`·`validateDebugScreenshotTest` 명령과 golden 운영 규칙은 `docs/testing/SCREENSHOT_TEST_IMPLEMENTATION_PLAN.md`를 따른다. 도입 전에는 해당 task를 실행하지 않는다.

문서-only 변경은 Gradle 검증을 생략할 수 있지만, 최종 응답에 생략 이유를 명확히 보고한다.

## Task Workflows

사용자의 자연어 요청이 workflow trigger examples와 비슷하면 `docs/ai/workflows.md`의 대응 workflow를 적용한다. workflow 이름은 Codex CLI의 내장 명령이 아니다.

최종 응답 형식은 `docs/ai/change-report-template.md`, 검증 판단은 `docs/ai/quality-gates.md`, 작업 범위 판단은 `docs/ai/task-scope-control.md`를 따른다. Medium·Large·Risky Change와 policy·skill·agent 변경은 관련 근거와 회고를 Change Report에 포함하고 `docs/history/` 작업 이력에도 기록한다.

문서·코드 근거, 작업 이력, project skill·agent 배선, 세션 회고의 정본은 `docs/ai/knowledge-operations.md`와 `docs/knowledge/INDEX.md`다.

단순 질의와 짧은 상태 응답을 제외한 모든 project 작업은 시작 시 `project-knowledge-grounding`을 적용한다. 파일을 수정하는 모든 project 작업은 종료 전에 `session-retrospective`를 적용해 작업 이력, generated Index, 지식 관계 검증과 Change Report를 함께 점검한다.

## Custom Agent Delegation

custom agent의 역할·필수 문서·운영 계약은 `docs/ai/subagents.md`와 `docs/ai/registry/agents.toml`을 따른다.

- custom agent는 사용자가 명시적으로 요청했거나, 현재 workflow가 독립적인 검토가 필요하다고 정한 경우에만 호출한다.
- reviewer는 hard read-only다. 구현은 main agent가 직접 수행하거나, main agent가 명시한 제한된 write scope의 별도 worker만 수행한다.
- Small 변경은 기본적으로 main agent만 사용한다. 독립적인 read-only 조사·검토는 최대 3개까지 병렬화할 수 있으며, 같은 파일 또는 같은 동작을 수정하는 작업은 한 명만 write 담당으로 둔다.
- main agent는 subagent 결과를 검토·통합하고, 위험 판단·최종 적용·검증·Change Report 책임을 유지한다.

## Coding Conventions

Kotlin/Android 코드 작성 규칙의 source of truth는 `docs/ai/android-coding-conventions.md`다. 기존 프로젝트 custom docs도 함께 보존한다.

- 기존 상세 규칙: `docs/CONVENTION.md`
- AI coding guide: `docs/ai/CODING_GUIDELINES.md`
- UI guide: `docs/ai/UI_GUIDELINES.md`
- Data logic guide: `docs/ai/DATA_LOGIC_GUIDELINES.md`
- Workflow guide: `docs/workflow/DEVELOPMENT_FLOW.md`

규칙 간 충돌이 발견되면 이번 작업에서 임의로 정리하지 않고 follow-up으로 보고한다.

## Quality Gates

코드 수정 작업은 `docs/ai/quality-gates.md`의 검증 규칙을 따른다. 문서-only 변경은 Gradle 검증을 생략할 수 있다.

## Screenshot Test Strategy

Compose screenshot test의 도입 상태, 대상 module, fixture 분리, golden·CI 규칙은 `docs/testing/TESTING_GUIDE.md`와 `docs/testing/SCREENSHOT_TEST_IMPLEMENTATION_PLAN.md`를 source of truth로 둔다. Gradle plugin/dependency, source set, CI 변경은 Risky Change이므로 계획의 Phase 1 전에는 사용자 승인을 받는다.

## Task Scope Control

작업 크기와 위험 변경 판단은 `docs/ai/task-scope-control.md`를 따른다. Large Task 또는 Risky Change는 바로 수정하지 않고 영향 범위와 계획을 먼저 제시한다.

## 완료 기준

작업은 요청한 동작이 구현되고, 기존 아키텍처를 존중하며, 관련 build/test/lint 명령을 실행했거나 생략 이유를 명확히 설명했을 때 완료된다. 최종 응답은 `docs/ai/change-report-template.md`의 Change Report 형식을 우선 따른다.
