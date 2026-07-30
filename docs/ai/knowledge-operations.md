# Knowledge Operations

이 문서는 GithubSearch의 문서·코드 근거, 작업 이력, project skill·agent 배선, 세션 회고를 운영하는 primary 규칙이다. 경로·ID·관계의 기계적 정본은 `registry/`다.

## 작업 시작: 문서와 코드 근거

분석·구현·리뷰·문서 작업은 요청과 연결된 최소 문서·코드 근거 묶음을 선택한다. 전체 저장소를 무차별로 읽지 않는다.

1. 요청·제약·시작 커밋을 확인한다.
2. `docs/knowledge/INDEX.md`와 document registry에서 관련 문서 ID와 code path를 고른다.
3. 선택한 문서와 현재 코드·테스트·Gradle 설정을 함께 읽는다.
4. 문서와 코드가 충돌하면 현재 코드와 Git 이력을 재확인한다.
5. 근거가 부족하면 추가 조사하거나 `insufficient context`로 보고하고 추측으로 수정하지 않는다.

문서-only 작업도 관련 코드 영향을 확인한다. 코드가 무관하면 작업 이력에 `code impact: none`과 이유를 남긴다.

## 작업 이력과 보고

Medium·Large·Risky Change, policy·skill·agent 변경은 `docs/history/README.md`의 템플릿으로 작업 레코드를 만든다. 최종 Change Report에는 관련 근거, 지시 이행, skill 평가, 개선 후보를 포함한다.

작업 이력에는 사용자 요구사항·결정·검증의 보안 필터링된 요약만 기록한다. 원문 대화, 내부 추론, secret, token, 개인 정보는 기록하지 않는다.

## 정본과 생성물

| 영역 | 정본 | 생성물 또는 보조 문서 |
| --- | --- | --- |
| 문서·코드 관계 | `registry/documents.toml` | `docs/knowledge/INDEX.md` |
| project skill 경로·상태 | `registry/skills.toml`, 각 `SKILL.md` | `docs/ai/SKILLS_CATALOG.md` |
| custom agent 경로·입력 | `registry/agents.toml`, 각 `.toml` | `docs/ai/subagents.md` |
| 작업·대화 결정 이력 | `docs/history/records/` | 최종 Change Report |
| 정책 | `registry/policies.toml`, `workflows.md`, `quality-gates.md` | 이식 명세와 주제별 가이드 |

생성물은 직접 고치지 않는다. registry를 수정한 뒤 index generator로 재생성하고 graph verifier로 관계를 확인한다.

`documents.toml`의 `[[local_document]]`는 개인 보조 문서의 경로와 목적만 기록한다. local document는 공유 정본이 아니므로 generated Index, project skill·agent 입력, 공통 history `related_documents`, 공통 Markdown link 검증에서 제외한다. 파일이 없는 clean checkout과 CI에서도 오류가 아니며, local document의 내용·Git 추적 상태는 이 체계가 변경하지 않는다.

## 세션 회고와 개선 권한

파일 수정 작업의 종료에는 지시 이행, 근거, 변경, 검증, 사용 skill·agent, 개선 후보를 비교한다.

- 자동: 작업 이력 추가, 생성 index 갱신, 확인된 link·frontmatter 보정
- 검토 후 적용: primary 문서, skill 본문, agent 지시의 최소 수정
- 명시 승인: 삭제·이동, 검증 명령 변경, 보안 정책 변경

한 번의 회고에서 자동 개선 반복은 한 번으로 제한한다. 이 규칙은 기존 위험 변경·secret 보호 규칙보다 우선하지 않는다.
