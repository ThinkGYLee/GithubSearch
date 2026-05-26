# Liquid Navigation Padding & Insets Guide

본 문서는 GitHub Search 프로젝트에 적용된 **Liquid Navigation (반투명 Edge-to-Edge 네비게이션)** 효과를 구현하기 위한 Scaffold 및 Padding/WindowInsets 설정 방식을 설명합니다.

## 1. 개요 및 목적 (Why)

현재 프로젝트는 콘텐츠가 시스템 바(Status Bar, Navigation Bar) 및 앱의 네비게이션 바 영역 뒤로 비치면서 스크롤되는 트렌디한 시각적 효과(Cloudy Blur)를 제공합니다. 

일반적인 Material3 `Scaffold`의 기본 Insets 처리를 그대로 사용하면, 콘텐츠 영역이 화면 가장자리에서 밀려나(push) 네비게이션 바 뒤로 리스트가 지나가는 효과를 낼 수 없습니다. 따라서 시스템 UI 영역을 화면 그리기 영역으로 포함시키면서도, 실제 콘텐츠(버튼, 텍스트 등)는 시스템 UI에 가려지지 않도록 세밀한 패딩 전략이 필요합니다.

---

## 2. Scaffold 설정 (핵심 규칙)

가장 중요한 것은 컴포넌트 최상단이나 화면의 루트에 위치한 `Scaffold`의 기본 시스템 인셋 적용을 무력화하는 것입니다.

```kotlin
Scaffold(
    // 기본 시스템 바 패딩을 0으로 만들어 화면 전체(Full Screen)를 점유하도록 설정
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    // ...
) { paddingValues ->
    // ...
}
```

*   **이유**: 이 설정을 하지 않으면 `Scaffold`가 내부적으로 `navigationBars`, `statusBars` 영역을 계산하여 리스트 컴포저블을 뷰포트 바깥으로 밀어내게 되며, 결과적으로 투명한 바 뒤로 콘텐츠가 비치지 않게 됩니다.

---

## 3. Bottom Navigation 영역 처리 (`GithubSearchScreen` 기준)

하단 네비게이션 바가 리스트 콘텐츠 위를 떠다니는(Overlay) 형태로 구성되어야 합니다.

### 3.1. `bottomBar` 슬롯 미사용
*   `Scaffold(bottomBar = { ... })` 파라미터를 **사용하지 않습니다.** 
*   **이유**: `Scaffold`에 `bottomBar`를 등록하면, `Scaffold`가 `paddingValues`의 Bottom 영역에 바의 높이만큼 강제로 패딩을 삽입하여 리스트가 바 밑으로 그려지는 것을 원천 차단하기 때문입니다.

### 3.2. Box 오버레이 배치
*   콘텐츠(`NavHost`)와 네비게이션 바를 `Box` 내부에 배치하고, 네비게이션 바를 `Modifier.align(Alignment.BottomCenter)`로 콘텐츠 위에 띄웁니다.

### 3.3. 시스템 네비게이션 바 패딩
*   네비게이션 바 내부 디자인에서는 `cloudy` 블러 배경을 가진 부모 `Box` 아래에 시스템 바 높이만큼의 빈 공간을 두어, 시스템 네비게이션 영역까지 블러 처리를 연장시킵니다.
    ```kotlin
    Column {
        LiquidNavigationBar(...)
        // 시스템 네비게이션 바 영역도 투명하게 유지하여 리스트가 비치도록 함
        Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding())
    }
    ```

---

## 4. Top App Bar 투명도에 따른 적용 방식의 차이

TopBar가 블러 효과를 가지며 뒤가 비쳐야 하는 경우(`HomeScreen`)와, 투명 효과 없이 일반적인 불투명 TopBar를 사용하는 경우(`FavoriteScreen`)의 패딩 처리 방식이 다릅니다. 새로운 페이지를 만들 때 TopBar의 UI 요구사항에 맞게 아래 두 가지 방식 중 하나를 선택해야 합니다.

### 4.1. 투명/블러 효과가 필요한 경우 (예: `HomeScreen`)

이 방식에서는 콘텐츠가 스크롤될 때 TopBar 뒤로 비치며 넘어가야 합니다.
*   **TopBar UI 구성**: `statusBarsPadding()`을 내부에 적용하여 상태 표시줄과의 겹침을 방지합니다.
*   **패딩 적용 위치**: 콘텐츠가 TopBar 뒤로 스크롤되어야 하므로 컨테이너 자체가 아닌, 내부 스크롤 컴포넌트(예: `LazyColumn`)의 **`contentPadding`**을 통해 초기 여백만 확보합니다.
    ```kotlin
    // LazyColumn 내부에서 TopBar 높이만큼 패딩 처리
    LazyColumn(
        contentPadding = PaddingValues(top = paddingValues.calculateTopPadding() + 12.dp)
    )
    ```

### 4.2. 일반 불투명 TopBar인 경우 (예: `FavoriteScreen`)

이 방식에서는 콘텐츠가 TopBar 뒤로 비칠 필요가 없으며, TopBar 밑에서 딱 잘려야(클리핑) 합니다. 단, 하단의 Liquid Navigation을 위해 `contentWindowInsets`는 여전히 `0`으로 유지해야 합니다.
*   **TopBar UI 구성**: 기본 `TopAppBar` 등 제공되는 불투명 컴포넌트를 사용합니다. (Scaffold가 높이를 계산하여 `paddingValues`로 넘겨줍니다)
*   **패딩 적용 위치**: 리스트가 TopBar 뒤로 넘어가는 것을 막기 위해 `LazyColumn`의 속성이 아닌, 콘텐츠 전체를 감싸는 상위 레이아웃 컨테이너에 직접 **`Modifier.padding(top = ...)`**을 적용하여 그리기 영역 자체를 밀어냅니다.
    ```kotlin
    // 상위 컨테이너에 강제로 패딩을 주어 리스트가 TopBar 영역을 침범하지 못하게 함
    FavoriteScreen(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding()),
        // ...
    )
    ```

---

## 5. 하단 여유 공간 (Bottom Padding - 더미 아이템 방식)

하단 네비게이션은 `Scaffold` 밖에서 오버레이 되었으므로 TopBar 투명도 여부와 상관없이 모든 화면에서 동일하게 처리해야 합니다.

*   `Scaffold`의 `paddingValues` 하단 값을 쓸 수 없습니다.
*   대신 `LazyColumn`의 **제일 마지막 아이템으로 빈 `Box`를 추가**하여 마지막 리스트 아이템이 하단 네비게이션 바 위로 완전히 올라올 수 있도록 스크롤 여유 공간을 만듭니다.
    ```kotlin
    item {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // 시스템 UI 영역 확보
                .padding(bottom = LiquidNavBarDefaults.Height + LiquidNavBarDefaults.BottomMargin) // 커스텀 바 높이 및 마진 확보
        )
    }
    ```

---

## 요약
1. `Scaffold`의 `contentWindowInsets`를 `0`으로 비운다. (모든 화면 공통)
2. Bottom Navigation 영역은 Scaffold의 `bottomBar` 슬롯을 쓰지 않고 오버레이(`Alignment.BottomCenter`)로 처리한다.
3. 리스트 하단 여백은 마지막 빈 Dummy Item을 통해 스크롤 가능 영역을 늘려준다.
4. **TopBar 투명도에 따른 상단 패딩 처리 전략**:
    *   **투명/블러 (뒤가 비쳐야 할 때)**: 스크롤 컴포넌트의 `contentPadding`으로 상단 여백 확보.
    *   **불투명 (딱 잘려야 할 때)**: 상위 컨테이너에 `Modifier.padding(top = paddingValues.calculateTopPadding())`을 주어 렌더링 영역 자체를 제한.