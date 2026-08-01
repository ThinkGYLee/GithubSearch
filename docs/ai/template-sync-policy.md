# Template Sync Policy

## 역할

- skill의 `assets/templates`는 보일러플레이트 원본이다.
- project docs는 적용본이며 프로젝트 상황에 맞게 수정될 수 있다.
- skill template 업데이트가 기존 프로젝트에 자동 반영되는 것은 아니다.
- bootstrap 실행 절차는 `android-docs-bootstrap` skill의 `SKILL.md`가 담당한다.

## Sync 원칙

- 기존 프로젝트에 새 template 변경을 반영할 때는 diff를 먼저 확인한다.
- 필요한 부분만 가져오고, 프로젝트 고유 규칙은 project docs에 남긴다.
- 기존 프로젝트는 최신 template으로 자동 덮어쓰지 않는다.
- sync는 파일별 diff 기반으로 수행한다.
- legacy docs는 삭제하기 전에 현재 프로젝트 관련성을 먼저 분류한다.
- 모든 Android 프로젝트에 재사용할 규칙은 skill template으로 승격한다.
- 개인 공통 원칙은 global `AGENTS.md`에 남긴다.

## Update Decision Rule

- 모든 프로젝트에 적용되는 개인 습관이면 global `AGENTS.md`에 둔다.
- Android 프로젝트 초기 세팅마다 가져갈 보일러플레이트면 skill template에 둔다.
- 현재 repository에만 해당하면 project docs에 둔다.
- 반복 가능한 작업 절차면 skill에 둔다.
- 현재 repository의 실제 명령어와 모듈 구조면 project docs에 둔다.

## Safe Sync Policy

- 누락된 core docs는 생성할 수 있다.
- 기존 `GEMINI.md`, `docs/CONVENTION.md`, `docs/workflow/DEVELOPMENT_FLOW.md`, 기존 `docs/ai/*_GUIDELINES.md`는 명시 요청 없이는 수정하지 않는다.
- README의 stale 문구, commit convention 충돌, CI/local 검증 명령 불일치는 별도 follow-up으로 남기고 safe sync 범위에서 수정하지 않는다.
- 기존 작업트리 변경은 되돌리거나 덮어쓰지 않는다.

## Bootstrap 연결

상세 bootstrap checklist는 `android-docs-bootstrap` skill의 `SKILL.md`를 기준으로 한다. 이 문서는 template 변경을 기존 project docs에 반영할지, project-specific rule로 남길지, skill template으로 승격할지 판단하는 기준만 관리한다.
