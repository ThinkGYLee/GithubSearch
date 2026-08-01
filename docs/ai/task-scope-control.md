# Task Scope Control

이 문서는 현재 repository에 적용된 작업 범위 제한 규칙이다. Codex가 요청보다 변경 범위를 과하게 넓히지 않도록 작업 크기와 위험 변경 기준을 정의한다. 검증 명령 선택은 `docs/ai/quality-gates.md`, 최종 응답 전체 형식은 `docs/ai/change-report-template.md`가 담당한다.

Template metadata는 `docs/ai/README.md`를 따른다.

## 작업 크기 분류

### Small Task

- 1~3개 파일 정도의 제한된 변경.
- 단일 버그 수정.
- 단일 Composable 개선.
- 단일 ViewModel 로직 일부 수정.
- 문서 일부 수정.
- 기본적으로 바로 수정 가능하다.

### Medium Task

- 4~8개 파일 정도의 변경.
- 여러 모듈에 걸친 작은 변경.
- UI와 ViewModel이 함께 바뀌는 작업.
- UseCase와 Repository interface가 함께 바뀌는 작업.
- core docs safe sync처럼 여러 문서를 추가하되 기존 의미를 바꾸지 않는 작업.
- 수정 전 짧은 계획을 제시한다.

### Large Task

- 9개 이상 파일 변경 가능성이 있는 작업.
- 아키텍처 구조 변경.
- 모듈 구조 변경.
- DB schema 변경.
- navigation 구조 변경.
- Gradle / build-logic 대규모 변경.
- public API 변경.
- 기본적으로 바로 수정하지 말고 계획만 제시한다.

## 위험 변경 규칙

아래 변경은 Risky Change로 간주한다.

- Room Entity 변경.
- Database version 변경.
- Migration 추가 또는 수정.
- Navigation route 변경.
- public API 변경.
- 모듈 dependency 변경.
- Gradle plugin 또는 dependency version 변경.
- DI graph 구조 변경.
- 대규모 rename 또는 move.
- 삭제 작업.
- generated file 수정.
- signing, keystore, `local.properties`, secrets 관련 파일 수정.

Risky Change는 바로 수정하지 않는다. 먼저 영향 범위와 위험도를 설명하고, 가능한 대안을 제시한 뒤, 사용자가 명확히 적용을 요청한 경우에만 수정한다.

## 파일 수 제한

- Small Task는 최대 3개 파일 변경을 목표로 한다.
- Medium Task는 최대 8개 파일 변경을 목표로 한다.
- Large Task는 바로 수정하지 않고 계획만 제시한다.
- 제한을 넘겨야 하면 먼저 이유를 설명한다.

## 작업 중단 조건

Codex는 아래 상황에서 멈추고 보고한다.

- 요청보다 변경 범위가 커지는 경우.
- migration이 필요한 경우.
- 새 의존성 추가가 필요한 경우.
- 모듈 dependency 변경이 필요한 경우.
- 기존 테스트가 대량으로 실패하는 경우.
- 원인이 현재 작업과 무관한 것으로 보이는 경우.
- 보안/비밀 파일을 건드려야 하는 경우.
- 기존 작업트리 변경을 덮어쓰거나 되돌려야 하는 경우.

## Task Scope Section

최종 응답의 Task Scope 섹션에는 아래 내용을 포함한다.

```text
Task Scope:
- Size: <Small / Medium / Large>
- Files changed: <number>
- Risky change: <yes / no>
- Notes: <short explanation>
```
