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
- JUnit4, MockK, kotlinx-coroutines-test.
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

문서-only 변경은 Gradle 검증을 생략할 수 있지만, 최종 응답에 생략 이유를 명확히 보고한다.

## Task Workflows

사용자의 자연어 요청이 workflow trigger examples와 비슷하면 `docs/ai/workflows.md`의 대응 workflow를 적용한다. workflow 이름은 Codex CLI의 내장 명령이 아니다.

최종 응답 형식은 `docs/ai/change-report-template.md`, 검증 판단은 `docs/ai/quality-gates.md`, 작업 범위 판단은 `docs/ai/task-scope-control.md`를 따른다.

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

## Task Scope Control

작업 크기와 위험 변경 판단은 `docs/ai/task-scope-control.md`를 따른다. Large Task 또는 Risky Change는 바로 수정하지 않고 영향 범위와 계획을 먼저 제시한다.

## 완료 기준

작업은 요청한 동작이 구현되고, 기존 아키텍처를 존중하며, 관련 build/test/lint 명령을 실행했거나 생략 이유를 명확히 설명했을 때 완료된다. 최종 응답은 `docs/ai/change-report-template.md`의 Change Report 형식을 우선 따른다.
