---
id: GS-2026-0006
status: completed
base_commit: 165172d
related_documents: [coding-conventions, quality-gates, task-scope, history-readme]
related_code: [.editorconfig, build.gradle.kts, app, core, data, domain, feature, build-logic]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# ktlint 서식 기준 단일화

## 요청

- CI에서 발생한 Kotlin 서식 위반을 정리하고 하나의 서식 기준에 맞춘 뒤 PR에 반영한다.

## 근거

- `.editorconfig`의 `ktlint_code_style = android`은 현재 ktlint 1.8.0에서 유효하지 않아 CI가 `ktlint_official` 기본값으로 대체한다.
- PR의 `spotlessKotlinCheck`는 이 대체 규칙 기준으로 `GithubSearchScreen.kt`와 기존 `MainViewModel.kt`의 줄바꿈을 위반으로 판정했다.

## 결정

- 실제 CI가 적용 중인 `ktlint_official`을 명시적인 repository 단일 기준으로 채택한다.
- `spotlessApply`로 Kotlin·Gradle Kotlin 파일을 해당 기준으로 정렬하고, 전체 CI 명령으로 결과를 검증한다.

## 변경

- `.editorconfig`에서 유효하지 않은 `android` 값을 `ktlint_official`로 바꿔 로컬·CI의 실제 서식 규칙을 명시했다.
- `spotlessApply`로 108개 Kotlin 파일을 정렬했다. 자동으로 고칠 수 없던 긴 줄·연속 주석은 동작을 바꾸지 않는 줄바꿈과 주석 분리로 정리했다.
- `GithubSearchScreen`, `MainViewModel`을 포함해 app·core·data·domain·feature의 기존 서식 위반을 같은 기준으로 정리했다.

## 검증

- `./gradlew cleanSpotlessKotlin spotlessApply --rerun-tasks --no-configuration-cache --no-build-cache --console=plain`: 전체 서식 적용 통과.
- `./gradlew spotlessCheck lintDebug assembleDebug assembleDebugAndroidTest testDebugUnitTest jacocoFullReport --parallel --build-cache --configure-on-demand --no-daemon --console=plain`: CI와 동일한 전체 검증이 `BUILD SUCCESSFUL`로 통과.
- 지식 운영 문서 변경에 대해 generated Index와 graph verifier를 종료 전에 실행한다.

## 지시 이행

- 충족: 유효하지 않은 ktlint style 설정을 제거하고 하나의 명시적 규칙으로 통일했다.
- 충족: CI에서 발견된 서식 위반과 기존 모듈의 같은 기준 위반을 함께 정리했다.
- 충족: 전체 CI 명령을 로컬에서 통과시킨 뒤 기존 PR에 반영한다.
- 충족: 사용자 untracked `docs/multi-module-migration-template.md`는 변경·stage·commit하지 않는다.

## 스킬 평가

- `project-knowledge-grounding`: 품질 규칙, 작업 범위, 실제 Spotless·ktlint 설정을 함께 확인해 명시적인 단일 규칙을 선택하는 근거로 사용했다.
- `session-retrospective`: 전체 검증, 작업 이력, 지식 관계 검사, 사용자 local 파일 보존을 종료 전에 점검하는 데 사용했다.

## 개선 후보

- automated: CI가 통과한 뒤 `ktlint_code_style`의 유효성을 별도 사전 검증하는 것은 현재 plugin API 확인 후 follow-up으로 검토한다.
