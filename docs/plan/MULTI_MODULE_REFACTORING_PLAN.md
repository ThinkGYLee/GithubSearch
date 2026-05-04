# GitHub Search: Multi-Module Refactoring Plan

## 🎯 Objective
현재 단일 `app` 모듈에 집중되어 있는 비즈니스 로직, 데이터 계층, UI 화면들을 `domain`, `data`, `core`, `feature` 모듈로 점진적으로 분리하여 클린 아키텍처 기반의 멀티 모듈 구조를 완성합니다.

## 🛠️ Key Files & Context
- `app/src/main/java/com/gyleedev/githubsearch/` 내의 전체 코드
- `build-logic`에 구축된 컨벤션 플러그인 (`AndroidLibraryConventionPlugin`, `AndroidLibraryComposeConventionPlugin`, `JvmLibraryConventionPlugin` 등)
- 각 모듈 생성 시 `build.gradle.kts`에 컨벤션 플러그인을 사용하여 설정 간소화

## 📋 Implementation Steps (진행 상황)

### Phase 1: `:domain` 모듈 마이그레이션 (순수 Kotlin)
- [ ] `:domain` 모듈 생성 및 `gyleedev.jvm.library` 플러그인 적용
- [ ] `app/.../domain/model` 패키지의 엔티티/데이터 클래스 이동
- [ ] `app/.../domain/usecase` 패키지의 UseCase 클래스 이동
- [ ] `app` 모듈에서 `:domain` 모듈 의존성 추가 및 빌드 검증

### Phase 2: `:data` 모듈 마이그레이션 (Android Library)
- [ ] `:data` 모듈 생성 및 `gyleedev.android.library` (또는 data 전용 컨벤션) 적용
- [ ] `:data` 모듈에서 `:domain` 의존성 추가
- [ ] `app/.../data/database` (Room DAO, Entity, Database) 이동
- [ ] `app/.../data/remote` (Retrofit Service, Interceptor, Response) 이동
- [ ] `app/.../data/repository` (Repository Impl) 이동
- [ ] `app/.../data/paging` (PagingSource) 이동
- [ ] Dagger Hilt 모듈(`NetworkModule`, `DatabaseModule`, `RepositoryModule`) 이동 및 수정

### Phase 3: :core:common 및 :core:designsystem 분리
- [x] **:core:designsystem**: UI 공통 요소 마이그레이션
    - 이동 대상: `app/.../ui/theme/` (Color.kt, Theme.kt, Type.kt)
    - 목적지: `core/designsystem/.../core/designsystem/theme/`
    - 패키지명 변경: `com.gyleedev.githubsearch.ui.theme` -> `com.gyleedev.githubsearch.core.designsystem.theme`
- [x] **:core:common**: 안드로이드/비즈니스 공통 로직 분리
    - 모듈 생성: `core/common` 추가 (Android Library)
    - 이동 대상:
        - `app/.../core/BaseViewModel.kt` -> `core/common/.../core/common/BaseViewModel.kt`
        - `app/.../util/LifecycleUtil.kt` -> `core/common/.../core/common/util/LifecycleUtil.kt`
    - 의존성 추가: `:domain` (FetchState 사용을 위함)
- [ ] **의존성 정비**:
    - `app` 모듈에서 `:core:designsystem`, `:core:common` 의존성 추가
    - `MainActivity`, `GithubSearchScreen` 등 기존 참조 코드의 import 경로 수정
    - 각 모듈에 적합한 Compose/Library 컨벤션 플러그인 적용 (`gyleedev.android.library.compose`, `gyleedev.android.library`)

### Phase 4: `:feature:*` 모듈 마이그레이션 (Compose UI)
- [ ] **`:feature:home`**: `app/.../ui/home` 패키지 (Screen, ViewModel) 이동
- [ ] **`:feature:detail`**: `app/.../ui/detail` 패키지 이동
- [ ] **`:feature:favorite`**: `app/.../ui/favorite` 패키지 이동
- [ ] **`:feature:setting`**: `app/.../ui/setting` 패키지 이동
- [ ] 각 feature 모듈에 `gyleedev.android.feature` 컨벤션 적용 및 `:domain`, `:core:designsystem` 의존성 주입

### Phase 5: `:app` 모듈 최종 통합 (Wiring)
- [ ] `MainActivity`, `MainViewModel`, `GithubSearchScreen` (Global Navigation) 확인
- [ ] `GithubSearchApplication` 설정 유지 및 앱 시작점 정비
- [ ] `:app` 모듈에서 모든 `:feature:*` 및 `:data` 의존성을 연결하여 최종 앱 빌드 확인

## 🧪 Verification & Testing
- 각 Phase 완료 후 `./gradlew assembleDebug` 및 `./gradlew spotlessApply` 수행
- 런타임 크래시가 발생하지 않도록 의존성 그래프와 Dagger Hilt 컴포넌트 유효성 검사

---
**Status**: 🚧 Phase 2 완료
