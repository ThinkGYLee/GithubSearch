---
id: GS-2026-0016
status: completed
base_commit: fb6d8af
related_documents: [liquid-navigation-ui-follow-up-plan, ui-guidelines, quality-gates, knowledge-operations, history-readme]
related_code: [feature/setting, app/src/main/java/com/gyleedev/githubsearch/ui]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# Liquid Navigation Setting debug 토글 fixture

## 요청

- Liquid Navigation overlay 검증을 위해 Setting 화면에 간단한 토글을 임시로 추가하고, 검증이 끝나면 제거한다.

## 근거

- Setting은 현재 고정 `Column`으로 구성돼 있으며, Overlay navigation 아래에서 긴 내용·큰 글꼴·가로 화면의 마지막 행과 Snackbar를 확인할 검증용 항목이 부족하다.
- 제품 기능처럼 보이는 임의 설정이나 로그인 사용자·프로젝트 URL을 전제로 한 링크는 추가하지 않는다.

## 결정

- 토글은 `BuildConfig.DEBUG`에서만 표시하며, 값은 `remember` 상태로만 보관한다.
- 토글은 앱 동작·OAuth·DB·사용자 설정을 변경하지 않으며, 검증 완료 후 fixture와 토글 row 모델을 제거한다.

## 변경

- `BuildConfig.DEBUG` 전용 6개 local-only 토글을 Setting 화면에 일시 추가했다.
- Setting 본문을 세로 스크롤 가능하게 만들고, 마지막 여백에 Liquid Navigation·system navigation bar 높이를 확보했다.
- Setting Snackbar를 Liquid Navigation 위에 표시하도록 같은 하단 여백을 적용했다.
- 검증 완료 후 debug 토글·문자열·row 모델을 제거했다. release와 debug 빌드 모두에 검증용 UI가 남지 않는다.

## 검증

- `./gradlew spotlessApply :feature:setting:compileDebugKotlin :feature:setting:testDebugUnitTest`: 통과.
- `./gradlew spotlessApply :app:installDebug`: 통과, 연결된 Android 기기와 emulator에 설치 완료.
- emulator portrait에서 fixture의 마지막 토글 두 개가 기존에는 Liquid Navigation 뒤에 가려지는 것을 확인했다.
- 스크롤·하단 여백 보정 뒤 마지막 토글까지 스크롤해 Liquid Navigation 위에서 완전히 보이는 것을 확인했다.

## 지시 이행

- fulfilled: 임시 토글로 Setting overlay 문제를 재현하고, 확인 뒤 fixture를 제거했다.
- fulfilled: 실제 사용자 설정·OAuth·DB·navigation route는 변경하지 않았다.

## 스킬 평가

- `project-knowledge-grounding`: Setting의 실제 row 모델·Scaffold·overlay 구조와 기존 Liquid Navigation 문서를 연결하는 데 사용했다.
- `session-retrospective`: fixture가 source에 남지 않았는지와 검증 결과 기록을 점검하는 데 사용한다.

## 개선 후보

- 큰 글꼴·landscape의 추가 수동 확인과 Snackbar 실제 표시 확인은 다음 UI 검증 작업에서 보완한다.
