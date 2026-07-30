---
id: GS-2026-0003
status: completed
base_commit: 0f1f3dd
related_documents: [root-agents, workflows, quality-gates, knowledge-operations, reporting-system-migration-plan, knowledge-governance-enforcement-plan, knowledge-governance-enforcement-prompt, history-readme, skills-catalog]
related_code: [AGENTS.md, docs/ai, docs/history, scripts/ai, scripts/git-hooks, .githooks, .github]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# 지식 운영 강제 체계 구현

## 요청

- P0 local document 분리, P1 graph verifier 전수 강화, P2 AI 절차·pre-commit hook·최소 권한 CI를 구현한다.

## 근거

- `docs/multi-module-migration-template.md`는 사용자 local 문서이지만 현재 공유 document registry에 등록되어 있다.
- 현재 verifier는 skill·agent 형식, Catalog 양방향 관계, history skill·코드 참조 같은 연결 오류를 전부 확인하지 않는다.
- 기존 build workflow는 docs 변경을 건너뛰며 secret과 배포 권한을 함께 가진다.

## 결정

- 공유 document와 개인 local document를 registry 수준에서 분리하고, local document는 팀 공통 정본에서 제외한다.
- SmartTimer의 폭넓은 verifier 계약은 GithubSearch 구조에 맞춰 이식한다.
- hook은 working tree·Git index를 수정하지 않고 staged 지식 변경만 확인하며, 별도 최소 권한 CI가 우회를 보완한다.

## 변경

- `documents.toml`에 `[[local_document]]`를 도입하고 사용자 untracked migration template을 이동했다.
- generator가 shared document만 Index에 내보내도록 명시하고, verifier를 registry·link·skill·agent·history 전수 검사로 확대했다.
- 10개의 dependency-free Python 회귀 검사를 추가했다. local 문서 부재·제외, ID 중복, code path, link, skill·agent 계약, history 참조, hook의 skip/pass/fail을 확인한다.
- AI workflow·두 project skill·quality gate에 grounding과 retrospective의 의무 적용 조건과 hook 설치 방법을 반영했다.
- version-controlled pre-commit hook과 최소 권한 `Knowledge verification` workflow를 추가했다.

## 검증

- `python3 -m unittest discover -s scripts/ai/tests -p 'test_*.py'`: 10 tests 통과.
- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error, 0 warning 통과.
- `python3 -m py_compile scripts/ai/registry.py scripts/ai/generate_knowledge_index.py scripts/ai/verify_knowledge_graph.py scripts/ai/tests/test_verify_knowledge_graph.py`: 통과.
- `ruby -e 'require "yaml"; YAML.load_file(".github/workflows/knowledge.yml")'`: YAML parse 통과.
- `git diff --exit-code -- .github/workflows/build.yml`: 기존 build workflow 무변경 확인.
- `git diff --check`: 통과.
- 앱 코드·Gradle 의존성은 변경하지 않았으므로 Gradle 검증은 실행하지 않았다.

## 지시 이행

- 충족: 사용자 untracked migration template은 내용·추적·stage 상태를 변경하지 않았다.
- 충족: local document는 shared Index, skill·agent 입력, history 공통 참조, 공통 link 검사에서 제외했다.
- 충족: SmartTimer 수준의 registry·skill·agent·history 관계 검증과 회귀 검사를 추가했다.
- 충족: AI skill 의무 적용, read-only hook, 최소 권한 별도 PR CI를 추가했다.
- 충족: 기존 build workflow의 trigger, secret, coverage·Pages 배포 동작은 변경하지 않았다.

## 스킬 평가

- `project-knowledge-grounding`: registry, verifier, workflow, CI의 실제 범위와 개인 문서 제약을 작업 전 근거로 고정하는 데 사용했다.
- `session-retrospective`: history record, generated Index, graph 검증, 테스트, 최종 리스크를 비교하는 데 사용했다.

## 개선 후보

- manual: hook을 사용할 개발자는 `git config --local core.hooksPath .githooks`를 실행해야 한다.
- manual: 첫 PR에서 `Knowledge verification` workflow가 실행되는지 확인한 뒤 repository 관리자가 required status로 지정할 수 있다.
