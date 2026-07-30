---
name: session-retrospective
description: GithubSearch 파일 수정 작업의 종료 전에 지시 이행, 근거, 변경, 검증, skill 평가와 개선 후보를 history record와 Change Report로 점검한다.
---

# Session Retrospective

파일을 수정한 모든 project 작업의 완료 전에 현재 diff, history record, 사용자 요구사항, 실행한 검증을 비교한다.

1. 요구사항별 이행 결과를 `fulfilled`, `partial`, `not fulfilled`로 정리한다.
2. Medium·Large·Risky 또는 policy·skill·agent 작업의 history record에 결정·변경·검증·개선 후보를 갱신한다.
3. `python3 scripts/ai/generate_knowledge_index.py --check`와 `python3 scripts/ai/verify_knowledge_graph.py`를 실행한다. `scripts/ai/tests/`를 변경했으면 dependency-free 회귀 검사도 실행한다.
4. 실패를 숨기지 않고 Change Report의 Verification과 Risks에 기록한다.
5. 자동 개선은 작업 이력, generated index, 확인된 link·frontmatter 보정으로 한 번만 제한한다.

삭제·이동, 보안 정책 또는 검증 명령 변경은 명시 승인 없이는 수행하지 않는다.
