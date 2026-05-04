# 🧠 DATA_LOGIC_GUIDELINES.md

## 🎯 Mission
데이터 계층과 비즈니스 로직의 명확한 분리를 통해 테스트 가능하고 확장성 있는 코드를 작성합니다.

---

## 🏗️ Architecture Layers

### 1. Data Layer (Entity & RepositoryImpl)
* **Entity-Model Separation**: Entity(Data)와 Model(Domain) 분리 체계를 엄격히 준수합니다.
* **Mapping Strategy**: 데이터 레이어에서 도메인 레이어로 데이터를 전달할 때 반드시 매퍼 확장 함수(예: `toDomain()`)를 사용합니다.
* **Room Optimizing**: 외래키 사용 시 반드시 `indices = true`를 설정하여 쿼리 성능을 확보합니다.

### 2. Domain Layer (UseCase & Model)
* **UseCase Structure**: 하나의 UseCase는 하나의 비즈니스 책임만 가집니다. (`operator fun invoke` 선호)
* **Pure Kotlin**: 도메인 레이어는 안드로이드 프레임워크에 의존하지 않는 순수 코틀린 코드를 유지합니다.

---

## ⚙️ Logic & State Standards

### 1. State Management (ViewModel)
*   **State Combine Pattern (Standard)**: 화면의 복잡도가 높을 경우, 개별 데이터 조각을 위한 `private MutableStateFlow`들을 정의하고, 이를 `combine` 함수로 묶어 최종 UI 상태를 생성합니다.
*   **Single Source of Truth**: 내부 구현 방식(Combine 등)에 상관없이 UI 레이어에 노출되는 최종 상태는 반드시 단일 `StateFlow<UiState>`여야 합니다.
*   **StateFlow Promotion**: `stateIn`을 사용하여 Flow를 UI 상태로 승격합니다.
*   **Resource Optimization**: **`SharingStarted.WhileSubscribed(5_000)`** 정책을 기본으로 사용하여 리소스를 효율화하고 화면 회전 시 상태를 보존합니다.

### 2. Error & Flow Handling
* **Sealed State**: `UiState`와 에러 모델링에 `sealed interface`를 적극 활용합니다.
* **No `runCatching`**: 예외 처리는 반드시 `try-catch` 블록으로 명시합니다.

---

## ✅ Compliance Checklist
1. Entity와 Domain Model이 명확히 분리되었는가?
2. ViewModel 상태 노출 시 `WhileSubscribed(5000)`가 적용되었는가?
3. 비즈니스 로직이 UI 레이어에 침투하지 않았는가?
4. Room 엔티티의 인덱스 설정이 적절한가?
