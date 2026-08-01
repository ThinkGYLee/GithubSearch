# AI Workflows

이 문서는 AI 작업 흐름에서 공통으로 적용할 규칙을 둔다.

## Source Of Truth

이 문서는 `android-docs-bootstrap` skill 템플릿에서 생성될 수 있지만, 현재 repository에 import된 뒤에는 프로젝트 적용본으로 관리한다. 템플릿 원본은 skill에 있고, 실제 적용 규칙은 이 파일과 관련 `docs/ai` 문서를 따른다.

Template metadata는 `docs/ai/README.md`를 따른다.

워크플로우 이름은 Codex CLI의 내장 명령이 아니다. 사용자의 자연어 요청이 아래 trigger examples와 비슷하면 대응되는 workflow를 적용한다.

## 공통 검증 규칙

파일을 수정하는 workflow는 `docs/ai/quality-gates.md`를 따른다. 코드 수정 작업은 적절한 verification step으로 끝나야 하며, 문서-only 변경은 Gradle 검증을 생략할 수 있다.

## 공통 작업 범위 규칙

모든 workflow는 `docs/ai/task-scope-control.md`의 작업 크기와 위험 변경 규칙을 따른다. Large Task 또는 Risky Change는 바로 수정하지 않고 영향 범위와 계획을 먼저 제시한다.

## 문서·코드 근거와 작업 이력

단순 질의와 짧은 상태 응답을 제외한 모든 project 작업은 시작 시 `project-knowledge-grounding`을 적용해 `knowledge-operations.md`의 관련 문서와 코드·테스트·설정을 함께 확인한다. 파일을 수정하는 모든 project 작업은 종료 전에 `session-retrospective`를 적용한다. Medium·Large·Risky Change와 policy·skill·agent 변경은 작업 시작 시 history record를 만들고, 종료 시 지시 이행·검증·개선 후보를 갱신한다.

근거가 불충분하거나 문서와 코드가 충돌하면 추측으로 결론을 내리거나 수정하지 않는다. 추가 조사 또는 `insufficient context` 보고를 선택한다.

## 공통 최종 보고 규칙

파일을 수정하는 모든 workflow의 최종 응답은 `docs/ai/change-report-template.md`의 Change Report 형식을 따른다. workflow별 응답 요구사항은 Change Report 안에 통합하며, 분석-only 작업이나 문서-only 작업은 필요한 섹션만 간소화할 수 있다.

## Workflow Triggers

- Analysis Workflow: `수정하지 말고 원인만 분석해줘.`
- Fix Workflow: `빌드 에러를 최소 변경으로 고쳐줘.`
- Review Workflow: `현재 변경사항을 리뷰해줘.`
- Compose Workflow: `Composable recomposition 관점에서 개선해줘.`
- Flow Workflow: `ViewModel 상태 흐름을 점검해줘.`
- Data Logic Workflow: `Repository와 UseCase 경계를 확인해줘.`
- Room Workflow: `Room migration 필요 여부를 먼저 분석해줘.`
- Gradle Workflow: `Gradle/KSP/Hilt 빌드 오류를 확인해줘.`
- Documentation Workflow: `문서 작업으로 진행해줘.`
- Docs Safe Sync Workflow: `android-docs-bootstrap 기준으로 safe sync 해줘.`

## Review Workflow

- review 시작 전에 base branch, 특정 commit, working tree diff, 요청 파일 중 하나로 범위를 선언한다.
- `docs/ai/code-review.md`의 P0~P2 severity, evidence, false-positive, test-gap 기준을 적용한다.
- 관련 primary document와 코드·테스트 근거를 함께 확인하고, verdict·scope·evidence·finding·후속 검증·uncertainty를 결과에 남긴다.
- finding이 없으면 `pass`와 검토 범위를 명시한다. style 선호만으로 finding을 만들지 않는다.
- reviewer 결과는 read-only 근거이며, main agent 또는 사람이 최종 적용·검증·merge 판단을 수행한다.

## Documentation Workflow

- 기존 문서를 덮어쓰기 전에 현재 파일을 먼저 읽는다.
- legacy/custom docs는 삭제하지 않고 보존한다.
- README, root AI guide, coding convention처럼 의미 변경 위험이 있는 파일은 요청 범위에 있을 때만 수정한다.
- 문서-only 변경은 Gradle 검증을 생략할 수 있으며, 대신 생성/수정 파일 목록과 metadata 여부를 확인한다.
