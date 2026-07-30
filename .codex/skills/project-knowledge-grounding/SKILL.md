---
name: project-knowledge-grounding
description: GithubSearch의 분석, 구현, 리뷰, 문서 작업 전에 관련 문서와 코드 근거를 함께 선택하고 충분성을 판정한다.
---

# Project Knowledge Grounding

작업 시작 전에 `AGENTS.md`, `docs/ai/workflows.md`, `docs/ai/knowledge-operations.md`를 읽고 `docs/knowledge/INDEX.md`와 registry에서 요청에 맞는 최소 문서·코드 경로를 선택한다.

1. 사용자 요구사항, 제약, 현재 커밋을 확인한다.
2. 선택한 문서와 현재 코드·테스트·설정을 함께 읽는다.
3. 문서와 코드가 충돌하거나 근거가 부족하면 추가 조사한다.
4. 충분한 근거가 없으면 추측으로 수정하지 않고 `insufficient context`로 보고한다.
5. Medium·Large·Risky 또는 policy·skill·agent 작업이면 history record에 요청·근거·관련 경로를 기록한다.

원문 대화, 내부 추론, secret·token·개인 정보는 기록하거나 출력하지 않는다.
