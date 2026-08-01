# Liquid Navigation UI 수정 후보

> **Status:** proposed — 아직 구현하지 않음
>
> **Source:** `compose-ui-reviewer`의 Phase 1 runtime 검토 결과와 현재 Compose 코드
>
> **Purpose:** Liquid navigation overlay와 접근성·시각 회귀 검증에서 확인된 가능성을 구현 작업과 분리해 기록한다.

## 범위와 비범위

이 문서는 수정 후보, 근거, 검증 방법만 기록한다. 아래 후보는 아직 재현 테스트나 화면 변경으로 확정된 버그가 아니다.

- 포함: Setting 화면의 overlay 안전 영역, 하단 탭 접근성 그룹, Liquid navigation Preview·시각 회귀 검증
- 제외: navigation route·back stack·OAuth 동작, 새로운 screenshot Gradle plugin 또는 dependency, 디자인 색상·타이포그래피 재설계
- 구현 전: `compose-ui-reviewer`와 `test-strategy-reviewer`의 근거 기반 검토를 다시 요청하고, 작은 범위의 별도 작업으로 진행한다.

## 현재 근거

- [Liquid Navigation Guide](../design/LIQUID_NAVIGATION_GUIDE.md)는 navigation bar가 `Scaffold` 바깥의 overlay이고, 각 화면의 마지막 콘텐츠가 bar 위까지 스크롤될 하단 여유를 가져야 한다고 정의한다.
- `GithubSearchScreen`은 Detail 외 화면에 `BottomNavigation`을 `Box`의 하단 overlay로 표시한다.
- `SettingScreen`은 기본 `Scaffold`의 `paddingValues`를 전체 `SettingMainBlock`에 적용한다.
- `LiquidNavigationBar`의 각 탭은 `Role.Tab`을 가진 `selectable`이지만 상위 `Row`는 selection group을 선언하지 않는다.
- 현재 `LiquidNavigationBar`에는 Light/Dark Preview 또는 navigation 동작을 직접 검증하는 UI/instrumentation/screenshot test가 없다.

## 후보 1 — Setting 화면의 하단 overlay 안전 영역

**우선순위:** Medium — 사용자 화면에서 콘텐츠 또는 Snackbar가 가려질 가능성

**관련 경로:**

- `feature/setting/src/main/java/com/gyleedev/githubsearch/feature/setting/SettingScreen.kt`
- `app/src/main/java/com/gyleedev/githubsearch/ui/GithubSearchScreen.kt`
- `docs/design/LIQUID_NAVIGATION_GUIDE.md`

**가능성:** Setting 화면은 기본 `Scaffold` inset과 `paddingValues`를 사용하지만, Liquid navigation은 앱 shell의 overlay다. compact landscape 또는 큰 글꼴에서 마지막 설정 항목·Snackbar가 bar와 겹칠 수 있다. 현재 OAuth AuthTab이 전면에 있어 runtime 화면으로는 아직 재현하지 못했다.

**수정 전 확인:**

1. Home·Favorite와 Setting의 Scaffold/inset·마지막 콘텐츠 여유 방식을 코드에서 비교한다.
2. emulator에서 portrait, landscape, 큰 글꼴에서 마지막 설정 항목과 Snackbar를 확인한다.
3. 겹침이 실제로 재현될 때만 Setting의 자체 scaffold/inset 또는 list 하단 여유를 최소 변경으로 정렬한다.

**최소 검증:** 변경 화면의 수동 확인, `./gradlew :feature:setting:compileDebugKotlin` 또는 관련 `assembleDebug`, 필요한 경우 screenshot test 도입 계획과 분리된 UI 검증.

## 후보 2 — Liquid navigation 탭의 접근성 선택 그룹

**우선순위:** Low — TalkBack 등 접근성 서비스의 탭 묶음 인식 개선 가능성

**관련 경로:**

- `app/src/main/java/com/gyleedev/githubsearch/ui/LiquidNavigationBar.kt`

**가능성:** 각 탭은 `Role.Tab`을 전달하지만 탭을 감싸는 `Row`에는 `selectableGroup()`이 없다. group semantics를 추가하면 선택 항목이 하나의 탭 집합이라는 정보를 접근성 서비스에 더 명확히 제공할 수 있다.

**수정 전 확인:** 현재 Compose semantics tree와 TalkBack에서 선택 상태·순서가 실제로 부족한지 확인한다.

**최소 수정 후보:** 탭 `Row`에 group semantics만 추가하고, 선택 동작·route·색상·애니메이션은 바꾸지 않는다.

**최소 검증:** Compose semantics 또는 device TalkBack 확인, `./gradlew :app:compileDebugKotlin`, 선택된 탭과 비선택 탭의 접근성 label 수동 확인.

## 후보 3 — Liquid navigation Preview와 시각 회귀 검증

**우선순위:** Low — 테마·레이아웃 회귀를 조기에 확인할 수 있는 기반

**관련 경로:**

- `app/src/main/java/com/gyleedev/githubsearch/ui/LiquidNavigationBar.kt`
- `docs/ai/UI_GUIDELINES.md`
- `docs/testing/SCREENSHOT_TEST_IMPLEMENTATION_PLAN.md`

**가능성:** Liquid navigation은 light/dark 테마, 선택 탭, 폭에 따라 glass와 pill 위치가 달라지지만 해당 상태를 고정해 보는 Preview가 없다. screenshot test plugin은 아직 도입 전이므로 지금 새 dependency를 추가하지 않는다.

**수정 전 확인:** Preview에 필요한 static state와 callback을 production navigation controller 없이 전달할 수 있는지 확인한다.

**최소 수정 후보:** `LiquidNavigationBar`의 선택 route를 고정한 Light/Dark Preview를 추가한다. screenshot test는 기존 도입 계획의 승인·Phase와 별도 진행한다.

**최소 검증:** Android Studio Preview 확인, `./gradlew :app:compileDebugKotlin`; screenshot test는 plugin 도입 승인 후에만 `validateDebugScreenshotTest`를 사용한다.

## 구현 순서와 완료 기준

| 순서 | 후보 | 실행 조건 | 완료 기준 |
| --- | --- | --- | --- |
| 1 | Setting overlay | 실제 겹침 재현 또는 코드 근거 재확인 | 마지막 항목·Snackbar가 portrait/landscape/큰 글꼴에서 가려지지 않음 |
| 2 | tab group semantics | semantics 또는 TalkBack 확인 | 세 탭이 하나의 선택 그룹으로 읽히고 선택 동작 유지 |
| 3 | Preview | static state 분리 가능 | Light/Dark Preview에서 세 탭과 선택 상태 확인 |

각 후보는 별도 Small 작업으로 처리한다. route, navigation 동작, screenshot Gradle plugin, UI 구조 전면 변경으로 범위가 커지면 `docs/ai/task-scope-control.md`의 Risky Change 절차로 중단하고 계획을 갱신한다.

## 현재 결론

세 항목은 수정 후보이며, 앱 동작 결함으로 확정하지 않았다. 다음 UI 작업에서 실제 재현·접근성 확인을 먼저 수행한 뒤 최소 변경만 적용한다.
