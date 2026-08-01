# Navigation Contracts

## 목적과 적용 범위

이 문서는 현재 `GithubSearch` 구현에 존재하는 Compose navigation 계약을 기록하는 정본이다. route, argument, 호출자와 목적지 호환성, back stack, 상태 복원, 하단 navigation 표시 규칙을 변경하기 전에 이 문서와 현재 코드를 함께 확인한다.

이 문서는 type-safe navigation 전환을 지시하지 않는다. 해당 전환은 아직 계획 상태인 `docs/plan/TYPE_SAFE_NAVIGATION_MIGRATION_PLAN.md`의 별도 위험 변경이다.

## 현재 route와 argument 계약

| 대상 | route 계약 | argument | 현재 호출자 |
| --- | --- | --- | --- |
| Home | `HOME` | 없음 | `NavHost`의 start destination, 하단 navigation |
| Favorite | `FAVORITE` | 없음 | 하단 navigation |
| Setting | `SETTING` | 없음 | 하단 navigation |
| Detail | `DETAIL/{id}?from={from}` | `id`: 필수 String, `from`: 필수 String이며 기본값은 빈 문자열 | Home 일반 목록, Home 검색 결과, Favorite |

- route 문자열은 `app/src/main/java/com/gyleedev/githubsearch/ui/Const.kt`에 선언되고 `BottomNavItem`이 이를 화면 route로 노출한다.
- `DetailViewModel`은 `SavedStateHandle`의 `id`를 읽어 사용자·repository 로딩을 시작한다. 따라서 Detail 호출자는 비어 있지 않은 `id`를 전달해야 한다.
- `from`은 `DetailScreen`에 그대로 전달되는 출발 화면 구분 값이다. 현재 값은 Home 일반 목록의 `home`, Home 검색 결과의 `search`, Favorite의 `favorite`이며, argument가 없는 호환 호출에는 기본값 `""`이 사용된다.

## 호출·back stack·상태 계약

- Detail의 세 호출자는 모두 `GithubSearchScreen`에 있으며, 목적지 route 형식과 `from` 값을 함께 결정한다. 호출자 또는 값 변경은 Detail 화면의 화면 전환·표시 동작을 함께 확인해야 한다.
- Detail의 뒤로 가기는 `navController.navigateUp()`을 사용한다. 이 동작은 현재 back stack에 의존하므로 route를 직접 진입시키거나 호출 구조를 바꿀 때 별도 검증이 필요하다.
- 하단 navigation은 Home·Favorite·Setting만 노출한다. 현재 destination route가 `DETAIL`로 시작하면 하단 바를 숨긴다.
- 하단 navigation 전환은 start destination까지 `popUpTo`하면서 `saveState = true`를 사용하고, `launchSingleTop = true`, `restoreState = true`를 사용한다. 따라서 탭 route나 start destination을 바꾸면 중복 destination과 탭별 상태 복원을 함께 검증해야 한다.
- Detail의 `id`는 `SavedStateHandle`로 복원된다. 반면 `from`은 현재 `DetailScreen`의 navigation argument에서 직접 읽으므로 두 argument의 전달·복원 경로를 분리해 확인한다.

## Deep link와 테스트 현황

- 현재 `app`과 `feature`의 navigation 선언에는 `deepLink` 또는 `deepLinks` 사용이 없다. deep link 도입·변경은 새 외부 진입 계약이므로 위험 변경으로 다룬다.
- 현재 unit test는 `feature/detail/src/test/java/com/gyleedev/githubsearch/feature/detail/DetailViewModelTest.kt`에서 `SavedStateHandle`의 `id` 전달 여부를 확인한다. route 조립, `from` 값, `navigateUp()`, 하단 navigation의 상태 복원을 직접 검증하는 navigation test는 현재 검색 범위에서 찾지 못했다.

## 위험 변경과 최소 검증

다음 변경은 `docs/ai/task-scope-control.md`의 위험 변경 절차를 먼저 적용한다.

- route 제거·이름 변경, Detail argument의 이름·필수성·기본값 변경
- `from` 값 또는 해석 변경, 호출자와 목적지의 호환성 제거
- deep link 추가·변경, back stack·탭 상태 복원 규칙 변경
- navigation 관련 public API, module dependency, Gradle dependency, DI 변경

최소 확인은 변경된 각 호출자에서 Detail route와 argument를 확인하고, 목적지에서 argument 수신과 뒤로 가기 동작을 확인하는 것이다. 탭 navigation을 건드리면 중복 화면 방지와 상태 복원까지 확인한다. 코드 변경이면 `docs/ai/quality-gates.md`에 따라 가장 좁은 관련 Gradle 검증을 선택하고, navigation behavior test의 필요성은 `test-strategy-reviewer`와 함께 판단한다.

## 검토 역할 사용 규칙

`navigation-contract-reviewer`는 review-first 역할이다. 결론 전 이 문서 같은 관련 정본 문서와 현재 영향 코드 또는 테스트를 읽고, 결과에 두 근거 경로를 모두 남긴다. 파일 수정은 주 작업자가 명시한 제한된 write 범위에서만 가능하며, 최종 적용·위험 판단·검증·보고 책임은 주 작업자에게 남는다.
