# GitHub Search Project Context for AI

이 프로젝트는 GitHub 사용자 및 레포지토리를 검색하고 관리하는 안드로이드 앱 `GitHub Search`입니다.

## 🧭 Codex 기준 참조
Codex/agent 작업 기준 source of truth는 **[AGENTS.md](./AGENTS.md)**와 **[AI Docs](./docs/ai/README.md)**입니다. 이 파일은 Gemini/legacy context로 보존하며, 규칙이 충돌할 경우 `AGENTS.md`와 `docs/ai/*`를 우선합니다.

## 🧭 Gemini / Legacy Context
Gemini 계열 도구에서 프로젝트 맥락을 빠르게 파악하기 위한 참고 문서입니다:
1. **[Gemini Adapter Guide](./docs/ai/GEMINI.md)**: Gemini 상호작용 맥락 및 legacy guide.
2. **[Coding Guidelines](./docs/ai/CODING_GUIDELINES.md)**: 기존 AI coding workflow와 품질 통제 참고.
3. **[UI Guidelines](./docs/ai/UI_GUIDELINES.md)**: Compose 설계 원칙 및 Material 3 적용 지침.
4. **[Data & Logic](./docs/ai/DATA_LOGIC_GUIDELINES.md)**: 아키텍처 계층별 구현 표준.

## 🧱 Foundational Documents
프로젝트의 근간이 되는 핵심 문서들입니다:
1. **[Identity & Ethics](./docs/IDENTITY.md)**: 소통 방식 및 AI 제약 사항.
2. **[Conventions](./docs/CONVENTION.md)**: 엄격한 코드 작성 규칙(엘비스 연산자 금지 등).
3. **[Architecture](./docs/ARCHITECTURE.md)**: 멀티 모듈 구조 및 기술 스택 명세.
4. **[Design Guide](./docs/design/DESIGN_GUIDE.md)**: M3 기반 디자인 시스템 표준.
5. **[Development Flow](./docs/workflow/DEVELOPMENT_FLOW.md)**: 커밋 컨벤션 및 PR 전략.
6. **[Testing Guide](./docs/testing/TESTING_GUIDE.md)**: 테스트 프레임워크 및 네이밍 전략.

## Quick Start for AI
- 작업 workflow는 `docs/ai/workflows.md`를 따르십시오.
- verification 판단은 `docs/ai/quality-gates.md`를 따르십시오.
- task scope와 risky change 판단은 `docs/ai/task-scope-control.md`를 따르십시오.
- 최종 응답과 commit message 형식은 `docs/ai/change-report-template.md`와 `docs/ai/android-coding-conventions.md`를 따르십시오.
- **[CRITICAL] 커밋 메시지 규칙**: 커밋 컨벤션의 source of truth는 `docs/ai/android-coding-conventions.md`의 Git Commit Convention을 따르십시오.
- **[CRITICAL] 커밋 전 명시적 승인 필수**: `git commit` 수행 전 반드시 변경 사항을 요약 보고하고, 사용자의 명시적인 승인("네", "진행하세요" 등)을 받은 후에만 별도의 턴에서 커밋을 실행하십시오. 질문과 커밋을 동시에 수행하는 것은 금지됩니다.
