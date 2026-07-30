# Quality Gates

이 문서는 현재 repository에 적용된 검증 규칙이다. 검증 명령 선택과 결과 보고를 담당하며, 작업 크기와 위험 변경 판단은 `docs/ai/task-scope-control.md`, 코드 작성 규칙은 `docs/ai/android-coding-conventions.md`가 담당한다.

Template metadata는 `docs/ai/README.md`를 따른다.

모든 코드 수정 작업은 verification step으로 끝나야 한다. Codex는 변경 범위에 맞는 가장 좁고 적절한 local 검증 명령을 우선 선택하고, 마지막 응답에 실행 여부와 결과를 반드시 보고한다.

## 공통 원칙

- 검증 명령을 실행했으면 실행한 명령과 결과를 보고한다.
- 검증하지 못했으면 이유를 명확히 말한다.
- 검증하지 않았는데 `verified`, `success`, `passed`처럼 검증 완료를 암시하는 표현을 쓰지 않는다.
- 문서만 수정한 경우 Gradle 검증은 필요 없다고 보고한다.
- 실패한 검증이 있으면 실패 사실과 남은 리스크를 숨기지 않는다.
- 기존 작업트리에 사용자 변경이 있으면 그 변경을 건드리지 않았는지 `git status --short`로 확인한다.
- 문서·project skill·custom agent·작업 이력 변경은 `python3 scripts/ai/generate_knowledge_index.py --check`와 `python3 scripts/ai/verify_knowledge_graph.py`를 실행한다.

## 변경 유형별 기본 선택

- Kotlin/Android 코드 수정 공통: Spotless가 설정되어 있으면 `./gradlew spotlessApply`를 우선 고려한다.
- formatting 검증만 필요한 경우: `./gradlew spotlessCheck`를 고려한다.
- Android module 변경: `./gradlew :<module>:compileDebugKotlin` 또는 `./gradlew assembleDebug`를 우선 고려한다.
- Kotlin/JVM module 변경: `./gradlew :<module>:compileKotlin` 또는 `./gradlew test`를 우선 고려한다.
- 여러 모듈 변경 또는 모듈 판단이 어려운 경우: `./gradlew assembleDebug` 또는 CI full command를 고려한다.
- Compose 변경: 보통 `./gradlew assembleDebug`를 우선 고려한다.
- ViewModel / Flow / Domain 변경: `./gradlew testDebugUnitTest`, `./gradlew test`, 또는 module-specific test를 우선 고려한다.
- Hilt / DI 변경: `./gradlew assembleDebug` 또는 CI full command를 고려한다.
- Room / DB 변경: migration 영향 분석을 먼저 수행한다.
- Gradle / build-logic 변경: `./gradlew assembleDebug` 또는 CI full command를 고려한다.
- 문서-only 변경: Gradle 검증은 실행하지 않아도 되며 `rg`, `git diff`, `git status` 등으로 확인한다.
- 문서·project skill·custom agent·작업 이력 변경: knowledge index와 graph 검증을 실행한다. Gradle 검증은 생략할 수 있으나 사유를 보고한다.

## CI Reference

현재 `.github/workflows/build.yml`은 Markdown과 `docs/**`만 변경된 경우 CI build를 건너뛴다. 코드 변경으로 판단되면 다음 명령을 실행한다.

```bash
./gradlew spotlessCheck lintDebug assembleDebug assembleDebugAndroidTest testDebugUnitTest jacocoFullReport --parallel --build-cache --configure-on-demand
```

CI full command는 CI 기준의 full verification이며 local에서 항상 강제하지 않는다. local에서는 변경 범위에 맞는 최소 검증을 우선하고, 여러 모듈, DI, Gradle, build-logic 영향처럼 범위가 넓은 경우 CI full command 실행을 고려한다.

## Verification Section

최종 응답의 Verification 섹션에는 아래 내용을 포함한다.

```text
Verification:
- Command: <command or not run>
- Result: <passed / failed / not run>
- Notes: <short explanation>
```
