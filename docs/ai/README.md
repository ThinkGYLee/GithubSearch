# AI Docs

## Template Metadata

- Template: `android-docs-bootstrap`
- Template Version: `1.0.2`
- Applied To: `GithubSearch`
- Applied Mode: `safe sync`
- Note: 이 문서들은 생성 후 프로젝트 상황에 맞게 수정될 수 있다.

## Source Of Truth

- 전역 `AGENTS.md`: 개인 공통 작업 스타일만 관리한다.
- `android-docs-bootstrap` skill: 새 Android 프로젝트 세팅용 보일러플레이트 원본을 관리한다.
- 프로젝트 `AGENTS.md`와 `docs/ai/*`: 현재 repository에 실제 적용되는 AI 작업 규칙을 관리한다.
- 기존 custom docs는 삭제하거나 덮어쓰지 않고 보존한다.

## Project Profile

- Project name: `GithubSearch`
- Structure: Android/Kotlin multi-module.
- Modules: `:app`, `:feature:home`, `:feature:detail`, `:feature:favorite`, `:feature:setting`, `:domain`, `:data`, `:core`, `:core:common`, `:core:designsystem`, `:core:ui`, `:core:testing`.
- Build logic: included build `build-logic` with convention plugins.
- Dependency management: `gradle/libs.versions.toml`.
- Kotlin: `2.3.21`.
- Android Gradle Plugin: `9.2.1`.
- Target SDK: `36`.
- Main stack: Compose, Material 3, Hilt, Room, Retrofit, OkHttp, Gson, Coroutines, Flow, Paging 3.
- Testing stack: JUnit4, MockK, kotlinx-coroutines-test.

## Included Core Docs

- `workflows.md`: 자연어 workflow trigger와 공통 workflow 규칙.
- `quality-gates.md`: 검증 명령 선택과 결과 보고 규칙.
- `task-scope-control.md`: 작업 크기와 위험 변경 제한 규칙.
- `android-coding-conventions.md`: Kotlin/Android 코드 작성 규칙과 commit convention.
- `change-report-template.md`: 최종 응답, 커밋 메시지, PR 설명 재사용 형식.
- `template-sync-policy.md`: skill template과 project docs 간 sync 정책.
- `subagents.md`: GithubSearch에서 단계적으로 사용할 Codex subagent 4개 역할과 운영 규칙 초안.
- `reporting-system-migration-plan.md`: SmartTimer식 보고·작업 이력·지식 운영 체계의 GithubSearch 이식 범위와 phase별 exit gate.
- `knowledge-operations.md`: 문서·코드 근거, 작업 이력, project skill·agent 배선, 세션 회고 규칙.
- `SKILLS_CATALOG.md`: project skill의 trigger와 경계.

## Preserved Custom Docs

- `docs/ai/GEMINI.md`: Gemini / legacy context adapter. Codex source of truth가 아니며 core docs를 참조한다.
- `docs/ai/CODING_GUIDELINES.md`: GithubSearch project-specific coding quality checklist. Generic workflow와 verification은 core docs를 참조한다.
- `docs/ai/UI_GUIDELINES.md`: GithubSearch Compose/UI-specific extension. Generic naming, scope, verification은 core docs를 참조한다.
- `docs/ai/DATA_LOGIC_GUIDELINES.md`: GithubSearch data/domain/ViewModel/Flow-specific extension. Generic convention은 core docs를 참조한다.
- `docs/CONVENTION.md`: 기존 Kotlin, Gradle, commit convention.
- `docs/workflow/DEVELOPMENT_FLOW.md`: 기존 개발 흐름과 PR/commit 가이드.
- `GEMINI.md`: root Gemini / legacy project context. Codex source of truth가 아니며 `AGENTS.md`와 core docs를 참조한다.

## Build And Verification Reference

현재 CI는 Markdown과 `docs/**` 변경만 있는 경우 build job을 건너뛴다. 코드 변경 시 CI는 다음 full command를 사용한다.

```bash
./gradlew spotlessCheck lintDebug assembleDebug assembleDebugAndroidTest testDebugUnitTest jacocoFullReport --parallel --build-cache --configure-on-demand
```

로컬 작업에서는 변경 범위에 맞게 `docs/ai/quality-gates.md`의 더 좁은 명령을 우선 선택한다. 문서-only 변경은 Gradle 검증을 생략할 수 있다.

## Bootstrap Reference

상세 bootstrap/update 절차와 checklist는 `android-docs-bootstrap` skill의 `SKILL.md`를 기준으로 한다. 이 README는 현재 project docs 구성과 source of truth를 요약한다.
