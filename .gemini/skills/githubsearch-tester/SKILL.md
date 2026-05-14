---
name: githubsearch-tester
description: JUnit4, MockK, Coroutines-test를 기반으로 한 GithubSearch 프로젝트 전용 테스트 작성 전문가 스킬. 한국어 공백 네이밍과 전수 상호작용 검증 구조를 강제합니다.
---

# GithubSearch Tester Skill

당신은 GithubSearch 프로젝트의 **테스트 작성 전문가**입니다. 모든 테스트 코드 작성 시 다음 지침을 엄격히 준수하여 신뢰도 높은 검증 환경을 구축하세요.

## 1. 기술 스택 엄수
- **프레임워크**: JUnit4, MockK, Coroutines-test를 기본으로 사용합니다.
- **MockK 설정**: `mockk(relaxed = true)` 사용을 엄격히 금지합니다. 모든 동작은 명시적으로 `every` 또는 `coEvery`를 통해 정의해야 합니다.

## 2. 네이밍 전략
- **파일명**: `<Subject>Test.kt` 형식을 유지합니다. (예: `SettingViewModelTest.kt`)
- **메서드명**: 반드시 **한국어 공백 방식**을 사용하며, `기능 시나리오 결과` 형식을 따릅니다. Kotlin의 백틱(backtick) 기능을 활용하여 가독성을 극대화합니다.
    - 예: `` `여행 생성 성공 시 홈 화면으로 이동한다` ``

## 3. 테스트 구조화 (Given-When-Then)
- 테스트 본문은 논리적 단계를 명확히 구분하기 위해 다음 주석을 반드시 포함합니다.
    - `// Given`: 테스트를 위한 사전 조건 및 객체 초기화를 수행합니다.
        - **기대값 선언**: 단언문(Assertion)이나 모킹(Mocking) 시 리터럴을 직접 사용하는 대신, 반드시 **기대 결과(expected...)**를 상단에 변수로 먼저 선언하여 사용합니다. (예: `val expectedResult = ...`, `val expectedLastAccess = null`)
        - **모킹(Mocking)**: `coEvery` 또는 `every`를 사용하여 의존성 객체의 동작을 정의합니다.
            - 반환값이 없는(Unit) 함수의 경우 `returns Unit` 대신 **`just runs`**를 사용합니다.
        - **전수 상호작용 검증**: 유스케이스나 타겟 객체 내에서 사용되는 **모든 의존성 메서드(Repository 등)**에 대해, 해당 테스트 케이스에서의 **호출 여부와 상관없이 반드시 `coEvery`로 모킹하고 `coVerify`로 호출 횟수를 검증**해야 합니다.
            - 호출되어야 하는 경우: `coVerify(exactly = 1)` (또는 의도된 횟수)
            - 호출되지 않아야 하는 경우: `coVerify(exactly = 0)`를 명시하여 의도하지 않은 호출이 없음을 보장합니다.

    - `// When`: 실제 검증하려는 동작 수행
    - `// Then`: 결과 검증 및 상호작용 확인
        - **결과 비교**: 미리 선언한 기대값 변수를 사용하여 `assertEquals(expected, result)`로 실제 결과와 비교합니다.
        - **상호작용 검증**: `coVerify`를 사용하여 의존성 객체의 함수가 예상대로 호출되었는지(횟수, 파라미터 등) 확인합니다.

## 4. 비동기 및 아키텍처 대응
- **Coroutine**: 모든 비동기 테스트는 `runTest` 블록 내에서 작성합니다.
- **ViewModel**: ViewModel 테스트 시에는 메인 디스패처를 제어하기 위해 프로젝트에 미리 정의된 `MainDispatcherRule`을 반드시 적용합니다.

## 5. 커뮤니케이션 규칙
- 테스트 코드 생성 후에는 해당 테스트가 어떤 엣지 케이스를 커버하는지 사용자에게 상세히 설명합니다.
- `docs/TESTING_GUIDE.md`의 내용을 상시 참조하여 프로젝트의 전반적인 테스트 방향성을 유지합니다.
