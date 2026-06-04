# GitHub Search Coding Conventions

## 1. Strict Coding Rules (MUST FOLLOW)
- **No Elvis Operator (`?:`):** 가독성과 명확한 조건 분기를 위해 엘비스 연산자 사용을 엄격히 금지함. `if-else` 또는 명시적 체크 사용.
- **Explicit `if` Blocks:** 모든 `if` 문은 가독성을 위해 반드시 중괄호 `{ }` 블록을 사용해야 함. 단일 행 실행문이라도 예외 없이 블록 내부에 작성함.
- **No Arbitrary Modifications:** 명시적으로 지시받지 않은 변수명 변경, 패키지 구조 변경, 또는 스타일 수정을 엄격히 금지함. 요청받은 작업 범위 내에서만 정밀하게 수정할 것.
- **No `runCatching`:** 예외 처리는 명시적인 `try-catch` 블록을 사용함.
- **No Non-null Assertion (`!!`):** 강제 캐스팅 금지. 안전한 호출(`?.`)이나 스마트 캐스트 활용.
- **No `GlobalScope`:** 메모리 누수 및 리소스 관리의 어려움으로 인해 `GlobalScope` 사용을 절대 금지함. 대신 적절한 `CoroutineScope`(`viewModelScope`, `lifecycleScope` 등)를 사용할 것.

## 2. Naming & Style
- **Naming:** 
    - Class: `PascalCase`
    - Function/Variable: `camelCase`
    - Constants: `UPPER_SNAKE_CASE` (예: `MAX_REPEAT_COUNT`)
    - Resource: `snake_case`
    - Compose Function (UI): `PascalCase`
    - Preview Function: `PascalCase` + `Preview` 접미사 (예: `MainScreenPreview`)
- **Formatting:** 
    - 반드시 `Spotless` 가이드 및 아래의 추가 규칙을 준수함.
    - **Multi-line Arguments:** 함수 정의, 생성자, 호출 시 인자가 2개 이상인 경우 모든 인자를 개별 행에 배치하고, 마지막 인자 뒤에 반드시 쉼표(Trailing Comma)를 추가함.
    - **Named Arguments:** 함수 호출 및 객체 생성 시 반드시 `인자명 = 값` 형태의 Named Argument를 사용하여 각 인자의 역할을 명시함. 이때, 반드시 실제 정의된 인자명을 정확히 확인하여 컴파일 에러를 방지함.
    - **Encourage Function References (`::`):** 이벤트 핸들러나 콜백 등록 시 불필요한 람다 작성을 줄이고 가독성을 높이기 위해 메소드 참조(`viewModel::onClick` 등) 사용을 적극 권장함. 단, 파라미터 변환이 필요한 경우에만 명시적 람다를 사용함.
    - **Logical Operators:** `if` 문 등에서 논리 연산자(`&&`, `||`)로 인해 줄바꿈이 발생할 경우, 연산자를 **다음 줄의 맨 앞**에 배치하여 조건의 연속성을 명확히 드러냄.
- **Comments:** 
    - 코드와 같은 줄에 주석을 작성하는 것을 금지함 (Inline comment 금지).
    - 주석은 해당 변수, 함수, 클래스 등 관련 내용의 바로 윗줄에 작성하여 가독성을 확보함.
- **Command:** 코드 수정 후 검증 명령 선택은 `docs/ai/quality-gates.md`를 따른다.

## 3. Implementation Guidelines
- **State Management:** `ViewModel`과 `StateFlow`를 사용하여 상태 관리.
- **Dependency:** 모든 의존성은 `gradle/libs.versions.toml`에서 관리. 직접 버전을 기입하지 말 것.
- **Gradle:** 반드시 **Kotlin DSL (`.kts`)**만 사용하며, 공통 빌드 로직은 `:build-logic`을 참조.
- **Room Database:**
    - 외래키(`ForeignKey`) 사용 시 해당 컬럼에 `indices = true`를 지정하여 쿼리 성능 확보.
    - 스키마 변경 이력 관리를 위해 `exportSchema = true` 유지.

## 4. Git Commit Convention (MUST FOLLOW)
커밋 컨벤션의 source of truth는 `docs/ai/android-coding-conventions.md`입니다.

- **Format:** `<Type>(Scope) : Subject`
- **Type Rules:**
    - `Feat`: 새로운 기능 추가
    - `Fix`: 버그 수정
    - `Design`: UI/UX 디자인 변경
    - `Refactor`: 코드 리팩터링
    - `Docs`: 문서 수정/추가
    - `Test`: 테스트 코드 관련
    - `Chore`: 빌드/설정/의존성 변경
    - `Rename`: 파일/패키지명 변경
- **Subject:** 영어 명령형 사용, 50자 이내, 마침표 생략.
- **Body (선택):** 변경 이유와 핵심 내용을 **한국어**로 상세히 기술.
- **Atomic Commit:** 하나의 커밋은 하나의 논리적 변경 사항만 포함.

## 5. Workflow & Validation (Hooks)

### [Pre-Modification Hook]
- **역할:** 수정 전 상태 확인 및 보고.
- **로직:**
    1. 코드를 수정하기 전, `read_file` 등을 통해 최신 로컬 상태를 반드시 확인함.
    2. 변경할 대상 코드와 수정 계획을 사용자에게 요약 보고함.
    3. 사용자의 **"OK"** 또는 승인 입력을 받은 뒤에만 수정을 시작함.

### [Post-Modification Hook]
- **역할:** 변경 범위에 맞는 빌드 및 스타일 검증 수행.
- **로직:**
    - 코드 수정 후 `docs/ai/quality-gates.md`에 따라 변경 범위에 맞는 최소 검증을 선택한다.
    - Kotlin/Android 코드 수정 시 Spotless가 설정되어 있으면 `./gradlew spotlessApply`를 우선 고려한다.
    - Android module 변경은 `./gradlew :$MODULE_NAME:compileDebugKotlin` 또는 `./gradlew assembleDebug`를 고려한다.
    - 문서-only 변경은 Gradle 검증 대신 `rg`, `git diff`, `git status` 등으로 변경 범위를 확인할 수 있다.
- **성공 조건:** 선택한 검증 결과를 최종 응답에 명확히 보고하고, 실패가 있으면 남은 리스크를 숨기지 않는다.
