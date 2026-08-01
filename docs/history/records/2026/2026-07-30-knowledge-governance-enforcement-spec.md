---
id: GS-2026-0002
status: completed
base_commit: 0f1f3dd
related_documents: [root-agents, workflows, quality-gates, knowledge-operations, reporting-system-migration-plan, history-readme, knowledge-governance-enforcement-plan, knowledge-governance-enforcement-prompt]
related_code: [docs/ai, docs/history, scripts/ai, .github]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# 지식 운영 강제 체계 실행 명세와 프롬프트

## 요청

- SmartTimer와의 비교 결과를 반영해 P0 local document, P1 verifier 전수 강화, P2 AI 절차·hook·CI의 실행 명세를 작성한다.
- 명세를 달성할 수 있는 재사용 가능한 실행 프롬프트를 docs에 남긴다.

## 근거

- 공유 registry에 사용자 untracked 문서가 일반 document로 등록되어 있어 clean checkout과 CI에서 실패할 위험이 있다.
- 현재 verifier는 SmartTimer verifier보다 registry 중복, skill·agent 형식, history 참조 검증 범위가 좁다.
- 현재 build CI는 `docs/**`만 바뀌면 실행하지 않고, secret·배포 권한을 가진 build workflow에 문서 검증을 합치면 범위가 커진다.

## 결정

- 개인 문서는 `local_document`로 분리해 공유 정본·generated Index·공통 history 참조에서 제외한다.
- SmartTimer의 폭넓은 verifier 계약은 가져오되, 개인 절대 경로 hook과 문서 검증이 없는 CI 구성은 복사하지 않는다.
- hook은 read-only 최신성 검사만 하고, 최소 권한 별도 CI가 최종 확인하도록 설계한다.

## 변경

- 후속 실행 범위, 단계, 설계 계약, 검증 조건을 `knowledge-governance-enforcement-plan.md`에 추가했다.
- 그대로 실행할 수 있는 제약 포함 프롬프트를 `knowledge-governance-enforcement-prompt.md`에 추가했다.
- AI docs 목록, document registry, 이전 이식 명세의 follow-up 링크를 갱신했다.

## 검증

- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error로 통과.
- `git diff --check`: 통과.
- 문서-only 작업이므로 Gradle 검증은 실행하지 않았다.

## 지시 이행

- 충족: 구현 전에 작업 명세를 먼저 docs에 작성했다.
- 충족: P0/P1/P2 및 CI 분리 기준을 SmartTimer 비교 결과에 맞춰 명시했다.
- 충족: 실행용 프롬프트에 범위, 순서, 금지 사항, 검증, 보고 형식을 포함했다.

## 스킬 평가

- `project-knowledge-grounding`: 현재 registry, verifier, workflow, CI의 실제 차이를 명세 근거로 고정하는 데 사용했다.
- `session-retrospective`: 문서 registry, generated Index, history record, 종료 검증을 빠뜨리지 않게 하는 데 사용한다.

## 개선 후보

- update: 실제 구현 단계에서 hook 설치 안내의 사용성, local document의 optional 검증 범위, GitHub required status 운영 주체를 한 번 점검한다.
