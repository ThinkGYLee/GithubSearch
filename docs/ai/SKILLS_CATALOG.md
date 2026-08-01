# GithubSearch Project Skill 분류표

이 문서는 project에 설치된 skill의 적용 조건과 경계를 연결한다. 경로·필수 문서의 정본은 [`registry/skills.toml`](registry/skills.toml)이며, `python3 scripts/ai/verify_knowledge_graph.py`가 registry와 실제 skill을 함께 점검한다.

## 공통 지식·회고

| Skill | 적용 trigger | 경계 |
| --- | --- | --- |
| [`project-knowledge-grounding`](../../.codex/skills/project-knowledge-grounding/SKILL.md) | 분석·구현·리뷰·문서 작업의 시작 | 관련 문서와 코드의 최소 근거 묶음을 만들고 근거 없는 결론을 막는다. |
| [`session-retrospective`](../../.codex/skills/session-retrospective/SKILL.md) | 파일 수정 작업의 종료 | 지시 이행, 변경, 검증, skill 평가, 개선 후보와 history record를 점검한다. |
