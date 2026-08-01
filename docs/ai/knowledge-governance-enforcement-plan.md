# 지식 운영 강제 체계 보강 명세

> **Status:** implemented
>
> **Scope:** 기존 지식 registry·생성 Index·검증 스크립트를 신뢰할 수 있게 보강하고, AI 작업 절차·로컬 commit·PR CI에서 같은 기준으로 확인한다.
>
> **Predecessor:** [AI 보고·지식 운영 체계 이식 명세](reporting-system-migration-plan.md)

## 목적

현재 지식 운영 체계는 문서, project skill, custom agent, 작업 이력을 연결하지만 다음 네 가지 빈틈이 있다.

1. Git에 포함하지 않는 개인 문서를 일반 공유 문서처럼 등록하면 새 checkout과 CI에서 실패한다.
2. verifier가 일부 형식·참조 오류를 놓칠 수 있다.
3. AI가 생성 Index 갱신 절차를 따르지 않거나 사람이 직접 수정하면 최신성이 깨질 수 있다.
4. 현재 build CI는 `docs/**` 변경을 건너뛰므로 PR에서 지식 관계를 최종 확인하지 않는다.

목표는 다음 흐름을 확립하는 것이다.

```text
문서·skill·agent·이력 변경
  → AI skill이 registry와 생성 Index를 갱신
  → pre-commit hook이 최신성만 확인
  → 별도 PR CI가 동일한 검사를 다시 확인
```

hook과 CI는 파일을 자동 수정하거나 자동 stage하지 않는다. 실패한 경우 변경 내용을 검토한 사람이 generator를 실행하고 결과를 명시적으로 stage한다.

실행할 때는 [지식 운영 강제 체계 실행 프롬프트](knowledge-governance-enforcement-prompt.md)를 사용한다.

## SmartTimer 비교와 이식 결정

| 항목 | SmartTimer 현황 | GithubSearch 결정 |
| --- | --- | --- |
| 문서 registry | 등록 문서는 모두 공유·추적된 파일이라는 전제이며, 없으면 오류 | 개인 문서는 `local_document`로 별도 분류한다. 공유 정본과 생성 Index에서는 제외한다. |
| graph verifier | ID 중복, skill frontmatter·Catalog, agent 형식, history의 skill·코드 참조까지 확인 | 검증 범위를 같은 수준으로 확대하되 GithubSearch registry 구조와 경로만 사용한다. |
| AI workflow | 시작 grounding·종료 retrospective skill을 모든 작업 흐름에 연결 | 이미 도입한 두 skill을 AI 문서와 루트 지시에서 명시적으로 필수화한다. |
| pre-commit hook | 개인 PC의 shared hook 경로에서 번역 XML 변경만 빠르게 lint | 저장소 안의 단일 dispatcher가 문서 관계 최신성과 staged 번역 XML의 영향을 받은 module lint를 순서대로 확인한다. 개인 절대 경로는 사용하지 않는다. |
| PR CI | Android build와 screenshot 검증만 수행하며 지식 관계 검증은 없음 | 비밀값·배포 권한이 없는 별도 `knowledge` workflow를 추가한다. |

SmartTimer의 원칙은 가져오되 구현을 복사하지 않는다. 특히 SmartTimer의 `core.hooksPath`는 개인 PC 절대 경로를 사용하므로 GithubSearch의 공통 설치 방식으로 적합하지 않다.

## 범위와 비범위

### 포함

- `documents.toml`의 공유 문서와 로컬 보조 문서 분리
- `verify_knowledge_graph.py`의 전수 검증 확대와 회귀 검사
- generator·verifier를 실행하도록 하는 AI workflow/skill 규칙의 명확화
- 문서 관련 staged 변경만 검사하는 read-only pre-commit hook
- 문서·skill·agent·registry·검증 스크립트 변경에 반응하는 최소 권한 PR CI
- 변경 사항을 설명하는 작업 이력과 Change Report

### 제외

- `docs/multi-module-migration-template.md`의 내용 수정·stage·commit
- Android 앱 코드, Gradle 의존성, screenshot CI 변경
- 기존 build CI의 secret·coverage·Pages 배포 동작 변경
- GitHub branch protection 또는 required status의 원격 설정 변경
- hook을 무단 설치하거나 사용자의 Git 전역 설정을 변경하는 동작

## 설계 계약

### P0 — 로컬 보조 문서

`docs/ai/registry/documents.toml`에는 공유 문서용 `[[document]]`와 별도로 `[[local_document]]`를 둔다.

- `local_document`은 최소 `id`, `path`, `purpose`를 가진다.
- ID는 공유 문서·다른 로컬 문서와 중복될 수 없다.
- 파일이 없어도 오류가 아니다. 따라서 깨끗한 checkout과 CI가 통과해야 한다.
- 존재하는 로컬 문서는 document coverage에서 등록된 것으로 처리한다.
- 로컬 보조 문서는 generated `docs/knowledge/INDEX.md`, 공통 history의 `related_documents`, project skill·agent의 `required_documents`에서 제외한다.
- 로컬 문서의 내용, 링크, code path는 팀 공통 정본의 검증 대상이 아니다.

초기 대상은 `docs/multi-module-migration-template.md` 하나다. 이 파일은 사용자 변경이므로 내용·Git 추적 상태를 바꾸지 않는다. 이후 첫 CI 실행에서 `docs/build/PROJECT_GUIDE.md`와 `docs/build/STYLE_GUIDE.md`가 `build/` ignore 규칙으로 Git에 없는 개인 참고 문서임을 확인했으므로, 같은 `local_document` 정책으로 분리한다.

### P1 — verifier 전수 검증

`scripts/ai/verify_knowledge_graph.py`는 dependency-free Python으로 다음 계약을 확인한다.

| 영역 | 반드시 확인할 내용 |
| --- | --- |
| registry | document·local document·skill·agent ID 중복, 필수 ID와 파일 경로, 공유 문서의 실제 code path |
| 공유 문서 | history record를 제외한 모든 공유 Markdown의 registry 등록, local document 경로의 예외 처리, 깨진 로컬 Markdown link |
| project skill | `SKILL.md` frontmatter, skill 이름과 디렉터리 이름, 본문 존재, TODO, registry 경로·ID, Catalog와의 양방향 일치, 필요한 문서 ID |
| custom agent | TOML name·description·developer instruction·문자열 형식, registry 경로·ID, 필요한 문서 ID |
| 작업 이력 | record ID 중복, 상태, base commit, 관련 공유 문서 ID, 사용 skill ID, 관련 코드 경로, 완료 record의 필수 섹션 |

검사기는 문제가 있으면 사람이 고칠 수 있는 파일·필드·관계를 함께 출력한다. 새 검증 항목마다 정상 사례와 실패 사례를 재현하는 dependency-free 회귀 검사를 추가한다.

### P2 — AI 절차와 pre-commit hook

두 project skill의 책임을 유지한다.

- `project-knowledge-grounding`: 분석·구현·리뷰·문서 작업 시작 시 관련 문서와 코드 근거를 고른다.
- `session-retrospective`: 파일 수정 작업 종료 시 history, 생성 Index, graph 검증, Change Report를 확인한다.

루트 `AGENTS.md`와 `docs/ai/workflows.md`에는 단순 질의·짧은 상태 응답을 제외한 모든 project 작업에서 시작 skill을 적용하고, 파일 수정 작업에서는 종료 skill을 적용한다고 명시한다.

로컬 hook은 다음 설계로 추가한다.

- 경로: `.githooks/pre-commit`, 실제 검사: `scripts/git-hooks/run-staged-knowledge-check.sh`, `scripts/git-hooks/run-staged-resource-lint.sh`.
- 적용 경로: `AGENTS.md`, `docs/**/*.md`, `docs/ai/registry/**/*.toml`, `.codex/skills/**`, `.codex/agents/**`, `scripts/ai/**`, `*/src/main/res/values*/*.xml`.
- 해당 staged 변경이 없으면 즉시 성공한다.
- 지식 변경이 있으면 repository root에서 `generate_knowledge_index.py --check`와 `verify_knowledge_graph.py`를, values resource 변경이 있으면 중첩 경로까지 실제 Gradle module 경로로 변환한 `:<module>:lintDebug`를 실행한다.
- working tree, index, Git 설정을 수정하지 않는다. `--no-verify`는 기술적으로 가능하므로 CI가 최종 보호막이다.
- 설치는 version-controlled `.githooks`를 가리키는 repository-local `core.hooksPath` 설정 방법만 문서화한다. 설치 여부와 Git 전역 설정 변경은 사용자가 결정한다.

### P2 — 별도 PR CI

`.github/workflows/knowledge.yml`을 별도 workflow로 추가한다.

- `pull_request`와 `main`·`develop` push에서 아래 경로가 바뀔 때만 실행한다: `AGENTS.md`, `docs/**`, `.codex/skills/**`, `.codex/agents/**`, `scripts/ai/**`, `.github/workflows/knowledge.yml`.
- `contents: read`만 부여한다. secret, Gradle cache encryption key, Pages, PR comment 권한을 사용하지 않는다.
- checkout 후 Python으로 generator의 `--check`와 graph verifier를 실행한다.
- 기존 `build.yml`의 `paths-ignore`, secret 처리, coverage/Pages 배포 권한은 변경하지 않는다.
- GitHub에서 실제 병합 차단을 원하면 workflow 추가 후 repository 관리자가 이 workflow를 required status로 지정해야 한다. 이는 이번 자동 변경 범위 밖이다.

## 실행 단계

### Phase 1 — 기준선과 P0 분리

1. 작업 시작 history record를 만든다.
2. 사용자 untracked 문서의 내용·추적 상태를 기록만 하고 수정하지 않는다.
3. registry reader, generator, verifier에 `local_document` 의미를 추가한다.
4. 기존 migration template 등록을 `local_document`로 이동한다.
5. 로컬 파일이 있는 현재 환경과 없는 임시 환경 모두에서 기대 동작을 확인한다.

**Exit gate:** 개인 문서를 변경하지 않았고, 없는 환경에서도 shared registry 검증이 실패하지 않는다.

**실행 결과:** [x] `multi-module-migration-template`을 `local_document`로 분리했고, 파일이 없는 dependency-free fixture에서도 verifier가 통과했다. local document는 generated Index와 공통 link 검증에서 제외됨을 회귀 검사로 확인했다.

### Phase 2 — P1 verifier 확대

1. SmartTimer verifier의 유효한 검사 범위를 GithubSearch 경로에 맞춰 이식한다.
2. 현 registry·문서·skills·agents·history 전체를 점검해 기존 결함을 바로 고친다.
3. 각 새 검사 항목의 실패 fixture 또는 동등한 재현 검사를 추가한다.

**Exit gate:** 현재 repository는 0 error, 의도적으로 잘못 만든 fixture는 해당 오류를 낸다.

**실행 결과:** [x] registry ID·경로, shared Markdown link, skill frontmatter·Catalog, agent TOML, history metadata·참조를 검사하도록 확대했다. 정상 fixture와 10개 실패/동작 fixture를 포함한 11개 unittest가 통과했다.

### Phase 3 — P2 AI 절차와 hook

1. AI 문서와 두 skill의 의무 적용 조건을 충돌 없이 정렬한다.
2. version-controlled hook과 설치 안내를 추가한다.
3. 문서 관련 staged 변경, 무관한 staged 변경, stale Index의 세 경우를 확인한다.

**Exit gate:** hook은 문서를 자동 수정하지 않고, 관련 변경에서만 정확히 실패 또는 성공한다.

**실행 결과:** [x] `.githooks/pre-commit`을 knowledge 검사와 staged values resource lint를 순서대로 호출하는 dispatcher로 정렬했다. 회귀 검증에서 무관한 staged 변경은 통과하고, 관련 변경은 통과하며, stale Index는 실패하고, `app`·`feature/home` resource는 각각 정확한 Gradle module lint를 호출하는 것을 확인했다.

### Phase 4 — P2 CI

1. 최소 권한 `knowledge.yml`을 추가한다.
2. trigger 경로와 기존 build workflow의 분리를 확인한다.
3. CI와 같은 두 Python 명령을 로컬에서 실행한다.

**Exit gate:** 문서 변경은 knowledge workflow의 대상이고, 기존 secret-bearing build workflow의 범위를 넓히지 않는다.

**실행 결과:** [x] `contents: read` 전용 `knowledge.yml`을 추가하고 YAML parse 및 `build.yml` 무변경을 확인했다. 첫 원격 PR 실행과 required status 지정은 repository 관리자의 후속 작업이다.

### Phase 5 — 완료 보고

1. 생성 Index를 갱신하고 verifier, 회귀 검사, `git diff --check`를 실행한다.
2. history record를 `completed`로 전환한다.
3. Change Report에 hook 설치가 별도 사용자 결정임과 GitHub required status의 수동 설정 필요 여부를 명시한다.

**실행 결과:** [x] generator, verifier, dependency-free 회귀 검사, Python compile, YAML parse, `git diff --check`를 실행했다. 작업 이력은 완료 상태로 전환했다.

## 완료 기준

- P0의 개인 문서가 공유 CI·새 checkout을 깨지 않는다.
- P1의 전체 검사 범위와 회귀 검사가 존재하며 현재 repository가 통과한다.
- P2에서 AI 절차, 로컬 hook, 별도 CI가 같은 두 검증 명령을 사용한다.
- hook과 CI는 생성물을 몰래 수정·stage하지 않고 최소 권한을 지킨다.
- 기존 build CI의 secret·배포 동작과 사용자 untracked 파일은 보존된다.

## 검증 명령

```bash
python3 scripts/ai/generate_knowledge_index.py --check
python3 scripts/ai/verify_knowledge_graph.py
python3 -m unittest discover -s scripts/ai/tests -p 'test_*.py'
git diff --check
git status --short
```

구현 중 새 회귀 검사 위치가 기존 Python test 구조와 충돌하면, dependency-free 원칙을 유지하는 범위에서 경로를 조정하고 이유를 작업 이력에 남긴다.

## 예상 커밋 분리

1. `Docs(ai) : 로컬 보조 문서와 지식 검증 명세 추가`
2. `Build(ai) : 지식 관계 검증 강화`
3. `Chore(ai) : 지식 검증 hook과 CI 추가`

개별 커밋 전에는 사용자에게 변경 범위와 검증 결과를 다시 보고한다.
