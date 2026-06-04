# Change Report Template

이 문서는 Codex의 최종 응답 형식의 source of truth이며, 커밋 메시지나 PR 설명으로 재사용할 수 있게 정리하는 형식을 정의한다. Verification 판단은 `docs/ai/quality-gates.md`, Task Scope 판단은 `docs/ai/task-scope-control.md`를 source of truth로 둔다. workflow별 요구사항은 Change Report 안에 통합한다.

Template metadata는 `docs/ai/README.md`를 따른다.

## Change Report Format

모든 파일 수정 작업의 최종 응답에는 `Change Report`와 `Suggested Commit Message`를 기본 포함한다. 문서-only 변경도 예외 없이 commit message 후보를 포함한다. `Suggested PR Description`은 필요할 때만 추가한다.

```text
Change Report:
- Summary:
  - <1~3 bullet summary>
- Changed Files:
  - <file path>: <short reason>
- Task Scope:
  - Size: <Small / Medium / Large>
  - Files changed: <number>
  - Risky change: <yes / no>
  - Notes: <short explanation>
- Verification:
  - Command: <command or not run>
  - Result: <passed / failed / not run>
  - Notes: <short explanation>
- Risks:
  - <remaining risks or "None identified">
- Follow-up:
  - <optional follow-up or "None">

Suggested Commit Message:
- Full Message Line: `<Type>(Scope) : Subject`
- Type: <Feat / Fix / Design / Refactor / Docs / Test / Chore / Rename>
- Scope: <short change area>
- Subject: <short imperative summary>
- Body:
  - <why changed>
  - <what changed>
  - <verification>
```

## Suggested Commit Message

```text
Suggested Commit Message:
- Full Message Line: `<Type>(Scope) : Subject`
- Type: <Feat / Fix / Design / Refactor / Docs / Test / Chore / Rename>
- Scope: <short change area>
- Subject: <short imperative summary>
- Body:
  - <why changed>
  - <what changed>
  - <verification>
```

## Suggested PR Description

```text
## Summary
- <summary item>
- <summary item>

## Changes
- <change item>
- <change item>

## Verification
- <command/result>

## Risks
- <risk or none>

## Notes
- <optional notes>
```

## Git Commit Convention

Commit convention의 source of truth는 `docs/ai/android-coding-conventions.md`다.

- Format: `<Type>(Scope) : Subject`
- Type: `Feat`, `Fix`, `Design`, `Refactor`, `Docs`, `Test`, `Chore`, `Rename`
- Scope는 변경 영역을 짧게 나타낸다.
- Subject는 영어 명령형으로 작성하고 50자 이내로 유지한다.
- Subject 끝에 마침표를 붙이지 않는다.
- Body는 선택이며, 작성 시 한국어로 변경 이유와 핵심 내용을 설명한다.
