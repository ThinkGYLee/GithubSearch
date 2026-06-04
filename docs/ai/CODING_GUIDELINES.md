# 🤖 AI_CODING_GUIDELINES.md

## 1. 🧠 사고 및 통신 원칙 (Communication Principles)

GitHub Search 프로젝트의 AI는 답변을 생성하기 전 다음 지침을 내부적으로 먼저 검토합니다.

### 💡 프롬프트 해석 가이드
* **Zero-Inference (추측 금지):** 요구사항이 모호한 경우 추측하여 코딩하지 말고, 반드시 사용자에게 질문하여 맥락을 확정합니다.
* **Surgical Updates:** 기존 아키텍처와 컨벤션을 엄격히 준수하며 필요한 부분만 정밀하게 수정합니다.

---

## ⚙️ 2. Core Docs Reference

공통 workflow와 검증 정책은 core docs를 source of truth로 둡니다. 이 문서는 GithubSearch 프로젝트에서 특히 확인해야 할 coding quality checklist만 보존합니다.

- Workflow trigger와 공통 작업 흐름: `docs/ai/workflows.md`
- Verification 명령 선택과 결과 보고: `docs/ai/quality-gates.md`
- 작업 범위, 사용자 승인, risky change 판단: `docs/ai/task-scope-control.md`
- Kotlin/Android coding convention: `docs/ai/android-coding-conventions.md`
- 최종 응답과 commit message 형식: `docs/ai/change-report-template.md`

---

## 🛠 3. AI 전용 파워 툴 활용

* **최신성 유지 (`google_web_search`):** AndroidX, Compose 등 라이브러리 업데이트가 잦은 분야는 실시간 검색을 통해 최신 API를 확인합니다.
* **아키텍처 일관성 확인:** 특정 파일만 보는 것이 아니라, 관련 모듈(`feature`, `domain`, `data`, `core`) 전체를 훑어 의존성과 책임 경계가 유지되는지 확인합니다.

---

## 🧩 4. GithubSearch Quality Checklist

AI가 스스로 작성한 코드를 검열하는 프로젝트 특화 기준입니다.
* **Single Responsibility:** 하나의 클래스나 Composable이 너무 많은 역할을 수행하지 않는가?
* **Error Resilience:** 실패 경로가 명시적으로 처리되고, 에러 상태가 UI/Domain/Data 계층 경계에 맞게 전달되는가?
* **Clean Dependency:** 상위 레이어가 하위 레이어의 상세 구현에 의존하지 않는가?
* **Module Boundary:** `feature`, `domain`, `data`, `core` 모듈의 책임과 의존 방향이 유지되는가?

---
