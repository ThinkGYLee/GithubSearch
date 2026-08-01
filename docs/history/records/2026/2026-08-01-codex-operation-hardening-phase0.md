---
id: GS-2026-0009
status: completed
base_commit: 2147a3e
related_documents: [knowledge-operations, task-scope, quality-gates, codex-operation-hardening-plan, history-readme]
related_code: [.codex/agents, .githooks, .github/workflows, build-logic, app, data]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# Codex 운영 보강 Phase 0 결정과 책임 분리

## 요청

- Codex 운영 보강 명세의 Phase 0을 적용하고, reviewer·main agent·CI·worktree·관리자 권한의 책임과 후속 변경을 분리한다.

## 근거

- 현재 5개 reviewer는 review-first 정책을 가지지만 TOML sandbox가 아직 hard read-only가 아니다.
- `.githooks/pre-commit`과 `knowledge.yml`은 각각 staged 변경과 PR 지식 관계를 검사한다. `build.yml`은 verify, PR comment, Pages deploy 권한을 한 job에 둔다.
- `local.properties`는 ignore된 local 파일임만 확인했고, 내용은 읽거나 기록하지 않았다.
- 공식 Codex 가이드의 durable guidance, bounded subagent, least privilege, worktree 원칙을 phase 명세의 근거로 사용했다.

## 결정

- reviewer의 hard read-only 전환은 Phase 1, CI 권한·OAuth secret 제거는 Phase 2, worktree local 설정 문서는 Phase 3, 공통 review 기준은 Phase 4로 분리한다.
- 현재 runtime 정본은 기존 `AGENTS.md`, `subagents.md`, agent TOML로 유지한다. Phase 0은 후속 정책의 승인·책임 경계만 적용한다.
- repository에는 팀 공통 불변값만 두고, 개인 모델·reasoning·connector·승인 설정은 사용자 전역 config에 남긴다.
- GitHub Actions 권한, secret, Pages, branch protection은 저장소 관리자와 명시적 Risky Change 승인 없이는 변경하지 않는다.

## 변경

- 운영 보강 명세 상태를 `Phase 0 completed, Phase 1 pending`으로 바꿨다.
- main agent, reviewer, Codex config, Git hook, knowledge CI, build CI, worktree, 저장소 관리자별 책임·현재 상태·후속 phase를 표로 분리했다.
- Phase 0 체크리스트와 실행 결과를 완료 상태로 기록했다.

## 검증

- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과.
- `python3 scripts/ai/verify_knowledge_graph.py`: 통과.
- `python3 -m unittest discover -s scripts/ai/tests -p 'test_*.py'`: 문서·registry 검증 회귀 11 tests 통과.
- `git diff --check`: 통과.
- 문서·registry·generated Index만 변경했으며 Android code, Gradle, CI runtime은 바꾸지 않아 Gradle 검증은 실행하지 않았다.

## 지시 이행

- 충족: Phase 0을 후속 구현과 분리해 정책 결정·책임·승인 경계만 적용했다.
- 충족: reviewer, CI, worktree, 관리자 책임을 각각 다른 phase로 분리했다.
- 충족: `local.properties`와 사용자 untracked migration template의 내용을 변경·stage·commit하지 않았다.
- 충족: commit·push·GitHub 원격 설정 변경을 수행하지 않았다.

## 스킬 평가

- `project-knowledge-grounding`: 현재 agent, hook, CI, build 설정을 정책 분리의 근거로 고정했다.
- `session-retrospective`: history·registry·generated Index·graph 검증과 사용자 local 파일 보존을 종료 전에 점검했다.

## 개선 후보

- Phase 1에서 hard read-only TOML을 적용할 때 reviewer 한 개로 parent runtime permission과의 실제 동작을 수동 확인한다.
