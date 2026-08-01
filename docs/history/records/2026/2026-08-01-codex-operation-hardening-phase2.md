---
id: GS-2026-0011
status: in_progress
base_commit: 81aa58f
related_documents: [knowledge-operations, task-scope, quality-gates, codex-operation-hardening-plan, history-readme]
related_code: [.github/workflows/build.yml, build-logic, app, data]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# Codex 운영 보강 Phase 2 CI 최소 권한과 secret-free 검증

## 요청

- Phase 2로 진행해 GitHub Actions verify·PR comment·Pages deploy 권한을 분리하고, production OAuth secret 없이 build를 검증한다.

## 근거

- 기존 `build` job은 code checkout·Gradle 실행·PR 댓글·Pages 배포에 `contents: write`, `pull-requests: write`, `pages: write`, `id-token: write`를 함께 부여했다.
- `app`은 `CLIENT_ID`, `CLIENT_SECRET`, `REDIRECT_URI`를, `data`는 앞의 두 값을 BuildConfig로 요구한다. `build-logic`은 local property, Gradle property, 환경 변수 순으로 값을 찾는다.
- GitHub Actions 공식 권한 최소화와 Pages custom workflow 권한 문서를 기준으로, 실행 job과 쓰기 권한 job을 분리했다.

## 결정

- verify는 `contents: read`와 비밀이 아닌 고정 placeholder만 사용한다.
- JaCoCo 댓글은 internal PR 전용 별도 job으로 분리하고 fork PR에서는 실행하지 않는다.
- Pages upload·deploy는 `main`·`develop` push 전용 별도 job으로 분리한다. deploy job은 build artifact만 받아 source checkout·Gradle 실행을 하지 않는다.
- `pull_request_target`은 도입하지 않는다.

## 변경

- `.github/workflows/build.yml`에서 production OAuth secret과 Gradle cache encryption secret 사용, secret 검증 step, build job의 write 권한을 제거했다.
- JaCoCo report와 Pages package를 일반 artifact로 전달하고, 댓글·배포를 별도 최소 권한 job으로 분리했다.
- `docs/ai/quality-gates.md`에 일반 PR, fork PR, protected branch push별 실행 경로·권한·경계를 기록했다.
- `docs/ai/codex-operation-hardening-plan.md`에 Phase 2 완료 항목과 관리자 후속 항목을 반영했다.

## 검증

- clean temporary worktree에서 non-secret placeholder와 Android SDK 경로를 제공해 CI full Gradle command를 실행했고 JaCoCo full report 생성까지 확인했다.
- `ruby -ryaml -e 'YAML.load_file(".github/workflows/build.yml")'`: 통과.
- production OAuth·Gradle cache encryption secret·`pull_request_target` 참조 부재와 placeholder 존재를 정적 검사했다.
- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error, 0 warning.
- `python3 -m unittest discover -s scripts/ai/tests -p 'test_*.py'`: 11 tests 통과.
- `actionlint`는 현재 local 환경에 설치되어 있지 않아 실행하지 못했다. YAML parser와 원격 PR workflow로 보완한다.
- 원격 PR workflow 실행과 required status 지정은 아직 수행 전이다.

## 지시 이행

- fulfilled: Phase 2 CI 최소 권한·secret-free 검증·댓글/Pages 분리를 적용했다.
- partial: 원격 GitHub Actions 실행 확인과 required status 지정은 PR 생성 후 저장소 관리자 권한이 필요하다.

## 스킬 평가

- `project-knowledge-grounding`: workflow, BuildConfig 입력, plan·quality gate를 함께 확인해 placeholder 범위를 정하는 데 충분했다.
- `session-retrospective`: history record, generated index, graph 검증을 종료 기준으로 유지한다.

## 개선 후보

- 원격 workflow에서 artifact download가 최소 권한 job에서도 동작하는지 확인한다. 실패하면 권한을 넓히지 않고 GitHub 공식 action의 artifact 접근 요구사항을 근거로 최소 수정한다.
- 원격 실행이 안정화되면 저장소 관리자가 `Knowledge verification`과 build verify status를 required status로 지정한다.
