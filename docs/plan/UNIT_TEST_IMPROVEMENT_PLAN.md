# Unit Test Improvement Plan

이 문서는 프로젝트의 Unit Test 커버리지 및 신뢰도를 높이기 위해 추가로 구현할 테스트 시나리오와 보완 과제들을 정리합니다.

## ⏸️ 보류 및 논의 사항 (Pending Discussions)

1. **에러/예외 시나리오 (BaseViewModel.exceptionHandler)**
   - **현황**: 모든 ViewModel이 `BaseViewModel.exceptionHandler`를 통해 예외를 잡지만, 현재 실질적인 예외 복구나 UI 처리 로직이 없어 로깅/emit 수준에 머물러 있습니다.
   - **결정**: 실질적인 에러 핸들링 정책이 확립될 때까지 테스트 구현을 **보류**합니다.
2. **실패 분기 (Failure Results) 검증**
   - **현황**: `UpdateFavoriteResult.Failure`, `RevokeResult.Failure`, `ResetDataResult.Failure`, `UserUpdateResult.Failure` 등 UseCase가 실패를 반환했을 때 ViewModel이 이를 조용히 무시(Swallow)하는 상태입니다.
   - **결정**: 이것이 의도된 정책인지 구체적인 방향성을 **고민 중**이며, 정책이 확정된 후 테스트로 명세화할 예정입니다.

---

## 🚀 추가 구현 예정 (Action Items)

### 공통 과제 (Common)
- **Turbine 라이브러리 도입**
  - **목적**: `isLoading: false -> true -> false` 등과 같이 빠르게 변하는 중간 상태(Transient State)를 `awaitItem()`으로 순차적 검증하기 위함.
- **Paging Flow 상태 검증 고도화**
  - **목적**: `users`, `items` 플로우에 대해 컬렉터만 연결해두는 것을 넘어, 필터 변경 시 새로운 PagingData가 정상 발행되는지 확인 (`paging-testing` 라이브러리의 `asSnapshot()` 등 활용 검토).

### 1. HomeViewModelTest
- **다중 입력 타이밍 검증 (Debounce & FlatMapLatest)**
  - 검색어 "A" 입력 후 200ms 대기, 이어서 "AB" 입력 후 300ms 대기 시 "A"에 대한 호출은 취소되고 "AB"로만 API가 1회 호출되는지 검증.
- **연속 동일 입력 무시 (distinctUntilChanged)**
  - 동일한 쿼리가 연속으로 들어왔을 때 API가 중복 호출되지 않는지 검증.
- **상태 전이 캡처 (Transient State)**
  - 검색 요청 시 로딩이 시작(`true`)되고 종료(`false`)되는 전체 시퀀스를 Turbine으로 검증.
- **다이얼로그 닫기 상태 검증**
  - `showRequestAuthDialog = true` 상태에서 `changeDialogState(false)` 호출 시 정상적으로 닫히는지 확인.

### 2. FavoriteViewModelTest
- **가드 로직(Null 체크) 검증**
  - `focusedUser`가 `null`일 때 `updateFavoriteStatus`를 호출하면 내부 UseCase 로직이 무시되는지 검증.
- **시퀀스 연속성 검증**
  - 업데이트 성공 후 `focusedUser`가 다시 `null`로 초기화되고 다이얼로그가 `false`로 닫히는지 검증.
- **양방향 토글 검증**
  - `updateShowFavoriteDialog`가 `false -> true`뿐만 아니라 `true -> false`로도 정상 토글되는지 검증.
- **Enum 확장 대응 (Parameterized Test)**
  - `FilterStatus`의 모든 상태값 변이에 대해 상태가 업데이트되는지 파라미터화 테스트 작성 권장.

### 3. SettingViewModelTest
- **이벤트 누락 보완 (RESET & Else 분기)**
  - `SettingEvent.RESET` 호출 시 `showResetDialog` 토글 동작 검증.
  - 정의되지 않은 나머지 이벤트(`else -> {}`) 발생 시 기존 상태가 유지되는지(Negative Test) 검증.
- **명시적 상태 변경 (LOGIN/LOGOUT)**
  - 초기값 `false`에서 다시 `false`로 가는 무의미한 검증이 아닌, 상태를 먼저 `true`로 바꾼 후 이벤트 호출로 `false`로 닫히는 동작을 검증.
- **양방향 토글 정밀 검증 (THEME/LANGUAGE)**
  - `false -> true` 전환 이후 `true -> false` 전환 시나리오 추가 검증.
- **SharedFlow 지연 구독 이슈 문서화**
  - 단발성 이벤트 방출 시 구독 시점에 따른 유실 가능성 명시 및 검증.

### 4. DetailViewModelTest
- **Flow 다중 방출 시나리오**
  - 즐겨찾기 상태 변경 시 DB 연동 등으로 인해 `user` 플로우가 새로운 데이터로 연속 갱신(emit)될 때 UI 상태가 올바르게 업데이트되는지 검증.
- **가드 로직 방어 선행 조건**
  - ID 값이 있어도 아직 Flow를 통해 사용자 정보가 로딩되기 전(`null` 상태)일 때 즐겨찾기 업데이트를 시도하면 안전하게 무시되는지 확인.
