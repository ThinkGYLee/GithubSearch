# Android Coding Conventions

이 문서는 실제 Kotlin/Android 코드 작성 규칙의 source of truth다. 작업 범위 판단은 `docs/ai/task-scope-control.md`, 검증 명령 선택은 `docs/ai/quality-gates.md`를 따른다.

Template metadata는 `docs/ai/README.md`를 따른다.

기존 프로젝트 custom convention은 `docs/CONVENTION.md`, `docs/ai/CODING_GUIDELINES.md`, `docs/ai/UI_GUIDELINES.md`, `docs/ai/DATA_LOGIC_GUIDELINES.md`에 보존되어 있다. 이 문서는 core AI docs의 기준점을 제공하며, 충돌이 발견되면 임의로 정리하지 않고 follow-up으로 보고한다.

## Strict Coding Rules

- Elvis operator `?:`를 사용하지 않는다. `if-else` 또는 명시적 null check를 사용한다.
- 모든 `if` 문은 단일 행 실행문이어도 반드시 `{ }` 블록을 사용한다.
- 명시적으로 요청받지 않은 변수명 변경, 패키지 구조 변경, 스타일 수정을 하지 않는다.
- `runCatching`을 사용하지 않고 명시적인 `try-catch` 블록을 사용한다.
- non-null assertion `!!`를 사용하지 않는다. 안전한 호출, 명시적 null check, smart cast를 사용한다.
- `GlobalScope`를 사용하지 않는다. `viewModelScope`, `lifecycleScope`, 주입된 `CoroutineScope` 등 적절한 scope를 사용한다.

## Naming And Style

- Class와 Compose function은 `PascalCase`를 사용한다.
- Function과 variable은 `camelCase`를 사용한다.
- Constant는 `UPPER_SNAKE_CASE`를 사용한다.
- Resource 이름은 `snake_case`를 사용한다.
- Preview function은 `PascalCase`에 `Preview` suffix를 붙인다.
- Spotless가 설정된 프로젝트에서는 Spotless 규칙을 따른다.
- 함수 정의, 생성자, 호출에서 인자가 2개 이상이면 multi-line과 trailing comma를 사용한다.
- 함수 호출과 객체 생성에는 named arguments를 사용하고, 사용 전 실제 파라미터명을 확인한다.
- 이벤트 핸들러나 callback은 가능하면 function reference `::`를 사용한다.
- 파라미터 변환이 필요한 경우에만 명시적 lambda를 사용한다.
- 논리 연산자 줄바꿈 시 `&&`, `||`는 다음 줄의 맨 앞에 둔다.
- inline comment를 작성하지 않는다. 주석이 필요하면 관련 코드 바로 윗줄에 작성한다.

## Implementation Guidelines

- 상태 관리는 ViewModel과 `StateFlow`를 우선 사용한다.
- 모든 dependency version은 `gradle/libs.versions.toml`에서 관리한다.
- `build.gradle`에는 직접 version을 적지 않는다.
- Gradle은 Kotlin DSL만 사용한다.
- 공통 빌드 로직은 `:build-logic`을 참조한다.
- Room `ForeignKey` 사용 시 해당 컬럼에 index를 지정한다.
- Room `exportSchema = true`를 유지한다.

## Project Modules

- UI feature는 `:feature:home`, `:feature:detail`, `:feature:favorite`, `:feature:setting` 경계를 우선 지킨다.
- Domain logic은 `:domain`에 두고 Android framework dependency를 추가하지 않는다.
- Data implementation은 `:data`에 두고 `:domain` interface를 구현한다.
- Shared UI component와 theme은 `:core:designsystem`을 우선 사용한다.
- 테스트 fixture와 coroutine rule은 `:core:testing`을 우선 사용한다.

## Git Commit Convention

- Format: `<Type>(Scope) : Subject`
- Type: `Feat`, `Fix`, `Design`, `Refactor`, `Docs`, `Test`, `Chore`, `Rename`
- Subject는 한국어 변경 요약으로 작성한다. 코드 식별자처럼 번역이 부정확한 용어만 영어를 사용한다.
- Subject는 50자 이내로 작성한다.
- Subject 끝에 마침표를 붙이지 않는다.
- Body는 선택이며, 작성 시 한국어로 변경 이유와 핵심 내용을 설명한다.
- 하나의 커밋은 하나의 논리적 변경만 포함한다.

## Workflow And Validation

- 수정 전에는 관련 파일의 최신 로컬 상태를 확인한다.
- 변경 대상과 수정 계획을 요약한다.
- Medium, Large, Risky Change는 사용자 승인 후 수정한다.
- Small Task는 사용자가 명확히 수정을 요청한 경우 바로 진행할 수 있다.
- 코드 수정 후 기본 검증은 `./gradlew spotlessApply`, Android module의 `./gradlew :<module>:compileDebugKotlin`, Kotlin/JVM module의 `./gradlew :<module>:compileKotlin`, 여러 모듈 변경의 `./gradlew assembleDebug`를 우선 고려한다.
- 문서-only 변경은 Gradle 검증이 필요 없다.
