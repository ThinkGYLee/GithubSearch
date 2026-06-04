# ♊ GEMINI.md: GitHub Search Gemini Adapter

> **Status:** Gemini / Legacy Context Adapter | **Target:** Gemini Paid Tier Senior Developer
> **Context:** Android 13+ (Min SDK 33) | Target SDK 36 | Kotlin 2.3.21 | JDK 17 | Multi-module

Codex/agent 작업 기준 source of truth는 root `AGENTS.md`와 `docs/ai/*` core 문서입니다. 이 파일은 Gemini 계열 도구를 위한 legacy context adapter로 보존하며, 규칙이 충돌할 경우 아래 문서를 우선합니다.

- Workflow: `docs/ai/workflows.md`
- Verification: `docs/ai/quality-gates.md`
- Task scope / risky change: `docs/ai/task-scope-control.md`
- Coding and commit convention: `docs/ai/android-coding-conventions.md`
- Final response and commit message format: `docs/ai/change-report-template.md`

---

## 1. 🎭 AI Interaction Protocol (기본 규칙)

GitHub Search 프로젝트에서 일관된 품질을 유지하기 위한 **상호작용 표준**입니다.

### 💡 프롬프트 및 응답 원칙
* **명확한 명령형 사용:** `Implement`, `Refactor`, `Fix`, `Review` 등 목적에 맞는 명령어를 최우선으로 사용합니다.
* **언어 및 형식:** 모든 답변과 주석은 **한국어**로 작성하며, 코드 블록 직후에는 반드시 `> [한 줄 요약]`을 추가합니다.
* **No Reduction:** 사용자의 요청 없이 코드를 생략하지 않으며, 전체 맥락을 상세히 제공합니다.

---

## ⚙️ 2. 요청 단계 흐름 (Workflow)

Workflow 판단의 source of truth는 `docs/ai/workflows.md`입니다. 아래 5단계는 Gemini legacy context에서 참고하는 요약입니다.

1. **Analyze**: 요구사항 분석 및 기존 코드의 맥락/제약 조건 확인
2. **Design**: 구현할 구조를 제안하고 사용자 승인을 획득
3. **Implement**: 스타일 가이드 및 아키텍처 원칙에 따른 코드 구현
4. **Verify (Logic Integrity)**: 
    *   **로직 비교 검증**: 리팩터링 전의 '구제 로직(Recovery)', '예외 처리' 규칙이 유실되지 않았는지 `git show HEAD:path` 등을 통해 정밀 대조합니다.
    *   **Hooks**: `.gemini/hooks/pre_commit_logic_check.py`가 필요한 Gemini workflow에서는 보조 검증으로 사용할 수 있습니다.
    *   **Build**: 실제 검증 명령 선택은 `docs/ai/quality-gates.md`를 따릅니다.
5. **Review & Proposal**: 최종 변경 사항 요약 보고는 `docs/ai/change-report-template.md`, 커밋 컨벤션은 `docs/ai/android-coding-conventions.md`를 따릅니다.


---

## 🧩 3. 세부 가이드라인 (Sub-Guides)

작성 지침이 필요한 분야별 전문 가이드라인입니다.

* **🤖 AI 코딩 전략:** [workflows.md](./workflows.md), [task-scope-control.md](./task-scope-control.md), [CODING_GUIDELINES.md](./CODING_GUIDELINES.md)
* **🖼️ UI 및 Compose:** [UI_GUIDELINES.md](./UI_GUIDELINES.md)
* **🧠 데이터 및 로직:** [DATA_LOGIC_GUIDELINES.md](./DATA_LOGIC_GUIDELINES.md)
* **✅ 검증 기준:** [quality-gates.md](./quality-gates.md)
* **📝 최종 보고:** [change-report-template.md](./change-report-template.md)
* **🏗️ 프로젝트 가이드:** [ARCHITECTURE.md](../ARCHITECTURE.md)
* **🎨 디자인 표준:** [DESIGN_GUIDE.md](../design/DESIGN_GUIDE.md)
* **🔗 개발 워크플로우:** [DEVELOPMENT_FLOW.md](../workflow/DEVELOPMENT_FLOW.md)

---

## 🧱 4. 기초 문서 (Foundational Docs)

* **🆔 정체성 및 윤리:** [IDENTITY.md](../IDENTITY.md)
* **✍️ 코딩 컨벤션:** [android-coding-conventions.md](./android-coding-conventions.md), [CONVENTION.md](../CONVENTION.md)
* **🏗️ 아키텍처 명세:** [ARCHITECTURE.md](../ARCHITECTURE.md)
* **🧪 테스트 가이드:** [TESTING_GUIDE.md](../testing/TESTING_GUIDE.md)

---
