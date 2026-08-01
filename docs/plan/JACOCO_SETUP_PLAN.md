# [고도화] JaCoCo 도입 및 테스트 자동화 파이프라인 수립 플랜

이 플랜은 `GithubSearch` 프로젝트의 테스트 신뢰성을 지표로 시각화하고, 고품질의 테스트 코드를 유지하기 위한 엄격한 기준과 자동화 환경을 구축하는 것을 목표로 합니다.

## 1. 목적
- **객관적 지표 확보**: JaCoCo를 통해 테스트 커버리지를 측정하고 시각화합니다.
- **테스트 품질 강화**: 한글 메서드 네이밍 및 Given-When-Then 구조를 통한 직관적인 테스트 코드를 작성합니다.
- **피드백 루프 자동화**: GitHub Actions를 통해 PR 단계에서 커버리지 리포트를 자동으로 제공합니다.

## 2. 주요 구성 요소
- **JaCoCo**: 코드 커버리지 측정 엔진.
- **GitHub Actions**: CI 파이프라인 (테스트 및 리포트 생성).
- **Madrapps/jacoco-report**: PR 코멘트 자동화 도구.
- **Testing Convention**: 한글 네이밍 및 G-W-T 구조.

## 3. 상세 구현 단계

### 1단계: 인프라 구축 (JaCoCo 플러그인 및 설정)
- **버전 관리**: `libs.versions.toml`에 JaCoCo 버전을 추가합니다.
- **Convention Plugin (`build-logic`)**:
    - `AndroidJacocoConventionPlugin.kt`: 모든 안드로이드 모듈에 적용할 공통 JaCoCo 설정을 정의합니다.
    - `jacocoTestReport` 태스크 정의: XML 및 HTML 리포트 생성을 설정합니다.
- **Exclusion 리스트 등록**: 
    - UI 관련: `**/*Activity*.*`, `**/*Fragment*.*`, `**/*Screen*.*` (Compose)
    - DI/Generated: `**/*Hilt*.*`, `**/Dagger*.*`, `**/*_Factory.*`, `**/*_MembersInjector.*`
    - 기타: `**/BuildConfig.*`, `**/Manifest*.*`, `**/*_Impl*.*` (Room)

### 2단계: 테스트 코드 표준화 (Given-When-Then & 한글 네이밍)
- **네이밍 규칙**: 테스트 의도를 직관적으로 파악할 수 있도록 **한글 공백 네이밍**을 사용합니다.
    - 예: `` fun `사용자가 검색어를 입력하면 올바른 결과를 반환한다`() ``
- **구조화**: 모든 테스트 메서드 내에 `// Given`, `// When`, `// Then` 주석을 명시하여 논리적 단계를 구분합니다.
- **대상**: ViewModel, Repository, UseCase 등 비즈니스 로직이 포함된 모든 클래스.

### 3단계: CI 파이프라인 자동화 (GitHub Actions)
- **Workflow 작성**: `.github/workflows/jacoco-report.yml`
    - 트리거: `pull_request` (target: main, develop)
    - 명령어: `./gradlew testDebugUnitTest jacocoFullReport` (통합 리포트 생성)
- **GitHub Pages 배포**: 기본 통합 브랜치인 `develop` 머지 시 전체 커버리지 HTML 리포트를 배포합니다. 하나의 Pages 사이트를 `main`과 공유하지 않아 어느 브랜치의 리포트인지 혼동되지 않게 합니다.

### 4단계: PR Comment 피드백 자동화
- **Madrapps/jacoco-report 액션 연동**:
    - JaCoCo가 생성한 XML 리포트를 분석합니다.
    - PR 댓글로 전체 커버리지 요약(%)과 파일별 커버리지 변화를 리포팅합니다.
    - 설정된 기준(초기 0%로 설정하여 도입 후 점진적으로 상향) 미달 시 빌드 실패 처리를 검토합니다.

## 4. 기대 효과 및 향후 관리
- **가시성**: 코드 변경이 테스트 커버리지에 미치는 영향을 즉각적으로 확인.
- **유지보수성**: 한글 네이밍과 구조화된 테스트를 통해 누구나 쉽게 테스트 코드를 읽고 수정 가능.
- **품질 유지**: 자동화된 검증 단계를 통해 테스트 없는 코드의 머지를 방지.

## 5. 제외 패턴 (Exclusions) 상세 목록
```kotlin
val fileFilter = mutableListOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/*$Lambda$*.*",
    "**/*$ExternalSynthetic$*.*",
    "**/*$TypeAdapter$*.*",
    "**/*Hilt*.*",
    "**/Dagger*.*",
    "**/*_Factory.*",
    "**/*_MembersInjector.*",
    "**/*_Impl*.*",
    "**/*Binding.*"
)
```
