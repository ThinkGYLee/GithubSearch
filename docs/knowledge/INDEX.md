# 프로젝트 지식 인덱스

> 이 파일은 `docs/ai/registry/documents.toml`에서 생성됩니다. 직접 수정하지 마세요.

작업 시작 시 요청과 겹치는 문서 및 관련 코드 경로를 함께 읽는다. 문서와 코드가 충돌하면 현재 코드와 Git 이력을 확인하고, 근거가 충분해질 때까지 결론을 보류한다.

## architecture

| ID | 문서 | 권한 | 관련 코드 |
|---|---|---|---|
| `architecture` | [docs/ARCHITECTURE.md](../../docs/ARCHITECTURE.md) | auxiliary | `app, domain, data, feature, core` |
| `data-logic` | [docs/ai/DATA_LOGIC_GUIDELINES.md](../../docs/ai/DATA_LOGIC_GUIDELINES.md) | primary | `domain, data, feature, core` |
| `design-guide` | [docs/design/DESIGN_GUIDE.md](../../docs/design/DESIGN_GUIDE.md) | auxiliary | `app, feature, core` |
| `liquid-navigation-guide` | [docs/design/LIQUID_NAVIGATION_GUIDE.md](../../docs/design/LIQUID_NAVIGATION_GUIDE.md) | auxiliary | `app, core, feature` |
| `project-guide` | [docs/build/PROJECT_GUIDE.md](../../docs/build/PROJECT_GUIDE.md) | primary | `settings.gradle.kts, build-logic, app, domain, data, feature, core` |
| `ui-guidelines` | [docs/ai/UI_GUIDELINES.md](../../docs/ai/UI_GUIDELINES.md) | primary | `app, feature, core` |

## index

| ID | 문서 | 권한 | 관련 코드 |
|---|---|---|---|
| `ai-readme` | [docs/ai/README.md](../../docs/ai/README.md) | primary | `AGENTS.md, .codex/agents, docs/ai` |
| `knowledge-index` | [docs/knowledge/INDEX.md](../../docs/knowledge/INDEX.md) | generated | `docs/ai/registry, scripts/ai` |
| `knowledge-readme` | [docs/knowledge/README.md](../../docs/knowledge/README.md) | primary | `docs/ai/registry, docs` |
| `skills-catalog` | [docs/ai/SKILLS_CATALOG.md](../../docs/ai/SKILLS_CATALOG.md) | primary | `.codex/skills, docs/ai/registry` |
| `subagents` | [docs/ai/subagents.md](../../docs/ai/subagents.md) | primary | `.codex/agents, docs/ai` |

## instruction

| ID | 문서 | 권한 | 관련 코드 |
|---|---|---|---|
| `gemini-adapter` | [docs/ai/GEMINI.md](../../docs/ai/GEMINI.md) | auxiliary | `docs/ai` |
| `root-agents` | [AGENTS.md](../../AGENTS.md) | primary | `app, domain, data, feature, core, build-logic` |

## policy

| ID | 문서 | 권한 | 관련 코드 |
|---|---|---|---|
| `ai-coding-guidelines` | [docs/ai/CODING_GUIDELINES.md](../../docs/ai/CODING_GUIDELINES.md) | auxiliary | `app, domain, data, feature, core` |
| `coding-conventions` | [docs/ai/android-coding-conventions.md](../../docs/ai/android-coding-conventions.md) | primary | `app, domain, data, feature, core, build-logic` |
| `convention` | [docs/CONVENTION.md](../../docs/CONVENTION.md) | auxiliary | `app, domain, data, feature, core` |
| `quality-gates` | [docs/ai/quality-gates.md](../../docs/ai/quality-gates.md) | primary | `build.gradle.kts, .github, scripts/ai` |
| `style-guide` | [docs/build/STYLE_GUIDE.md](../../docs/build/STYLE_GUIDE.md) | auxiliary | `app, domain, data, feature, core` |
| `task-scope` | [docs/ai/task-scope-control.md](../../docs/ai/task-scope-control.md) | primary | `app, domain, data, feature, core, build-logic` |
| `template-sync-policy` | [docs/ai/template-sync-policy.md](../../docs/ai/template-sync-policy.md) | primary | `docs, .codex` |
| `testing-guide` | [docs/testing/TESTING_GUIDE.md](../../docs/testing/TESTING_GUIDE.md) | primary | `domain, data, feature, core/testing` |

## reference

| ID | 문서 | 권한 | 관련 코드 |
|---|---|---|---|
| `identity` | [docs/IDENTITY.md](../../docs/IDENTITY.md) | auxiliary | `docs` |

## roadmap

| ID | 문서 | 권한 | 관련 코드 |
|---|---|---|---|
| `jacoco-setup-plan` | [docs/plan/JACOCO_SETUP_PLAN.md](../../docs/plan/JACOCO_SETUP_PLAN.md) | auxiliary | `build.gradle.kts, app, domain, data, feature, core` |
| `knowledge-governance-enforcement-plan` | [docs/ai/knowledge-governance-enforcement-plan.md](../../docs/ai/knowledge-governance-enforcement-plan.md) | primary | `docs, scripts/ai, .codex, .github` |
| `multi-module-refactoring-plan` | [docs/plan/MULTI_MODULE_REFACTORING_PLAN.md](../../docs/plan/MULTI_MODULE_REFACTORING_PLAN.md) | auxiliary | `settings.gradle.kts, build-logic, app, domain, data, feature, core` |
| `reporting-system-migration-plan` | [docs/ai/reporting-system-migration-plan.md](../../docs/ai/reporting-system-migration-plan.md) | primary | `docs, scripts/ai, .codex` |
| `screenshot-test-plan` | [docs/testing/SCREENSHOT_TEST_IMPLEMENTATION_PLAN.md](../../docs/testing/SCREENSHOT_TEST_IMPLEMENTATION_PLAN.md) | primary | `gradle, feature/setting, core/designsystem, .github` |
| `type-safe-navigation-plan` | [docs/plan/TYPE_SAFE_NAVIGATION_MIGRATION_PLAN.md](../../docs/plan/TYPE_SAFE_NAVIGATION_MIGRATION_PLAN.md) | auxiliary | `app, feature` |
| `unit-test-improvement-plan` | [docs/plan/UNIT_TEST_IMPROVEMENT_PLAN.md](../../docs/plan/UNIT_TEST_IMPROVEMENT_PLAN.md) | auxiliary | `domain, data, feature, core/testing` |

## template

| ID | 문서 | 권한 | 관련 코드 |
|---|---|---|---|
| `change-report` | [docs/ai/change-report-template.md](../../docs/ai/change-report-template.md) | primary | `AGENTS.md, docs/history, scripts/ai` |
| `knowledge-governance-enforcement-prompt` | [docs/ai/knowledge-governance-enforcement-prompt.md](../../docs/ai/knowledge-governance-enforcement-prompt.md) | primary | `docs, scripts/ai, .codex, .github` |

## workflow

| ID | 문서 | 권한 | 관련 코드 |
|---|---|---|---|
| `development-flow` | [docs/workflow/DEVELOPMENT_FLOW.md](../../docs/workflow/DEVELOPMENT_FLOW.md) | auxiliary | `.github, AGENTS.md` |
| `history-readme` | [docs/history/README.md](../../docs/history/README.md) | primary | `docs/history, scripts/ai` |
| `knowledge-operations` | [docs/ai/knowledge-operations.md](../../docs/ai/knowledge-operations.md) | primary | `docs, scripts/ai, .codex/agents` |
| `workflows` | [docs/ai/workflows.md](../../docs/ai/workflows.md) | primary | `docs, scripts/ai, .codex` |
