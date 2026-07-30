# 🧪 TESTING_GUIDE.md

## 🎯 Mission
GitHub Search의 안정성을 위해 **JUnit4 + MockK + Coroutines Test** 기반의 고품질 테스트 코드를 작성합니다. Compose UI의 시각 회귀는 도입 예정인 host-side screenshot test로 보완합니다.

---

## 🛠️ Testing Stack & Rules

* **Framework**: JUnit4
* **Assertion**: `kotlin.test`
* **Mocking**: MockK (엄격한 모킹 지향)
* **Async**: `kotlinx-coroutines-test` (`runTest`, `StandardTestDispatcher`)
* **Screenshot (도입 예정)**: Android 공식 Compose Preview Screenshot Testing (`com.android.compose.screenshot`)

> Screenshot test는 아직 Gradle plugin, `screenshotTest` source set, reference image가 적용되지 않은 **계획 단계**입니다. 구현 순서와 운영 규칙은 [SCREENSHOT_TEST_IMPLEMENTATION_PLAN.md](SCREENSHOT_TEST_IMPLEMENTATION_PLAN.md)를 따릅니다.

---

## 🧾 Naming & Structure

* **Function Naming**: 반드시 **`기능_시나리오_결과`** 형식의 한국어 언더스코어를 사용합니다.
* **Pattern**: `Given - When - Then` 주석을 단계별로 포함합니다.
* **Main Dispatcher**: ViewModel 테스트 시 `MainDispatcherRule`을 반드시 적용합니다.

---

## 🧪 Advanced Testing Patterns

### 1. Strict Verification (MockK)
* **`relaxed = true` 금지**: 모든 목 객체의 동작은 명시적으로 정의합니다.
* **Exact Verification**: `coVerify(exactly = 1)` 등을 사용하여 호출 횟수까지 정확히 검증합니다.

### 2. Async Time Control
* **`advanceUntilIdle()`**: 비동기 작업이 모두 완료될 때까지 가상 시간을 진행시켜 상태 전이를 검증합니다.

---

## 📸 Compose Screenshot Test 계획

### 목적과 범위

* screenshot test는 UI의 **시각적 결과**를 golden image와 비교한다. 클릭, 상태 전이, navigation 동작은 기존 unit/UI behavior test의 책임으로 유지한다.
* 도입 도구는 Layoutlib 기반의 host-side Android 공식 Compose Preview Screenshot Testing으로 제한한다. Robolectric, Paparazzi, Roborazzi, Dropshots는 이번 도입 범위에 포함하지 않는다.
* 첫 pilot은 `:feature:setting`의 화면 content다. Home/Favorite의 `LazyPagingItems`, Detail/Home/Favorite의 원격 Glide avatar는 fixture가 결정적이지 않아 후속 단계에서 다룬다.

### Fixture와 golden 규칙

* fixture는 Hilt, Flow 수집, 네트워크, system API, 실제 시간, animation을 우회하고 고정된 UI state와 no-op callback만 전달한다.
* theme은 `GithubSearchTheme(darkTheme = <명시값>, dynamicColor = false)`로 감싸 dynamic color에 의한 host 차이를 제거한다.
* 첫 기준 이미지는 `400 x 500dp`, Light theme, font scale `1.0`, 고정 데이터로 생성한다. 안정화 후 Dark theme, font scale `1.5`, 대표 wide layout을 추가한다.
* reference PNG는 `{module}/src/screenshotTestDebug/reference/`에 source와 함께 version control한다. `build/` report나 actual/diff 이미지는 커밋하지 않는다.
* 기준 이미지는 의도한 디자인 변경을 사람이 검토한 뒤 로컬 `update…ScreenshotTest`로만 갱신한다. CI는 `validate…ScreenshotTest`만 실행한다.

### 실행 규칙 (도입 후)

```bash
./gradlew :feature:setting:updateDebugScreenshotTest
./gradlew :feature:setting:validateDebugScreenshotTest
```

validation 실패 시 `{module}/build/reports/screenshotTest/preview/debug/index.html`과 reference/actual/diff를 확인한다. renderer 차이가 확인되기 전에는 golden을 갱신하거나 image-difference threshold를 완화하지 않는다.

### CI와 보고

* screenshot validation은 golden 생성 환경과 동일한 macOS runner에서 별도 job으로 실행한다. main build job에 secret이 필요하더라도 screenshot fixture는 secret 없이 실행 가능해야 한다.
* 실패 시 대상 module의 screenshot HTML report만 단기 artifact로 보관한다.
* UI, theme, layout, 대표 상태를 바꾼 작업은 대응 fixture가 있다면 validation 결과와 golden 또는 diff 이미지를 함께 보고한다.
* tool은 experimental이므로 plugin version, AGP/Kotlin/JDK 호환성과 renderer 환경을 도입 전에 다시 확인한다.

---

## ✅ Compliance Checklist
1. 테스트 함수명이 한국어 언더스코어 형식인가?
2. `runTest` 및 비동기 제어 로직이 포함되었는가?
3. `relaxed = true` 없이 명시적으로 모킹되었는가?
4. G-W-T 구조가 명확히 드러나는가?
5. screenshot 대상이라면 fixture가 외부 상태와 animation에 의존하지 않고, 의도한 golden 변경을 검토했는가?
