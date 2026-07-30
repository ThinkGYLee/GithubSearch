# 지식 운영 강제 체계 실행 프롬프트

아래 프롬프트는 [지식 운영 강제 체계 보강 명세](knowledge-governance-enforcement-plan.md)의 구현을 위한 실행 기준이다. 실행자는 먼저 명세를 읽고, 명세와 충돌하는 임의 확장을 하지 않는다.

```text
GithubSearch 저장소에서 `docs/ai/knowledge-governance-enforcement-plan.md`를 구현해 주세요.

목표는 문서·project skill·custom agent·작업 이력의 관계를 신뢰할 수 있게 만들고, AI 작업 절차·로컬 pre-commit hook·PR CI가 같은 검증 기준을 사용하게 하는 것입니다.

작업 전 반드시 다음을 읽으세요.
- AGENTS.md
- docs/ai/workflows.md
- docs/ai/knowledge-operations.md
- docs/ai/quality-gates.md
- docs/ai/knowledge-governance-enforcement-plan.md
- docs/ai/registry/*.toml
- scripts/ai/{registry,generate_knowledge_index,verify_knowledge_graph}.py
- .github/workflows/build.yml

시작 시 `project-knowledge-grounding`을 적용하고, Medium/Risky policy·skill·CI 작업이므로 `docs/history/records/2026/`에 in_progress record를 만드세요. 파일 수정 종료 전에는 `session-retrospective`를 적용하세요.

다음 순서를 지키세요.

1. P0 — 로컬 보조 문서
   - `docs/multi-module-migration-template.md`의 내용·추적 상태·stage 상태를 절대 바꾸지 마세요.
   - registry에 `[[local_document]]` 개념을 도입하고 기존 migration template을 이 분류로 옮기세요.
   - local document는 shared document와 ID가 중복되면 안 되지만, 파일이 없는 clean checkout/CI에서는 오류가 아니어야 합니다.
   - local document는 generated `docs/knowledge/INDEX.md`, history `related_documents`, skill/agent `required_documents`, 공통 link 검증 대상에서 제외하세요.
   - generator와 verifier의 document coverage가 이 규칙을 정확히 따르게 하세요.

2. P1 — graph verifier 전수 강화
   - SmartTimer의 verifier가 확인하는 다음 계약을 GithubSearch 구조에 맞게 구현하세요: registry ID 중복, shared document code path, Markdown link, skill frontmatter·directory 이름·Catalog 양방향 일치, agent TOML 형식, history ID·상태·commit·관련 문서·skill·코드 경로·완료 섹션.
   - 외부 Python dependency를 추가하지 마세요.
   - 각 새 검증 항목은 정상 사례와 실패 사례가 있는 dependency-free 회귀 검사로 보호하세요.
   - 현재 repository를 전수 검사해서 새 검증에서 발견한 실제 연결 오류는 함께 고치되, 요청 범위 밖 문서의 의미나 사용자 변경을 임의로 바꾸지 마세요.

3. P2 — AI 절차와 hook
   - AGENTS.md, workflows, knowledge operations, 두 project skill의 책임을 비교해 단순 질의·짧은 상태 응답을 제외한 project 작업은 grounding skill을, 파일 수정 작업은 retrospective skill을 적용하도록 명확히 정렬하세요.
   - `.githooks/pre-commit`과 `scripts/git-hooks/run-staged-knowledge-check.sh`를 추가하세요. repository-local 설치 방법만 문서화하고, 사용자의 global Git 설정을 바꾸거나 hook을 자동 설치하지 마세요.
   - staged `AGENTS.md`, docs Markdown, AI registry TOML, project skill·agent, `scripts/ai` 변경에서만 generator `--check`와 verifier를 실행하세요.
   - hook은 파일을 수정하거나 자동 stage하지 않아야 합니다. 무관한 staged 변경은 즉시 성공해야 합니다.

4. P2 — 별도 CI
   - `.github/workflows/knowledge.yml`을 추가하세요.
   - PR 및 main/develop push에서 knowledge 관련 경로가 바뀔 때만 실행하고, 권한은 `contents: read`만 사용하세요.
   - secret, Pages, coverage comment, Gradle build를 사용하지 말고 generator `--check`와 verifier만 실행하세요.
   - 기존 `.github/workflows/build.yml`의 trigger, secret 처리, 권한, coverage·Pages 배포는 바꾸지 마세요.
   - GitHub required status/branch protection은 원격 저장소 설정이므로 변경하지 말고, 최종 보고의 수동 후속 조치로만 남기세요.

5. 검증과 보고
   - 다음을 실행하고 결과를 보고하세요.
     - `python3 scripts/ai/generate_knowledge_index.py --check`
     - `python3 scripts/ai/verify_knowledge_graph.py`
     - 새 dependency-free 회귀 검사
     - `git diff --check`
   - 가능하면 local document가 없는 임시 환경에서도 verifier가 통과하는 것을 확인하세요.
   - 작업 이력을 completed로 전환하고 generated Index를 갱신하세요.
   - 커밋·push·PR 생성은 사용자가 명시적으로 요청할 때만 하세요.

제약:
- 앱 코드, Gradle 의존성, screenshot CI는 수정하지 않습니다.
- 기존 build workflow의 secret 및 배포 동작을 넓히거나 약화하지 않습니다.
- `docs/multi-module-migration-template.md`는 어떤 경우에도 수정·stage·commit하지 않습니다.
- SmartTimer의 개인 절대 경로 hook 구현은 복사하지 않습니다. 원칙만 GithubSearch에 맞게 적용합니다.
- 예상 밖의 기존 사용자 변경을 발견하면 보존하고, 충돌이 있을 때만 멈춰 보고하세요.

최종 응답은 Change Report 형식으로 변경 파일, P0/P1/P2 이행 결과, 검증 결과, 남은 리스크를 간결히 정리하세요.
```
