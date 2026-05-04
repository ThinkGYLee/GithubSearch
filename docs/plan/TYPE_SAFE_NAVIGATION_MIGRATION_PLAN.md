# Type-Safe Navigation 마이그레이션 전략

## 1. 플러그인 및 의존성 추가
* `libs.versions.toml`에 `kotlinx-serialization-json` 라이브러리와 `org.jetbrains.kotlin.plugin.serialization` 플러그인을 추가합니다.
* `app` 모듈 및 필요한 기능 모듈의 `build.gradle.kts` (또는 컨벤션 플러그인)에 해당 의존성을 적용합니다.

## 2. Type-Safe Route 정의 (경로 객체화)
* 기존의 `String` 상수(예: "home", "detail/{id}") 대신 `@Serializable` 어노테이션이 붙은 `Data Object` 또는 `Data Class`를 정의합니다.
* 예시: `data object Home`, `@Serializable data class Detail(val id: String)`

## 3. 네비게이션 그래프(GithubSearchScreen.kt) 리팩터링
* `NavHost` 내부의 `composable("route")` 블록을 `composable<RouteObject>` 형태로 변경합니다.
* 상세 화면으로 이동할 때 문자열 템플릿("detail/$id")을 사용하는 대신 인스턴스를 전달(`navController.navigate(Detail(id))`)하도록 수정합니다.

## 4. BottomNavigation (바텀 네비게이션) 로직 수정
* 현재 백스택 상태(`currentBackStackEntryAsState`)에서 현재 경로를 확인하는 방식을, Type-Safe 객체의 클래스 이름(`route::class.qualifiedName`) 등을 검사하는 방식으로 수정하여 호환성을 맞춥니다.
