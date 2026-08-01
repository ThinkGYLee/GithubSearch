# Codex 운영 정책 보강 명세

> **Status:** active — Phase 0~4 completed, Phase 5 in progress
>
> **Purpose:** GithubSearch의 Codex 운영 정책을 공식 Codex 가이드의 durable guidance, bounded subagent, 최소 권한, worktree 안전성 원칙에 맞춰 단계적으로 보강한다.
>
> **Scope:** custom agent, project Codex 설정, review 기준, GitHub Actions 권한, worktree 운영 문서. 앱 기능, navigation 동작, Gradle 의존성, OAuth 동작은 이 명세만으로 변경하지 않는다.

## 운영 상태와 책임 분리

이 명세에서 **완료된 Phase 0의 결정**과 **아직 적용하지 않은 후속 phase의 구현**을 구분한다. 현재 runtime 규칙은 `AGENTS.md`, `docs/ai/subagents.md`, `.codex/agents/*.toml`이 정본이며, Phase 1~4의 항목은 해당 phase가 완료될 때까지 현재 동작을 바꾸지 않는다.

| 책임 영역 | 정본·담당 | 현재 상태 | 다음 변경 phase |
| --- | --- | --- | --- |
| 작업 범위·최종 판단·보고 | main agent, `AGENTS.md`, `knowledge-operations.md` | 운영 중 | 유지 |
| reviewer 역할·근거·write 권한 | `.codex/agents/*.toml`, `subagents.md` | hard read-only 설정과 runtime 결과 형식 확인 완료 | 유지 |
| Codex session 기본값 | 사용자 전역 config, project `.codex/config.toml` | reviewer 최대 병렬 수 3 적용 | 유지 |
| staged 변경 검사 | `.githooks/pre-commit` | 운영 중, clone/worktree별 설치 필요 | 유지 |
| 문서·agent 지식 관계 | registry, generated Index, `knowledge.yml` | 운영 중, read-only CI | 유지 |
| Android build·coverage·배포 권한 | `build.yml`, 저장소 관리자 | verify/comment/deploy 분리, 원격 PR 검증 완료 | Phase 5에서 required status 확인 |
| worktree local 설정·기기 검증 | `worktree-policy.md`, 사용자 Local 환경 | secret 미복사·Local handoff 경계 문서화 | Phase 5에서 반복 적용 회고 |
| branch protection·required status | 저장소 관리자 | `develop` branch protection·required status 미설정 확인 | Phase 5 관리자 설정 대기 |

이 분리는 다음 경계를 고정한다.

- main agent와 reviewer의 책임은 분리한다. reviewer는 hard read-only이며, main agent가 구현·write scope·최종 적용·검증을 계속 소유한다.
- project repository는 팀 공통 불변값만 보관한다. 모델·reasoning·개인 connector·승인 설정은 사용자 전역 config에 남긴다.
- Git hook은 commit 시점, `knowledge.yml`은 PR 시점, 향후 Codex lifecycle hook은 세션 시점을 담당한다. 동일 검사를 여러 계층에 복제하지 않는다.
- CI 권한, OAuth secret, Pages 배포, branch protection은 repository 관리자와 명시적 Risky Change 승인 없이는 변경하지 않는다.

## 근거와 운영 원칙

- Codex는 repository 규칙을 `AGENTS.md`에 두고, 반복 작업은 skill·hook·CI로 옮기며, 독립적인 읽기 작업만 subagent로 병렬화할 것을 권장한다. [Best practices](https://learn.chatgpt.com/guides/best-practices.md)
- custom agent는 프로젝트 `.codex/agents/`에서 역할별 지시와 sandbox를 가질 수 있다. 읽기 중심 검토는 read-only agent가 적합하며, 병렬 쓰기는 충돌 위험 때문에 피한다. [Subagents](https://learn.chatgpt.com/docs/agent-configuration/subagents.md)
- Codex worktree는 ignore 파일을 기본 복사하지 않는다. 민감한 local 설정을 복사하기 전에 명시적인 보안 판단이 필요하다. [Worktrees](https://learn.chatgpt.com/docs/environments/git-worktrees.md)
- Codex hook은 세션과 도구 사용 주기에 연결되고, Git hook은 commit 시점 검사다. 두 수단은 목적이 다르므로 같은 검사를 중복시키지 않는다. [Hooks](https://learn.chatgpt.com/docs/hooks.md)

이 명세의 기본 원칙은 다음과 같다.

1. **정책과 강제를 분리한다.** `AGENTS.md`와 문서는 판단 기준을, agent sandbox·Git hook·CI는 기계적으로 확인 가능한 최소 규칙을 담당한다.
2. **권한은 가장 작게 부여한다.** 검토 agent와 검증 CI는 read-only를 기본으로 하고, 쓰기·배포·PR 댓글 권한은 필요한 분리 job에만 둔다.
3. **main agent가 책임진다.** subagent는 독립된 근거 수집·검토만 수행하며, 위험 판단·수정·검증 선택·최종 보고는 main agent가 맡는다.
4. **secret을 검증 입력으로 쓰지 않는다.** PR build는 production OAuth 값 없이 compile·test할 수 있어야 하며, `pull_request_target`로 이 문제를 우회하지 않는다.
5. **정책 phase는 한 번에 하나만 적용한다.** 각 phase의 exit gate가 통과하고 필요한 저장소 관리자 승인이 끝난 뒤 다음 phase로 진행한다.

## 현재 기준선

| 영역 | 현재 상태 | 유지할 점 | 보강 필요 |
| --- | --- | --- | --- |
| durable guidance | `AGENTS.md`와 `docs/ai/*`가 역할·검증·위험 변경을 정의 | 6.3KB의 간결한 root 지시와 주제별 분리 | 실제 권한 강제와 reviewer 결과 형식 |
| custom agent | 5개 reviewer와 registry·근거 경로 반환 규칙 존재 | review-first, Git·secret 금지, parent 책임 | technical read-only, 병렬 위임 판단 기준 |
| Git hook·knowledge CI | staged guard와 `contents: read` knowledge CI 존재 | 생성 Index·graph 관계 검사 | 새 clone/worktree 설치 안내와 Codex lifecycle hook의 경계 |
| build CI | verify·PR comment·Pages deploy가 분리되고 PR 원격 검증 완료 | build/knowledge workflow 분리 | required status 관리자 지정 |
| worktree | `worktree-policy.md`로 secret-free·Local handoff 경계 적용 | Git branch 충돌 방지 | Phase 5 실제 worktree 사례 회고 |
| review | `code-review.md`, reviewer, `/review`, 사람 PR review | P0~P2·scope·근거·test-gap 공통 기준 적용 | Phase 5에서 실제 review 품질 회고 |

## Phase 0 — 결정 고정과 기준선 검사

**목적:** 뒤 phase가 서로 다른 권한 모델을 만들지 않도록 보안·운영 결정을 먼저 고정한다. 이 phase는 문서와 검사만 바꾸며 CI 또는 agent 권한을 바꾸지 않는다.

### 수정 대상

- `docs/ai/codex-operation-hardening-plan.md` (이 명세)
- `docs/ai/README.md`
- `docs/ai/registry/documents.toml`
- `docs/history/records/YYYY/`

### 확정할 결정

- reviewer agent는 hard read-only로 고정하고, 파일 수정은 main agent 또는 별도 worker에게만 맡긴다.
- project `.codex/config.toml`에는 project 불변값만 둔다. 개인 모델, reasoning, connector, 승인 설정은 사용자 전역 config에 남긴다.
- worktree에는 `local.properties`, keystore, OAuth 값 등 ignore된 설정을 기본 복사하지 않는다.
- CI build는 production OAuth secret 없이 실행 가능하도록 바꾸며, `pull_request_target`은 사용하지 않는다.

### 체크리스트

- [x] 현재 `.codex/agents/*.toml`, `.githooks/pre-commit`, `build.yml`, `knowledge.yml`의 기준선과 담당자를 record에 남긴다.
- [x] `local.properties`의 **내용을 읽거나 기록하지 않고**, 파일이 ignore된 local 설정임만 확인한다.
- [x] Phase 1~4가 기존 기능 route, Gradle dependency, OAuth 동작을 바꾸지 않는지 확인한다.
- [x] branch protection과 GitHub Actions 권한 변경은 저장소 관리자 승인 항목으로 분리한다.

### Exit gate

- 문서 registry, generated Index, graph verifier가 통과한다.
- 사용자 local 파일을 수정·stage·commit하지 않는다.

### Phase 0 실행 결과

- reviewer hard read-only, project/user config 경계, worktree secret 미복사, CI secret 제거 목표를 후속 구현 전에 확정했다.
- Phase 1의 agent sandbox 변경, Phase 2의 CI 권한·secret 변경, Phase 3의 worktree 문서는 아직 적용하지 않았다.
- 이 phase는 문서·정책 경계만 변경하므로 앱 code, route, Gradle dependency, OAuth 동작에는 영향이 없다.

## Phase 1 — Reviewer 권한과 subagent 위임 정책

**목적:** “review-first·기본 read-only”를 자연어 약속에서 실제 실행 권한과 일관된 결과 형식으로 올린다.

### 수정 대상

- `.codex/config.toml` (신규)
- `.codex/agents/{compose-ui-reviewer,data-domain-boundary-guard,navigation-contract-reviewer,state-flow-architect,test-strategy-reviewer}.toml`
- `docs/ai/subagents.md`
- `docs/ai/registry/agents.toml`
- `AGENTS.md`

### 적용 정책

1. 다섯 reviewer TOML에 `sandbox_mode = "read-only"`를 명시한다. reviewer는 parent가 요청해도 파일을 직접 수정하지 않는다.
2. 구현이 필요하면 main agent가 수정하거나, 작업별로 명시적인 write scope를 받은 `worker` 역할을 별도 호출한다. reviewer 역할을 쓰기 전용 worker로 바꾸지 않는다.
3. project config의 `[agents]`에는 `max_concurrent_threads_per_session = 3`만 둔다. 모델·reasoning은 현재처럼 parent/user 설정을 상속하며 repository에 고정하지 않는다.
4. 병렬 위임은 독립적인 read-heavy 조사·테스트 전략·로그 분석에만 사용한다. 동일 파일 또는 하나의 동작을 수정하는 작업은 한 명만 write 담당으로 둔다.
5. custom agent 결과는 아래 형식을 사용한다.

```text
Verdict: <pass / finding / insufficient context>
Review scope: <base branch / commit / working-tree diff / requested files>
Primary-document evidence: <path>
Code/test evidence: <path>
Findings: <P0/P1/P2, impact, affected path>
Verification or follow-up: <minimum action>
Uncertainty: <none or missing evidence>
```

### 체크리스트

- [x] 모든 reviewer TOML의 name, description, scope, required documents가 registry와 일치한다.
- [x] 모든 reviewer가 read-only sandbox, Git 금지, secret 금지, 위험 변경 보고, 근거 경로 반환을 가진다.
- [x] `subagents.md`에 Small 변경은 single agent, 독립 read-only 검토는 최대 3개 병렬, write는 1명이라는 dispatcher 표를 추가한다.
- [x] `AGENTS.md`에 “명시적 요청 또는 project workflow가 정한 경우에만 subagent 위임” 원칙을 추가한다.
- [x] `compose-ui-reviewer`를 명시 호출해 파일 수정 없이 common output format과 근거 경로를 반환하는 runtime 수동 확인을 기록했다.

### Exit gate

- `python3 scripts/ai/generate_knowledge_index.py --check`
- `python3 scripts/ai/verify_knowledge_graph.py`
- agent TOML 경로·ID·required document 관계 검사 통과
- 새 Codex session 또는 명시적 reviewer 호출에서 reviewer가 파일 수정 없이 결과 형식을 반환하고, main agent가 이를 최종 판단·검증·보고에 통합한 예시 1건 확인

### Phase 1 실행 결과

- 다섯 reviewer TOML에 `sandbox_mode = "read-only"`를 설정하고, 제한된 write 위임 예외를 제거했다.
- project `.codex/config.toml`에 reviewer 최대 병렬 수 3을 설정했다. 모델·reasoning·connector·승인 설정은 repository에 고정하지 않았다.
- `AGENTS.md`와 `subagents.md`에 명시적 위임, Small 변경의 single-agent 기본값, 독립 read-only 검토 최대 3개, write 담당 1명, 공통 결과 형식을 추가했다.
- `compose-ui-reviewer`를 명시 호출해 문서·코드 근거와 finding을 반환하고, working tree에 reviewer의 파일 변경이 없음을 확인했다. Phase 1 운영 exit gate를 완료했다.

## Phase 2 — Build CI 최소 권한과 secret 없는 검증

**목적:** PR의 임의 코드가 production OAuth credential 또는 Pages 배포 권한을 가진 job에서 실행되지 않게 한다.

### 수정 대상

- `.github/workflows/build.yml`
- 필요 시 `.github/workflows/coverage-comment.yml` 또는 `build.yml`의 분리 job
- 필요 시 `docs/ai/quality-gates.md`, `docs/ai/README.md`
- 필요 시 CI placeholder 전달을 위한 build logic 또는 Gradle test 설정

### 적용 정책

1. compile·lint·test job은 `permissions: { contents: read }`만 가진다.
2. PR build에는 production `CLIENT_ID`, `CLIENT_SECRET`, `REDIRECT_URI`, cache encryption secret을 주입하지 않는다. 현재 `getApiKey`가 이 값을 요구하므로 먼저 비밀이 아닌 고정 CI placeholder로 전체 Gradle 검증이 되는지 확인한다.
3. JaCoCo PR 댓글을 유지한다면 별도 job으로 분리해 `pull-requests: write`만 준다. fork PR에서는 댓글 job을 실행하지 않는다.
4. Pages upload·deploy는 `main`·`develop` push 전용 별도 job으로 분리해 그 job에만 `pages: write`, `id-token: write`를 준다.
5. 외부 PR의 code를 secret 접근 권한으로 실행하게 만드는 `pull_request_target`은 사용하지 않는다.

### 체크리스트

- [x] `app`·`data` BuildConfig에 필요한 값과 `build-logic`의 `getApiKey` fallback 순서를 확인한다.
- [x] `CLIENT_ID`, `CLIENT_SECRET`, `REDIRECT_URI`의 비밀이 아닌 placeholder가 compile·unit test·instrumentation APK build에 충분한지 로컬에서 확인한다.
- [x] verify job에 `contents: read` 외 권한·production secret이 없는지 workflow YAML에서 확인한다.
- [x] PR comment와 Pages deploy job이 verify job과 분리됐는지 확인한다.
- [x] fork PR, 일반 PR, `main`/`develop` push의 실행 경로와 권한을 표로 기록한다.
- [ ] 저장소 관리자가 `Knowledge verification`과 새 verify status를 required status로 지정한다.

### Exit gate

- CI와 같은 Gradle command가 non-secret placeholder 환경에서 통과한다.
- workflow YAML parse와 GitHub Actions 권한 검토를 통과한다.
- 원격 PR에서 verify·knowledge workflow가 통과하고, production secret 로그·PR 전달이 없음을 확인한다.

### Phase 2 실행 결과

- `build` job은 `contents: read`만 사용하며, `CLIENT_ID`·`CLIENT_SECRET`·`REDIRECT_URI`에 비밀이 아닌 고정 placeholder를 전달한다. Gradle cache encryption secret과 OAuth secret 검증 step은 제거했다.
- `coverage-comment` job은 internal PR에서만 실행하고 `pull-requests: write`만 사용한다. fork PR은 job 조건에서 제외한다.
- `deploy-pages` job은 `main`·`develop` push에서만 실행하고 `pages: write`, `id-token: write`만 사용한다. Gradle 산출물은 일반 artifact로 전달되며 deploy job은 source checkout이나 Gradle 실행을 하지 않는다.
- 로컬 placeholder 검증과 YAML 검토는 완료했다. required status 지정과 원격 PR 실행 확인은 저장소 관리자·후속 PR 단계에서 완료한다.

### 위험과 rollback

이 phase는 GitHub Actions, secret, Pages 권한을 바꾸므로 Risky Change다. placeholder로 build가 되지 않으면 권한을 되돌려 secret을 재노출하지 않고, credential-free BuildConfig 설계를 별도 승인 작업으로 분리한다.

## Phase 3 — Worktree와 local Android 환경 정책

**목적:** Codex App worktree가 편의 때문에 local OAuth 값이나 machine-specific 설정을 복사하지 않도록 한다.

### 수정 대상

- `docs/ai/worktree-policy.md` (신규)
- `docs/ai/README.md`
- `docs/ai/registry/documents.toml`
- `AGENTS.md`
- 필요 시 `.worktreeinclude` (별도 보안 승인 후에만)

### 적용 정책

1. 기본 worktree는 secret-free·재현 가능한 read/build/test 작업만 수행한다.
2. 현재 ignore된 `local.properties`는 SDK 경로와 OAuth 값이 함께 있을 수 있으므로 `.worktreeinclude`에 기본 등록하지 않는다.
3. 기기 실행 또는 OAuth 수동 검증이 필요한 작업은 Local로 handoff하거나, 사용자가 worktree에 필요한 값을 직접 안전하게 준비한 뒤 진행한다.
4. 장기적으로 local 설정을 나눌 필요가 생기면 “SDK 위치”와 “credential”을 서로 다른 ignore 파일·공급 경로로 분리하는 별도 Risky Change로 다룬다.

### 체크리스트

- [x] worktree 생성, Local handoff, branch 중복 checkout 제한을 문서화한다.
- [x] ignore 파일을 복사하지 않는 기본값과 필요한 수동 준비 항목을 문서화한다.
- [x] `.worktreeinclude`를 추가하지 않았고, 미래 예외에는 credential-free 대상의 저장소 관리자 검토와 작업 이력을 요구한다.
- [x] 새 worktree에서 문서·unit/compile 검증 가능 범위와 Local 전용 검증 범위를 분리한다.

### Exit gate

- worktree 문서가 secret 복사를 지시하지 않는다.
- 새 worktree에서 가능한 검증과 handoff 조건이 명확하다.

### Phase 3 실행 결과

- `.worktreeinclude`를 추가하지 않았다. `local.properties`의 내용은 읽거나 복사하지 않았으며, SDK와 credential이 함께 있을 수 있는 ignore 파일로 취급한다.
- `worktree-policy.md`에 secret-free worktree 범위, Android SDK 준비 실패 시의 Local handoff, 실제 OAuth·기기·signing 검증의 Local 전용 경계를 기록했다.
- Git branch의 checkout 제한과 Codex App Handoff 사용 기준을 문서화했다.

## Phase 4 — 공통 code review 기준과 lifecycle hook 경계

**목적:** custom reviewer, `/review`, 사람이 보는 PR review가 같은 결과 기준을 사용하게 하고, Git hook과 Codex hook의 책임을 명확히 한다.

### 수정 대상

- `docs/ai/code-review.md` (신규)
- `AGENTS.md`
- `docs/ai/workflows.md`
- `docs/ai/subagents.md`
- `docs/ai/README.md`
- `docs/ai/registry/documents.toml`

### 적용 정책

1. review는 실제 동작 회귀, 보안, 데이터 손상, navigation 계약, 테스트 누락을 우선하고, 단순 스타일 선호는 finding으로 보고하지 않는다.
2. finding에는 심각도(`P0`~`P2`), 영향, 재현 조건 또는 근거, 파일 경로, 최소 수정 또는 검증 방법을 포함한다.
3. `/review` 또는 custom reviewer의 범위는 base branch, 현재 diff, 특정 commit 중 하나로 명시한다.
4. Git hook·CI가 이미 강제하는 formatter, Index, graph 검사는 Codex lifecycle hook으로 중복하지 않는다.
5. 반복적으로 “세션 종료 전 이력·검증을 빼먹는” 실제 사례가 확인될 때만 `SessionStart` 또는 `Stop` hook을 별도 Small/Medium 작업으로 도입한다. hook은 파일 수정·stage·commit을 하지 않는다.

### 체크리스트

- [x] 공통 review 문서에 심각도, evidence, false-positive 처리, test-gap 보고 형식을 추가한다.
- [x] `Review Workflow`가 새 문서를 참조한다.
- [x] agent 결과 형식과 사람/`/review` 결과 형식이 충돌하지 않는지 확인한다.
- [x] lifecycle hook 필요성은 실제 누락 기록을 근거로 판단하고, 필요하지 않으면 추가하지 않는다.

### Exit gate

- representative diff 1건에서 custom reviewer 또는 `/review` 결과가 공통 형식을 따른다.
- 기존 Git hook·knowledge CI와 중복된 자동화가 추가되지 않는다.

### Phase 4 실행 결과

- `code-review.md`에 review scope, P0~P2 severity, evidence·false-positive·test-gap, 공통 결과 형식, Git hook·CI·lifecycle hook 책임을 정의했다.
- `AGENTS.md`, Review Workflow, subagent 문서와 다섯 reviewer TOML에 review scope와 P0~P2 형식을 연결했다.
- `compose-ui-reviewer`가 Liquid navigation 후보를 read-only로 검토해 scope·문서/코드 근거·P2 finding·follow-up·uncertainty 형식을 반환하는 것을 확인했다.
- 기존 Git hook·Knowledge CI가 기계적 검증을 담당하고, 현재 작업 이력에서 반복된 세션 종료 누락 근거가 없으므로 lifecycle hook은 추가하지 않았다.

## Phase 5 — 운영 정착과 관리자 확인

**목적:** 정책이 문서에만 남지 않도록 실제 PR과 반복 작업에서 효과를 확인한다.

### 수정 대상

- `docs/history/records/YYYY/`
- 필요 시 `docs/ai/codex-operation-hardening-plan.md`의 phase 상태

### 체크리스트

- [ ] Phase 1~4를 각각 독립 커밋·PR로 검토한다. Phase 2는 별도 PR을 사용한다.
- [ ] 각 phase의 exit gate, 실패 원인, rollback 여부를 history record에 남긴다.
- [ ] 저장소 관리자가 required status와 Pages 환경 권한을 확인한다.
- [ ] 3~5개 작업 후 subagent 사용률, 충돌, 검증 누락, review finding 품질을 회고한다.
- [ ] 효과가 없는 규칙은 삭제가 아니라 먼저 완화 또는 범위 축소를 제안한다.

### Exit gate

- 최소 세 번의 Medium 이상 작업에서 policy를 적용하고, 결과가 history와 Change Report에 남아 있다.
- CI 권한·secret 노출·worktree secret 복사·parallel write 충돌이 없음을 확인한다.

### Phase 5 현재 상태

- Phase 0~4는 독립 commit과 completed history record를 갖는다. Phase 2의 draft PR #161은 Build·Knowledge·coverage comment 성공과 Pages deploy skip을 확인했다.
- PR #161의 remote head는 아직 `b6bba07`이며, Phase 3·4 commit은 local branch에만 있다. push 후 PR CI를 다시 확인해야 한다.
- `develop`은 현재 branch protection이 없어 required status가 지정되지 않았다. 이는 저장소 관리자만 변경한다.
- `github-pages` environment에는 branch policy가 있으나 protected branch 전용은 아니다. Pages 권한·배포 branch 정책은 관리자 확인이 필요하다.
- 실제 Codex managed worktree의 Android 검증·Local handoff 반복 사례와 review 품질 3~5건 회고는 아직 부족하다.

## 적용 순서와 승인 경계

| 순서 | Phase | 예상 크기 | 사용자/관리자 승인 |
| --- | --- | --- | --- |
| 1 | Phase 0 | Small | 불필요 |
| 2 | Phase 1 | Medium | reviewer hard read-only 정책 승인 |
| 3 | Phase 2 | Large / Risky | GitHub Actions·secret·Pages 권한 및 required status 승인 |
| 4 | Phase 3 | Medium | `.worktreeinclude` 추가 시 보안 승인 |
| 5 | Phase 4 | Medium | lifecycle hook 추가 시 승인 |
| 6 | Phase 5 | 운영 확인 | repository 관리자 설정 확인 |

## 금지 사항

- production OAuth credential, `local.properties`, keystore, token을 문서·worktree·agent output·CI log에 복사하거나 출력하지 않는다.
- branch protection, required status, GitHub secret, Pages 설정을 API나 CLI로 자동 변경하지 않는다.
- `pull_request_target`로 PR build secret 문제를 우회하지 않는다.
- reviewer agent를 병렬 write worker로 사용하지 않는다.
- Phase 2 이전에 build workflow의 권한·secret을 단독으로 바꾸지 않는다.

## 공통 검증

모든 문서/agent 정책 phase는 다음을 실행한다. Android/CI 변경 phase는 해당 phase가 정한 Gradle·원격 CI 검증을 추가한다.

```bash
python3 scripts/ai/generate_knowledge_index.py --check
python3 scripts/ai/verify_knowledge_graph.py
python3 -m unittest discover -s scripts/ai/tests -p 'test_*.py'
git diff --check
git status --short
```
