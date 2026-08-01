---
id: GS-2026-0013
status: completed
base_commit: b6bba07
related_documents: [root-agents, ai-readme, knowledge-operations, quality-gates, codex-operation-hardening-plan, worktree-policy, history-readme]
related_code: [.gitignore, .codex, docs/ai]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# Codex 운영 보강 Phase 3 Worktree와 Local Android 환경 정책

## 요청

- Phase 3로 진행해 Codex App worktree가 local OAuth 값이나 machine-specific 설정을 복사하지 않도록 정책을 적용한다.

## 근거

- `local.properties`는 ignore된 local 파일이고, `.worktreeinclude`는 존재하지 않는다. 파일 내용은 읽지 않았다.
- 공식 Codex worktree 지침은 `.worktreeinclude`가 managed worktree에 ignore 파일을 복사할 수 있고, Handoff가 Local/worktree 간 Git 작업을 처리하며 같은 branch는 한 checkout에서만 checkout할 수 있음을 설명한다.
- Android SDK, OAuth, signing, device 검증은 repository마다 local 설정 의존성이 다르다.

## 결정

- `.worktreeinclude`를 새로 만들지 않는다.
- default worktree는 tracked 파일과 non-secret 입력만으로 가능한 문서·조사·검증에 제한한다.
- 실제 OAuth, 기기, signing, Google service 검증은 Local handoff로 분리한다.

## 변경

- `docs/ai/worktree-policy.md`에 작업 위치 선택, worktree 시작 전 확인, 허용 검증, Local handoff, `.worktreeinclude` 예외, branch·cleanup, 완료 보고 기준을 작성했다.
- `AGENTS.md`, AI docs README, registry, operation hardening plan에 이 정책을 연결하고 Phase 3 완료 상태를 반영했다.
- Phase 2 history에 draft PR #161의 원격 CI 성공 결과를 기록했다.

## 검증

- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error, 0 warning.
- `git diff --check`: 통과.
- 문서-only 변경이므로 Gradle 검증은 실행하지 않았다.

## 지시 이행

- fulfilled: secret 복사를 지시하지 않는 worktree·Local handoff 정책을 문서화했다.
- fulfilled: `.worktreeinclude`와 `local.properties`를 추가·수정·내용 확인하지 않았다.

## 스킬 평가

- `project-knowledge-grounding`: ignore 상태, 기존 phase 명세, Android 검증 경계를 함께 확인하는 데 사용했다.
- `session-retrospective`: history·registry·generated Index 관계를 종료 기준으로 적용한다.
- `openai-docs`: 최신 Codex manual의 managed worktree, Handoff, `.worktreeinclude`, branch 제한 지침을 확인하는 데 사용했다.

## 개선 후보

- Phase 5에서 실제 신규 managed worktree와 Local handoff 사례를 회고해 정책이 과도하거나 부족한지 점검한다.
- credential-free SDK 설정 파일을 분리해야 할 요구가 생기면 별도 Risky Change로 설계·승인한다.
