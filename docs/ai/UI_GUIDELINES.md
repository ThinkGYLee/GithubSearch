# 🖼️ UI_GUIDELINES.md

## 🎯 Mission
GitHub Search의 UI는 사용자에게 직관적이고 효율적인 검색 경험을 제공합니다. Jetpack Compose와 최신 Material 3 스타일을 기반으로 현대적인 안드로이드 UI 표준을 정립합니다.

---

## 🔗 Core Docs Reference

일반 naming, Preview suffix, 작업 범위, 검증, 최종 보고 형식은 core docs를 source of truth로 둡니다. 이 문서는 GithubSearch의 Compose/UI 특화 규칙만 보존합니다.

* **Kotlin/Android UI Convention**: `docs/ai/android-coding-conventions.md`
* **State / Flow Detail**: `docs/ai/DATA_LOGIC_GUIDELINES.md`
* **Task Scope / Risky Change**: `docs/ai/task-scope-control.md`
* **Verification**: `docs/ai/quality-gates.md`
* **Final Report Format**: `docs/ai/change-report-template.md`

---

## 🏗️ Compose Core Principles

### 1. Structure & Reusability
* **Naming**: 일반 Composable naming과 Preview suffix 규칙은 `docs/ai/android-coding-conventions.md`를 따릅니다.
* **Modifier First**: `modifier: Modifier = Modifier`를 첫 번째 선택 파라미터로 배치하여 레이아웃 조정의 유연성을 확보합니다.
* **Separation**: 단일 Composable 함수가 100줄을 초과할 경우, 논리적 단위(Header, Content, Footer 등)로 분리하여 컴포넌트화합니다.

### 2. State & Lifecycle
* **State Hoisting**: UI 상태는 ViewModel에서 관리하는 단일 `UiState`를 구독하여 처리하며, 하위 Composable은 무상태(Stateless)를 지향하고 UI event를 상위로 올립니다.
* **Stability & Reusability**:
    - **No ViewModel in Sub-Composables**: Screen 하위의 서브 컴포저블에는 **ViewModel을 직접 전달하지 않습니다.**
    - **Stable Parameter Only**: 하위 컴포저블은 원시 타입, `@Stable`한 도메인 모델, 또는 상태 변경을 위한 람다(`() -> Unit`)만을 파라미터로 받도록 설계하여 불필요한 리컴포지션을 방지합니다.
* **Preview**: 모든 UI 컴포넌트와 화면은 `MaterialTheme`가 적용된 Light/Dark 모드 프리뷰를 반드시 포함합니다.

---

## 🚀 Advanced Tech Standards

### 1. Material 3 Adaptive & Layout
* **Adaptive Layout**: `ListDetailPaneScaffold` 등 Adaptive 라이브러리를 적극 활용하여 스마트폰, 폴더블, 태블릿 등 다양한 화면 크기에 최적화된 레이아웃을 구현합니다.
* **Expressive Design**: M3 원칙에 따라 생동감 있는 색상 대비와 역동적인 모션을 UI에 반영합니다.
* **WindowInsets Management (Edge-to-Edge)**:
    - 앱 상단에 `Box(modifier = Modifier.statusBarsPadding())` 처럼 이미 상태바 패딩을 처리하는 컨테이너가 있는 경우, 그 내부에 배치되는 Material 3 컴포넌트(`CenterAlignedTopAppBar`, `SearchBar` 등)는 기본적으로 상태바 크기만큼의 Inset 패딩을 **중복**으로 더하게 됩니다.
    - 이러한 중복 패딩(Double Padding)을 방지하려면 내부 M3 컴포넌트에 명시적으로 `windowInsets = WindowInsets(0, 0, 0, 0)`을 설정해야 합니다.
    - 배경을 시스템바 위로 확장하면서 내용은 안전 영역에 배치해야 할 경우, `SearchBar` 애니메이션 등에 의존하기보다는 최상단 컨테이너에서 배경(Box)만 별도로 칠하고 내용물은 `statusBarsPadding()` 하위에 배치하는 구조가 안정적입니다.

---

## 🌗 Material 3 Styling
* **Theme Tokens**: 하드코딩된 색상 대신 `MaterialTheme.colorScheme`의 토큰을 활용합니다.
* **Typography**: M3의 의미적 타이포그래피(`Display`, `Headline`, `Title` 등) 체계를 준수합니다.

---

## ✅ Compliance Checklist
1. `modifier`가 외부 주입 가능한 구조인가?
2. Adaptive 레이아웃을 고려하여 설계되었는가?
3. Light/Dark 모드 프리뷰가 모두 존재하는가?
