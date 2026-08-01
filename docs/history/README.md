# 작업 이력

`records/YYYY/`에는 Medium·Large·Risky Change 또는 policy·skill·agent 변경마다 하나의 Markdown 레코드를 둔다. Git은 실제 diff와 커밋 이력을 보존하고, 이 디렉터리는 요청·근거·결정·검증·회고의 연결을 보존한다.

Small 작업은 작업 이력을 생략할 수 있다. 여러 세션을 거치거나 다음 작업의 판단 근거가 되는 Small 작업은 기록할 수 있다.

원문 대화, 내부 추론, secret·token·개인 정보는 기록하지 않는다. 대신 사용자 요구사항과 결정 사항을 재현 가능한 요약으로 남긴다.

## 파일 이름과 상태

- 파일 이름: `YYYY-MM-DD-<task-slug>.md`
- 작업 ID: `GS-YYYY-NNNN`
- 상태: 시작은 `in_progress`, 정상 종료는 `completed`, 중단·포기는 `interrupted`

다음 작업은 기존 `in_progress` 레코드를 먼저 확인하고 재개 또는 중단 사유를 정리한다.

## 템플릿

```md
---
id: GS-YYYY-NNNN
status: in_progress
base_commit: <git commit>
related_documents: [document-id]
related_code: [path]
skills_used: [skill-id]
---

# 작업 제목

## 요청

## 근거

## 결정

## 변경

## 검증

## 지시 이행

## 스킬 평가

## 개선 후보
```

`completed` 레코드는 모든 섹션을 채워야 하며, Phase 3 도입 뒤에는 `python3 scripts/ai/verify_knowledge_graph.py`도 통과해야 한다.
