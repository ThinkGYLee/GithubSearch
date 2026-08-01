# Code Review 기준과 Hook 경계

이 문서는 GithubSearch의 custom reviewer, Codex `/review`, 사람 PR review가 공유하는 결과 기준이다. 코드 작성 규칙은 `android-coding-conventions.md`, 검증 명령은 `quality-gates.md`, 위험 범위는 `task-scope-control.md`를 따른다.

## Review 시작 전 범위 선언

review 요청은 아래 중 하나로 범위를 명시한다. 범위가 없으면 reviewer는 추측으로 전체 repository를 검토하지 않고 필요한 base 또는 diff를 요청한다.

- base branch 대비 현재 diff
- 특정 commit
- 현재 working tree의 unstaged/staged diff
- 요청자가 지정한 파일 또는 동작

reviewer는 관련 primary document와 영향을 받는 코드 또는 테스트를 함께 읽고, 두 경로를 결과에 남긴다.

## Finding 우선순위

| 심각도 | 의미 | 예시 |
| --- | --- | --- |
| P0 | merge 전 반드시 막아야 하는 문제 | 데이터 손상·secret 노출·앱 실행 불가·보안 우회 |
| P1 | 다음 수정에서 반드시 해결해야 할 실제 동작 회귀 | navigation 계약 위반·잘못된 상태 전이·crash 가능성·API 계약 불일치 |
| P2 | 배포를 막지는 않지만 근거가 있는 품질·검증 공백 | 재현 가능한 접근성 문제·필요한 test 누락·명확한 유지보수 위험 |

단순 코드 스타일 선호, 대안 구현 취향, 현재 규칙으로 뒷받침되지 않는 제안은 finding으로 보고하지 않는다. 필요하면 `non-blocking note` 또는 `question`으로 구분한다.

## Finding 형식

각 finding에는 아래를 모두 포함한다.

```text
Severity: P0 | P1 | P2
Impact: 사용자·데이터·보안·계약에 미치는 실제 영향
Evidence: 재현 조건 또는 관련 문서·코드·테스트 경로
Affected path: <file path>
Minimum action: 가장 작은 수정 또는 검증 방법
```

- 실제 회귀를 재현할 수 없고 근거도 부족하면 `insufficient context`로 보고한다.
- 기존 동작이 의도된 것으로 확인되면 finding을 철회하고 이유를 남긴다.
- test gap은 기능 결함으로 단정하지 않는다. 어떤 동작이 검증되지 않았는지, 우선순위와 최소 검증 방법을 P2 또는 follow-up으로 기록한다.

## 공통 결과 형식

custom reviewer와 사람 또는 `/review` 결과는 다음 정보를 같은 순서로 제공한다.

```text
Verdict: pass | finding | insufficient context
Review scope: base branch | commit | working-tree diff | requested files
Primary-document evidence: <path(s)>
Code/test evidence: <path(s)>
Findings: <P0/P1/P2 finding(s), or none>
Verification or follow-up: <minimum action>
Uncertainty: <none or missing evidence>
```

custom reviewer는 read-only이며 결과가 `pass`여도 main agent 또는 사람이 최종 적용·검증·merge 판단을 소유한다.

## Lifecycle Hook·Git Hook·CI 경계

| 계층 | 책임 | 이 Phase의 상태 |
| --- | --- | --- |
| Git pre-commit hook | staged 지식 변경, resource lint처럼 commit 시점에 빠르게 확인 가능한 항목 | 유지 |
| Knowledge CI | PR에서 Index·graph 관계를 독립적으로 확인 | 유지 |
| Build CI | compile·lint·test와 job별 권한 검증 | 유지 |
| Codex lifecycle hook | 세션·도구 사용 주기의 보조 안내 또는 제한 | 이번 Phase에서는 추가하지 않음 |

Codex lifecycle hook은 같은 검사를 반복하는 수단이 아니다. formatter, Index, graph verifier처럼 이미 Git hook 또는 CI가 강제하는 검사를 lifecycle hook으로 중복하지 않는다.

`SessionStart` 또는 `Stop` hook은 실제 작업 이력에서 같은 누락이 반복될 때만 별도 Small/Medium 작업으로 제안한다. 도입 시에는 hook의 신뢰 검토, timeout, 실행 범위, secret 출력 금지, 파일·stage·commit 미수정, rollback을 명시한다.

## 대표 확인과 후속 회고

Phase 1에서 `compose-ui-reviewer`가 문서 근거, 코드 근거, finding, 최소 후속 조치를 반환한 runtime 확인 결과를 이 공통 형식의 대표 사례로 사용한다. Phase 5에서는 실제 PR review 3~5건을 다시 살펴 severity 일관성, false-positive 비율, test-gap 품질, lifecycle hook 필요성을 회고한다.
