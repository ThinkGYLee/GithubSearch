# AI 보고·지식 운영 체계 이식 명세

> **Status:** active — Phase 0
>
> **Scope:** SmartTimer의 근거 기반 Change Report, 영속 작업 이력, 지식 registry, 검증 스크립트, 시작·종료 skill을 GithubSearch의 기존 AI 문서와 4개 review-first agent에 맞게 이식한다.
> **Source of truth:** 이 문서는 이식 범위와 순서를 정의한다. 실제 운영 규칙은 완료한 phase에서 지정하는 `docs/ai/*` 정본 문서로 이동한다.

> **Follow-up:** 이식 후 확인된 local document, verifier 범위, hook·CI 강제의 보강은 [지식 운영 강제 체계 보강 명세](knowledge-governance-enforcement-plan.md)에서 별도로 관리한다.

## 목적

GithubSearch의 기능·정책·문서 업데이트가 이어질 때, 최종 응답만으로는 이전 결정의 근거와 검증을 재사용하기 어렵다. 이 체계는 다음 연결을 보장한다.

```text
요청 → 관련 문서·코드 근거 → 최소 변경 → 검증 → Change Report → 작업 이력 → 다음 작업의 근거
```

작업 이력에는 원문 대화, 내부 추론, secret, token, 개인 정보를 저장하지 않는다. 사용자 요구사항·결정·검증의 재현 가능한 요약만 남긴다.

## 이식 분류

### 적용

| 항목 | GithubSearch 적용 방식 | 이유 |
| --- | --- | --- |
| `Evidence & Retrospective` | Medium/Risky Change의 Change Report 필수 섹션으로 추가 | 근거·지시 이행·skill 효과·개선 후보를 최종 보고에 남긴다. |
| 영속 작업 이력 | `docs/history/records/YYYY/`에 Markdown record 저장 | 여러 세션의 설계·검증·후속 결정을 연결한다. |
| 지식 운영 | `knowledge-operations.md`, registry, generated index 추가 | 문서와 실제 코드·설정 근거를 최소 묶음으로 선택하게 한다. |
| 지식 검증 | link, registry, history metadata를 검사하는 dependency-free Python script 추가 | 문서/agent/skill 경로 드리프트를 자동 발견한다. |
| 시작·종료 skill | `project-knowledge-grounding`, `session-retrospective`를 project skill로 추가 | 작업 시작의 근거 수집과 종료의 이행 점검을 재사용한다. |
| 문서/skill 변경 gate | quality gate에 index·graph 검증 추가 | policy 변경의 source-of-truth 누락을 막는다. |
| project agent registry | 기존 4개 `.codex/agents/*.toml`을 registry에만 등록 | agent의 이름·경로·필수 문서를 기계적으로 검증한다. |

### GithubSearch에 맞게 축소 적용

| SmartTimer 방식 | GithubSearch 정책 |
| --- | --- |
| 모든 의미 있는 작업에 history record | Small은 선택, Medium·Large·Risky Change와 policy/skill/agent 변경은 필수 |
| 모든 작업의 시작/종료 skill | 분석 전 근거가 필요한 작업과 파일 수정 작업에 적용. 단순 질의·짧은 status 응답은 제외 |
| 대규모 skill catalog | 새 project skill과 project agent만 catalog/registry에 관리. system 제공 skill 전체를 복제하지 않음 |
| 전체 domain 전용 agent | 기존 4개 reviewer를 유지. Navigation/OAuth 전용 agent는 반복 근거가 생긴 뒤 별도 승인 |
| 모든 project 문서의 상세 code path | 모든 Markdown을 registry에 등록하되, code path는 지속적 계약·운영 문서에만 최소 지정 |

### 이식하지 않음

- SmartTimer의 timer lifecycle, AlarmManager, notification, release/Firebase 특화 policy와 agent
- SmartTimer의 전체 project skill 목록과 Compose performance micro-skill catalog
- 기존 작업의 과거 history backfill
- 자동 삭제·이동·대규모 문서 재작성 권한
- 앱 코드, Gradle, CI workflow, Git hook 변경

## 현재 체계에서 정리할 부분

| 현재 항목 | 조치 | 시점 |
| --- | --- | --- |
| `change-report-template.md`의 중복된 `Suggested Commit Message` 블록 | 하나의 canonical template만 남긴다. | Phase 1 |
| 커밋 Subject 영어 명령형 규칙 | 전역 한국어 정책과 일치하도록 한국어 변경 요약으로 바꾸고 coding convention과 함께 정렬한다. | Phase 1 |
| 문서-only 변경의 검증 기준이 `git diff` 중심 | 문서·skill·agent·history 변경일 때 knowledge index/graph 검증을 추가한다. | Phase 3 |
| 최종 응답만 남는 보고 | Medium/Risky 작업은 Change Report와 history record를 함께 남긴다. | Phase 2 |
| `GEMINI.md`의 독자적 workflow 요약 | legacy adapter로 보존한다. 정본을 복제하지 않고 새 reporting source-of-truth 링크만 추가한다. | Phase 4 |
| 현재 untracked `docs/multi-module-migration-template.md` | 내용은 수정·stage·commit하지 않는다. registry coverage를 위해 path와 분류만 추가한다. | Phase 3 |

## Phase 0 — 기준선과 명세

**목적:** 적용 범위, 제외 범위, 기존 문서 보존 규칙을 고정한다.

- [x] SmartTimer와 GithubSearch의 Change Report, history, knowledge operations, skill/agent registry를 비교했다.
- [x] GithubSearch의 현재 4개 custom agent, legacy `GEMINI.md`, 기존 AI docs와 untracked 문서를 확인했다.
- [x] 적용·축소·제외·정리 대상을 이 명세에 기록했다.

**Exit gate:** Markdown link 확인, `git diff --check`, 기존 user 변경 미수정 확인.

## Phase 1 — 최종 보고 정본 정렬

**목적:** 사용자에게 보이는 Change Report를 근거·회고를 담을 수 있게 정렬한다.

**변경 대상:**

- `docs/ai/change-report-template.md`
- `docs/ai/android-coding-conventions.md`
- `AGENTS.md`

**완료 조건:**

- Change Report에 `Evidence & Retrospective`가 있고 관련 문서/코드, 지시 이행, skill 평가, 개선 후보를 기록할 수 있다.
- Small 작업은 필요한 섹션만 간소화할 수 있다.
- 커밋 Subject 언어 규칙이 전역/프로젝트 문서와 충돌하지 않는다.

**현재 검증 상태:**

- [x] `Evidence & Retrospective`와 한국어 Subject 규칙을 정본과 coding convention에 반영했다.
- [x] 중복된 Suggested Commit Message template을 제거했다.
- [x] `AGENTS.md`에 Medium/Risky 보고·이력 연결 규칙을 반영했다.

## Phase 2 — 영속 작업 이력

**목적:** Medium/Risky 작업의 요청·결정·검증을 보안 필터링된 Markdown으로 남긴다.

**변경 대상:**

- `docs/history/README.md`
- `docs/history/records/YYYY/` (이식 작업 자체의 첫 record 포함)
- `docs/ai/knowledge-operations.md`
- `docs/ai/workflows.md`

**완료 조건:**

- task ID, status, base commit, related documents/code, skills metadata와 요청·근거·결정·변경·검증·지시 이행·skill 평가·개선 후보 템플릿이 정의된다.
- `in_progress`, `completed`, `interrupted` 상태와 다음 세션 처리 기준이 명확하다.
- secret·원문 대화·내부 추론 저장 금지 규칙이 포함된다.

**현재 검증 상태:**

- [x] 작업 이력 템플릿과 이식 작업의 `in_progress` record를 추가했다.
- [x] knowledge operations와 workflow에 근거·이력·보안 필터링 규칙을 반영했다.

## Phase 3 — Registry와 지식 검증

**목적:** 문서·project skill·custom agent·history record의 연결을 기계적으로 확인한다.

**변경 대상:**

- `docs/ai/registry/{documents,skills,agents,policies}.toml`
- `docs/knowledge/{README,INDEX}.md`
- `docs/ai/SKILLS_CATALOG.md`
- `scripts/ai/{registry,generate_knowledge_index,verify_knowledge_graph}.py`
- `docs/ai/quality-gates.md`, `docs/ai/README.md`

**완료 조건:**

- `docs/**/*.md`는 history record를 제외하고 document registry에 등록된다.
- generated `INDEX.md`는 직접 수정하지 않으며 `--check` 모드로 최신성을 확인한다.
- verifier는 Markdown link, document coverage, registered project skill, registered custom agent, history metadata를 검사한다.
- 기존 untracked migration template은 registry에만 등록되고 내용은 변경되지 않는다.

**현재 검증 상태:**

- [x] 모든 Markdown 문서와 기존 4개 agent를 registry에 등록했다.
- [x] generated knowledge index와 dependency-free link/registry/history verifier를 추가했다.
- [x] user untracked migration template은 registry에만 등록하고 내용은 수정하지 않았다.

## Phase 4 — Project skill과 legacy adapter 연결

**목적:** 새 운영 체계를 일상 workflow에 연결하되 legacy 문서를 덮어쓰지 않는다.

**변경 대상:**

- `.codex/skills/project-knowledge-grounding/`
- `.codex/skills/session-retrospective/`
- `docs/ai/GEMINI.md`
- `docs/ai/subagents.md`

**완료 조건:**

- 시작 skill은 관련 문서·코드의 최소 근거 묶음과 충분성 판단만 담당한다.
- 종료 skill은 history record, diff, 지시 이행, 검증, 개선 후보를 점검한다.
- `GEMINI.md`는 adapter 지위를 유지하고 정본 reporting 규칙으로 링크한다.
- 기존 4개 agent의 write/Git 금지 guardrail은 변경하지 않는다.

**현재 검증 상태:**

- [x] `project-knowledge-grounding`, `session-retrospective` project skill과 registry/catalog 연결을 추가했다.
- [x] `GEMINI.md`를 legacy adapter로 보존하고 reporting 정본 링크만 추가했다.
- [x] 기존 agent 파일은 수정하지 않고 subagent 운영 문서에 주 작업자 책임만 연결했다.

## Phase 5 — 전체 검증과 운영 시작

**목적:** 이식 체계가 실제 update에 안전하게 적용되는지 확인한다.

**Exit gate:**

```bash
python3 scripts/ai/generate_knowledge_index.py --check
python3 scripts/ai/verify_knowledge_graph.py
git diff --check
git status --short
```

- 이식 작업의 history record를 `completed`로 전환한다.
- Change Report에 phase별 변경·검증·남은 리스크를 기록한다.
- 이후 새 Medium/Risky 작업에서 한 번 사용해 중복·과도한 절차를 확인한다.

**현재 검증 상태:**

- [x] generator, graph verifier, `git diff --check`, `git status --short`를 실행했다.
- [x] 이식 작업의 history record를 완료 상태로 전환했다.

## Commit 계획

1. `Docs(ai) : 보고 정본과 작업 이력 기반 추가` — Phase 1~2
2. `Docs(ai) : 지식 registry와 검증 체계 추가` — Phase 3
3. `Docs(ai) : 근거와 회고 skill 연결` — Phase 4~5

기존 사용자 변경인 `docs/multi-module-migration-template.md`는 어느 commit에도 포함하지 않는다.
