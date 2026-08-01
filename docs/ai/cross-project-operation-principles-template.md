# Cross-Project Codex 운영 원칙 템플릿

> **Status:** provisional — GithubSearch의 Phase 0~2 실행 결과를 바탕으로 만든 초안이다. Phase 3~5의 실제 운영 결과를 반영해 확정·분리·축소한다.
>
> **Purpose:** Android/Kotlin 프로젝트를 포함한 다른 repository에 Codex 운영 체계를 도입할 때, 그대로 복사할 규칙과 프로젝트별로 다시 판단할 설정을 구분한다.

## 사용 방법

이 문서는 다른 repository의 `AGENTS.md`, `docs/ai/`, CI workflow를 만들기 전의 설계 기준이다. project-specific 값, action, branch 이름, Gradle task를 그대로 복사하지 않는다.

1. 현재 repository의 코드·CI·secret 입력·worktree 동작을 먼저 조사한다.
2. 아래 공통 원칙 중 적용할 항목을 고르고, project-specific 매핑 표를 작성한다.
3. 권한, secret, 배포, branch protection 변경은 별도 승인과 원격 CI 검증을 거친다.
4. 도입 결과와 예외는 작업 이력에 남긴다.

## 공통 원칙

### 1. 문서와 자동 강제의 책임을 분리한다

- `AGENTS.md`와 문서는 사람이 판단할 기준, 역할, 예외를 설명한다.
- CI, Git hook, agent sandbox는 기계적으로 확인 가능한 최소 규칙만 강제한다.
- 같은 formatter, 문서 관계 검사, test를 여러 계층에서 중복 실행하지 않는다.

### 2. 권한은 실행 단위별로 최소화한다

- build·lint·test job은 source를 읽는 데 필요한 권한만 가진다.
- PR 댓글, release, deployment, repository write는 별도 job으로 분리하고 필요한 권한만 부여한다.
- build output은 artifact로 전달하고, 쓰기 권한 job이 PR source를 checkout하거나 임의 build command를 실행하지 않도록 설계한다.

### 3. PR 검증 입력으로 production secret을 사용하지 않는다

- compile·test에 값의 존재만 필요하다면 non-secret placeholder 또는 test fixture를 사용한다.
- 실제 credential이 필요한 통합 검증은 신뢰된 환경, protected branch, 또는 사용자가 안전하게 준비한 Local 환경으로 분리한다.
- `pull_request_target`로 PR source에 secret 접근 권한을 주어 이 문제를 우회하지 않는다.
- secret은 문서, agent 결과, build log, artifact, worktree 복사 대상에 기록하지 않는다.

### 4. 검토와 수정을 분리한다

- reviewer는 기본 read-only로 두고, 근거·영향·최소 후속 조치만 반환한다.
- 실제 수정은 main agent 또는 명시적인 write scope를 받은 담당자가 수행한다.
- 독립적인 read-heavy 조사만 제한적으로 병렬화하고, 같은 동작이나 파일을 바꾸는 작업은 write 담당자 한 명이 소유한다.

### 5. worktree는 재현 가능한 범위로 제한한다

- ignore된 `local.properties`, keystore, token, 개인 SDK 설정은 기본적으로 worktree에 복사하지 않는다.
- secret 없이 가능한 문서·compile·unit test와 Local 전용 기기·OAuth 검증을 구분한다.
- local 설정을 공유해야 한다면 SDK 위치와 credential을 분리할 수 있는지 먼저 검토한다.

### 6. 보고와 작업 이력을 결과 중심으로 남긴다

- Medium 이상 작업과 policy·agent·CI 변경은 요청, 근거, 결정, 변경, 검증, 남은 위험을 기록한다.
- 최종 보고는 수정 사항, 실행한 검증, 실행하지 못한 검증, 다음 승인 또는 운영 항목을 구분한다.
- 원문 대화, 내부 추론, secret, token, 개인 정보는 이력에 기록하지 않는다.

### 7. 단계적으로 도입하고 원격에서 확인한다

- policy 확정, agent 권한, CI 권한, worktree, review 기준, 운영 정착을 작은 phase로 나눈다.
- 각 phase는 local 검증과 원격 CI 확인을 분리하고, 실패 시 secret 재노출 대신 설계를 조정한다.
- branch protection, required status, deployment environment는 저장소 관리자가 원격 실행을 확인한 후 적용한다.

## 프로젝트별 조정 표

| 영역 | 반드시 조사할 항목 | 예시 결정 |
| --- | --- | --- |
| Build 입력 | 빌드가 요구하는 값, 실제 네트워크 호출 여부 | placeholder 가능 여부, test fixture 분리 |
| CI 검증 | 모듈 구조, Gradle task, 테스트 종류 | 최소 local command와 CI full command |
| GitHub 권한 | checkout, PR comment, release, Pages·배포 여부 | job별 `permissions`와 실행 조건 |
| Secret | credential 종류, 전달 경로, artifact/log 노출 | PR 미전달, trusted 환경 분리 |
| Branch | default/protected branch와 release branch | PR base, deploy push branch |
| Agent | 읽기 검토 역할, write 담당, 병렬 가능 범위 | reviewer sandbox와 동시성 제한 |
| Worktree | ignore 파일, SDK, device, signing 요구 | Local handoff 조건 |
| 보고 | 문서 registry, history, Change Report 형식 | 필수 검증·위험 기록 방식 |

## 도입 전 체크리스트

- [ ] 이 repository에서 실제 secret이 필요한 검증과 placeholder로 가능한 검증을 구분했다.
- [ ] PR source가 실행되는 job의 권한과 secret 입력을 확인했다.
- [ ] comment·deploy·release 권한을 build job과 분리할 수 있는지 확인했다.
- [ ] worktree가 복사하지 말아야 할 ignore 파일과 Local 전용 검증을 확인했다.
- [ ] project-specific Gradle task, branch, action, artifact 이름을 새로 정했다.
- [ ] 저장소 관리자 승인과 원격 CI 확인이 필요한 변경을 분리했다.

## GithubSearch에서 검증할 후속 보완

이 초안은 Phase 3~5 완료 후 아래를 재검토한다.

- worktree 정책이 실제 신규 worktree와 Local handoff에서 충분한지
- 공통 review 형식이 custom reviewer와 사람 review에서 모두 유효한지
- Phase 2 artifact 전달과 최소 권한 job이 원격 PR·push에서 안정적으로 동작하는지
- 반복 작업에서 이력·검증·subagent 정책이 과도한지 또는 부족한지
- 독립 문서, skill, bootstrap template 중 어디에 어떤 수준으로 승격할지

## 이 템플릿에 포함하지 않는 것

- 특정 OAuth provider의 key 이름이나 credential 값
- 특정 GitHub Action의 버전·PR comment 형식·coverage 도구
- 특정 Android 모듈 구조·Gradle task·SDK 버전
- 개인 모델, reasoning, connector, 승인 설정

이 값들은 해당 repository의 코드와 운영 환경을 조사한 뒤 project-specific 문서에 기록한다.
