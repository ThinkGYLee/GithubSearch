# 🧪 TESTING_GUIDE.md

## 🎯 Mission
GitHub Search의 안정성을 위해 **JUnit4 + MockK + Coroutines Test** 기반의 고품질 테스트 코드를 작성합니다.

---

## 🛠️ Testing Stack & Rules

* **Framework**: JUnit4
* **Assertion**: `kotlin.test`
* **Mocking**: MockK (엄격한 모킹 지향)
* **Async**: `kotlinx-coroutines-test` (`runTest`, `StandardTestDispatcher`)

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

## ✅ Compliance Checklist
1. 테스트 함수명이 한국어 언더스코어 형식인가?
2. `runTest` 및 비동기 제어 로직이 포함되었는가?
3. `relaxed = true` 없이 명시적으로 모킹되었는가?
4. G-W-T 구조가 명확히 드러나는가?
