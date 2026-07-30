---
id: GS-2026-0004
status: completed
base_commit: 5296116
related_documents: [root-agents, quality-gates, knowledge-operations, knowledge-governance-enforcement-plan, history-readme]
related_code: [.githooks, scripts/git-hooks, scripts/ai/tests, settings.gradle.kts]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# Pre-commit hook 단일화와 번역 리소스 검사

## 요청

- SmartTimer와 GithubSearch hook을 비교한 권장 방향대로 version-controlled hook을 단일화하고, 중첩 Gradle 모듈에 맞는 staged 번역 리소스 lint를 추가한다.

## 근거

- GithubSearch의 실제 `.git/hooks/pre-commit`은 untracked 수동 확인형이며 비대화 환경에서는 검증 없이 통과한다.
- SmartTimer는 staged `values*` XML 변경에서 실제 module lint를 실행하지만, 최상위 모듈 전제라 GithubSearch의 `feature/home` 구조에 그대로 적용할 수 없다.

## 결정

- `.githooks/pre-commit`을 유일한 version-controlled dispatcher로 두고 knowledge와 resource guard를 순서대로 호출한다.
- 기존 `.git/hooks/pre-commit`은 삭제하지 않되, 현재 repository의 local `core.hooksPath`를 `.githooks`로 설정해 운영 기준에서 제외한다.
- SmartTimer resource lint 원칙은 가져오되, `feature/home` 같은 중첩 경로를 `:feature:home` Gradle module로 변환한다.

## 변경

- `.githooks/pre-commit`을 multi-guard dispatcher로 변경했다.
- `run-staged-resource-lint.sh`를 추가해 staged `*/src/main/res/values*/*.xml`에서 영향을 받은 module만 `lintDebug`로 검사한다.
- quality gate와 보강 명세에 dispatcher·번역 resource lint 운영 규칙을 반영했다.
- 현재 GithubSearch repository의 local `core.hooksPath`를 `.githooks`로 설정했다. 전역 Git 설정과 기존 `.git/hooks` 파일은 변경하지 않았다.
- resource guard와 dispatcher까지 포함하도록 dependency-free 회귀 검사를 확장했다.

## 검증

- `python3 -m unittest discover -s scripts/ai/tests -p 'test_*.py'`: 11 tests 통과.
- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error, 0 warning 통과.
- `sh .githooks/pre-commit`: staged 변경이 없을 때 정상 종료.
- `git config --show-origin --get core.hooksPath`: `.git/config`의 `.githooks`를 확인.
- `git diff --check`: 통과.
- `./gradlew lintDebug --console=plain`: 실제 Android SDK·Gradle 환경에서 `BUILD SUCCESSFUL`로 통과. resource lint 자체는 fake Gradle wrapper가 `:app:lintDebug`, `:feature:home:lintDebug`를 받는 회귀 검사와 전체 lint로 확인했다.
- 실제 staged `feature/home/src/main/res/values/hook_verification.xml`과 활성화된 `.githooks/pre-commit`: values resource 감지 후 `:feature:home:lintDebug`가 `BUILD SUCCESSFUL`로 통과. 검증 파일과 stage는 즉시 원복했다.

## 지시 이행

- 충족: SmartTimer의 staged resource lint를 GithubSearch 중첩 Gradle module 구조에 맞게 이식했다.
- 충족: 지식 검사와 resource lint를 하나의 version-controlled dispatcher로 단일화했다.
- 충족: 기존 수동 확인형 `.git/hooks`을 삭제하지 않고 local config로만 새 hook을 활성화했다.
- 충족: 사용자 untracked migration template은 변경·stage·commit하지 않았다.

## 스킬 평가

- `project-knowledge-grounding`: 기존 active hook, Gemini 설정, SmartTimer hook, Gradle module 경로를 비교하는 근거를 제공했다.
- `session-retrospective`: hook 활성화 설정, staged-path 동작, 회귀 검사, 사용자 local 파일 보존을 종료 전에 확인하는 데 사용했다.

## 개선 후보

- manual: 다른 clone 또는 worktree에서도 hook을 쓰려면 해당 checkout에서 `git config --local core.hooksPath .githooks`를 실행한다.
- manual: 다른 clone 또는 worktree에서는 local `core.hooksPath` 설정이 필요하다.
