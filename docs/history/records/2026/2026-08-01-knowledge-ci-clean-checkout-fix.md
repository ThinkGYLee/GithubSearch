---
id: GS-2026-0005
status: completed
base_commit: 92a6ab8
related_documents: [quality-gates, knowledge-operations, knowledge-governance-enforcement-plan, history-readme]
related_code: [docs/ai/registry, docs/knowledge, .github/workflows, docs/history]
skills_used: [project-knowledge-grounding, session-retrospective]
---

# 지식 검증 CI의 깨끗한 checkout 보정

## 요청

- PR의 `Knowledge verification` 실패 원인을 분석하고 수정한다.

## 근거

- CI checkout에는 `build/` ignore 규칙으로 추적되지 않는 `docs/build` 참고 문서가 없지만, registry가 이를 공유 `document`로 등록해 generated Index와 graph verifier가 실패했다.
- history record의 `base_commit` 검증에는 과거 Git 객체가 필요하지만, workflow의 기본 shallow checkout에는 최근 커밋만 있다.

## 결정

- Git 추적 대상이 아닌 `docs/build` 참고 문서는 공유 정본이 아닌 `local_document`로 분류한다.
- knowledge workflow만 전체 Git 이력을 checkout하도록 해 history commit 검증 계약을 충족한다.

## 변경

- `project-guide`, `style-guide`를 shared `document`에서 `local_document`로 이동했다. 두 파일은 `build/` ignore 규칙으로 Git 추적 대상이 아니므로 generated Index와 공통 graph 검증에서 제외한다.
- `knowledge.yml` checkout에 `fetch-depth: 0`을 설정해 모든 history record의 `base_commit`을 CI에서도 실제 Git 객체로 검증한다.
- generated knowledge Index와 보강 명세에 clean checkout에서 확인된 local document 분류를 반영했다.

## 검증

- `python3 -m unittest discover -s scripts/ai/tests -p 'test_*.py'`: 11 tests 통과.
- `python3 scripts/ai/generate_knowledge_index.py --check`: 통과.
- `python3 scripts/ai/verify_knowledge_graph.py`: 0 error, 0 warning 통과. shared 33개·local 3개를 확인했다.
- `ruby -e 'require "yaml"; YAML.load_file(".github/workflows/knowledge.yml")'`: workflow YAML parse 통과.
- `git diff --check`: 통과.
- 앱 코드·Gradle 구성은 변경하지 않아 Android build/test는 실행하지 않았다.

## 지시 이행

- 충족: Actions 로그의 두 실패 원인(누락된 local 문서, shallow checkout)을 각각 registry 분리와 full-history checkout으로 수정했다.
- 충족: 기존 build workflow와 사용자 untracked `docs/multi-module-migration-template.md`는 변경·stage·commit하지 않는다.

## 스킬 평가

- `project-knowledge-grounding`: registry, ignore 규칙, generated Index, history 검증 계약과 CI 로그의 관계를 근거로 고정했다.
- `session-retrospective`: history 완료 기록, generated artifact, graph·회귀 검사 및 사용자 local 파일 보존을 종료 전에 점검했다.

## 개선 후보

- automated: PR의 새 `Knowledge verification` 실행이 통과하면 저장소 관리자가 이를 required status로 지정한다.
