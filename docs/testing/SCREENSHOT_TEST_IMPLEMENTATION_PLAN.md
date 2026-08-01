# GithubSearch Compose Screenshot Test 도입 계획

> **Status:** planned  
> **Scope:** Android 공식 Compose Preview Screenshot Testing으로 Compose UI의 의도한 시각 결과를 version-controlled golden image와 비교한다.  
> **Source of truth:** 일반 테스트 규칙은 [TESTING_GUIDE.md](TESTING_GUIDE.md), 작업 범위와 검증은 [../ai/task-scope-control.md](../ai/task-scope-control.md), [../ai/quality-gates.md](../ai/quality-gates.md)를 따른다.

## 조사 결과와 도입 결정

SmartTimer `develop`의 host-side golden 운영을 참고해, GithubSearch에도 Android 공식 Compose Preview Screenshot Testing을 적용한다. 현재 GithubSearch의 AGP `9.2.1`, Kotlin `2.3.21`, Gradle `9.5.1`, JDK `17`, Compose Compiler Plugin 구성은 Gradle task 방식의 최소 요건을 만족한다.

- 공식 도구: `com.android.compose.screenshot`과 `com.android.tools.screenshot:screenshot-validation-api`
- test source set: `{module}/src/screenshotTest/kotlin`
- reference 경로: `{module}/src/screenshotTestDebug/reference`
- golden 생성: `:{module}:updateDebugScreenshotTest`
- golden 검증: `:{module}:validateDebugScreenshotTest`
- 실패 report: `{module}/build/reports/screenshotTest/preview/debug/index.html`

도구는 experimental이다. 적용 직전에 [Android 공식 setup 문서](https://developer.android.com/studio/preview/compose-screenshot-testing)와 release notes를 재확인하고 version catalog에 실제 적용 version을 고정한다.

## 운영 원칙

1. screenshot test는 visual regression만 다룬다. 사용자 interaction, state transition, navigation, system UI 동작은 unit/behavior/instrumented test로 분리한다.
2. route는 Hilt, `Flow` 수집, side effect, system API를 소유한다. screenshot fixture는 고정 UI state와 callback만 받는 content를 호출한다.
3. fixture에는 네트워크, 실제 시간, dynamic color, 진행 중 animation, 무작위 값, platform 설정 읽기를 남기지 않는다.
4. 모든 fixture는 `GithubSearchTheme(darkTheme = <명시값>, dynamicColor = false)`를 사용한다.
5. reference PNG는 source와 함께 커밋한다. CI는 reference를 갱신하거나 자동 커밋하지 않고 validation만 수행한다.
6. golden diff는 의도한 디자인 변경을 사람이 시각적으로 검토한 뒤에만 승인한다. renderer 환경 차이가 의심돼도 수치와 diff를 확인하기 전 threshold를 바꾸지 않는다.
7. Screenshot fixture가 있는 UI/theme/layout/대표 state 변경은 validation 결과와 image를 작업 보고에 포함한다.

## 대상 우선순위

| 순서 | 대상 | 근거 | 선행 조건 |
| --- | --- | --- | --- |
| 1 | `:feature:setting` | 고정 목록·dialog 상태가 있어 최초 화면 golden을 결정적으로 만들기 쉽다. | `SettingScreen`에서 Hilt, Flow, Snackbar, `AppCompatDelegate`를 route에 남기고 `internal SettingContent`를 분리한다. |
| 2 | `:feature:detail`의 정적 repository/profile component | 현재 Preview fixture가 있어 component golden을 시작하기 쉽다. | remote Glide avatar를 정적 placeholder/slot으로 바꾸고 private content 경계를 test-visible `internal`로 조정한다. |
| 3 | `:core:designsystem`의 정적 component | feature 공통 시각 요소를 조기에 보호한다. | animation과 shared transition 의존을 정지 상태로 고정한다. |
| 4 | `:feature:home`, `:feature:favorite` | 대표 목록·선택·dialog 상태의 회귀를 막는다. | `LazyPagingItems`, 원격 avatar, animation/blur를 고정 fixture 또는 별도 rendering content로 분리한다. |
| 5 | 앱 shell/navigation | feature golden이 안정된 뒤 통합 배치를 확인한다. | SharedTransition, navigation, runtime 의존성이 없는 rendering component만 대상으로 삼는다. |

## Phase 0 — 적용 전 기준선

**목적:** code/Gradle 변경 전의 호환성, 첫 pilot 범위, golden 정책을 확정한다.

- [x] AGP, Kotlin, Gradle, JDK, Compose Compiler Plugin 호환성을 확인했다.
- [x] 현재 project에 screenshot plugin, `screenshotTest` source set, reference PNG, screenshot Gradle task가 없음을 확인했다.
- [x] 첫 pilot module을 `:feature:setting`으로 정했다.
- [x] theme의 dynamic color, Home/Favorite Paging, Detail/Home/Favorite의 Glide avatar와 animation을 결정성 위험으로 기록했다.
- [ ] 적용 직전에 공식 plugin version과 renderer release note를 재확인한다.

**Exit gate:** `./gradlew --version`, 현재 Gradle/source 검색 결과, `git diff --check`를 확인한다.

## Phase 1 — `:feature:setting` 기반 구성

**목적:** 다른 module을 바꾸지 않고 screenshot plugin, source set, 첫 task를 `:feature:setting`에만 적용한다.

**예정 변경 파일:**

- `gradle.properties`: `android.experimental.enableScreenshotTest=true`
- `gradle/libs.versions.toml`: screenshot plugin·validation API alias
- `build.gradle.kts`: screenshot plugin root declaration (`apply false`)
- `feature/setting/build.gradle.kts`: plugin, module experimental property, `screenshotTestImplementation`

**Exit gate:**

```bash
./gradlew :feature:setting:tasks --all
./gradlew :feature:setting:compileDebugKotlin :feature:setting:compileDebugScreenshotTestKotlin
./gradlew :app:assembleDebug
./gradlew spotlessCheck
```

## Phase 2 — Settings 대표 상태 golden

**목적:** 첫 screen-level fixture와 reference image를 만든다.

- `SettingScreen`의 Hilt/Flow/system effect와 rendering content를 분리한다.
- 기본 Settings, theme dialog, authentication/reset dialog처럼 시각적으로 다른 대표 상태를 고정 fixture로 만든다.
- 첫 기준은 `400 x 500dp`, Light, font scale `1.0`으로 만든다.
- `updateDebugScreenshotTest` 결과를 사람이 검토한 뒤 reference PNG를 커밋한다.

**Exit gate:**

```bash
./gradlew :feature:setting:updateDebugScreenshotTest
./gradlew :feature:setting:validateDebugScreenshotTest
./gradlew :feature:setting:compileDebugKotlin :app:assembleDebug spotlessCheck
```

## Phase 3 — 변형과 component 확대

**목적:** 안정화된 대표 화면에서 accessibility·layout 회귀를 점진적으로 확인한다.

- Settings 핵심 상태에 Dark theme, `fontScale = 1.5f`, 대표 wide layout을 추가한다.
- 이후 screen-level suite는 compact/medium/expanded 폭(`400`, `610`, `900dp`)과 높이(`400`, `500`, `1000dp`) 조합을 단계적으로 확대한다.
- Detail은 network avatar를 fixture에서 배제한 뒤 static profile/repository component부터 시작한다.
- Home/Favorite는 Paging item을 고정 rendering model로 바꾸거나 paging 의존을 content 밖으로 분리한 후 진행한다.

## Phase 4 — CI validation

**목적:** golden 생성 환경과 같은 renderer에서 PR 회귀를 차단한다.

- `.github/workflows/build.yml`에 macOS screenshot job을 별도로 추가한다. `contents: read`만 부여하며 OAuth/Gradle cache secret을 전달하지 않는다.
- screenshot job은 `:feature:setting:validateDebugScreenshotTest`만 실행한다.
- failure일 때만 `feature/setting/build/reports/screenshotTest/preview/debug/`를 artifact로 짧게 보관한다.
- 현재 build workflow의 code-change filter에 `src/screenshotTest/`와 `src/screenshotTestDebug/reference/`를 포함해 fixture/reference 변경도 검증 대상이 되게 한다.
- Linux와 macOS renderer가 다르면 golden 생성·validation runner를 macOS로 통일하고, Linux main build와 screenshot validation을 분리한다.

## 제외 범위

- 이번 도입에서 Robolectric, Paparazzi, Roborazzi, Dropshots, Gradle Managed Device, UI Automator를 추가하지 않는다.
- system permission dialog, OAuth browser, notification shade처럼 host fixture로 표현할 수 없는 UI는 screenshot golden으로 검증하지 않는다.
- CI에서 `update…ScreenshotTest`를 실행하거나 reference PNG를 자동 커밋하지 않는다.
