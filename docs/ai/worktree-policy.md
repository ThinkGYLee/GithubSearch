# Worktree와 Local Android 환경 정책

이 문서는 Codex App managed worktree와 Local checkout 사이에서 GithubSearch 작업을 안전하게 옮기는 기준이다. 정본은 이 문서이며, 일반 작업 규칙은 `AGENTS.md`, 검증 선택은 `docs/ai/quality-gates.md`를 따른다.

## 기본 원칙

- worktree는 Git tracked 파일만으로 가능한 문서, 코드 조사, secret-free compile·test 작업에 사용한다.
- 현재 ignore된 `local.properties`는 SDK 위치와 credential이 함께 있을 수 있으므로 내용 확인·복사·`.worktreeinclude` 등록을 하지 않는다.
- OAuth 수동 검증, 실제 계정 호출, 기기 실행, signing, `google-services.json`이 필요한 작업은 Local checkout에서 수행한다.
- Codex App의 Handoff를 사용해 chat과 변경을 Local 또는 worktree로 옮긴다. 같은 branch를 두 checkout에 동시에 checkout하지 않는다.

Codex managed worktree는 기본적으로 Git checkout에서 시작하며, `.worktreeinclude`에 적힌 ignore 파일만 추가 복사할 수 있다. 이 repository는 해당 기능을 기본 사용하지 않는다. [Codex Worktrees](https://learn.chatgpt.com/docs/environments/git-worktrees.md)

## 작업 위치 선택

| 작업 종류 | 기본 위치 | 이유 |
| --- | --- | --- |
| 문서, registry, agent policy, 코드 읽기·review | Worktree 또는 Local | tracked 파일만으로 수행 가능 |
| unit test, compile, lint, APK build | Worktree 가능 | Android SDK를 별도로 안전하게 준비하고, 실제 credential이 아닌 placeholder로 충분한 경우에만 수행 |
| OAuth 로그인·토큰 폐기·실제 API 호출 | Local | 실제 credential과 사용자 계정·redirect 환경이 필요 |
| emulator/실기기 확인, signing, Firebase·Google service 확인 | Local | machine-specific 설정 또는 민감한 ignore 파일이 필요할 수 있음 |
| 기존 IDE 상태·실행 중인 앱을 이용한 확인 | Local handoff | 기존 개발 환경을 그대로 사용 |

## Worktree 시작 전 확인

1. 기준 branch와 현재 Local의 unstaged 변경을 확인한다. Local 변경을 포함한 branch에서 worktree를 시작하면 변경도 함께 적용될 수 있다.
2. 작업이 tracked 파일과 secret-free 입력만으로 가능한지 판단한다.
3. 필요한 Android SDK 위치, emulator, credential, signing 설정을 worktree로 복사하지 않아도 되는지 확인한다.
4. branch가 이미 다른 checkout에서 사용 중이면 새 branch를 만들거나 Handoff를 사용한다.

## Worktree에서 허용하는 검증

- 문서 관계 검사: knowledge Index, graph verifier, 정책 script test
- source inspection, formatter·static analysis
- Android compile·unit test·APK build: 사용자가 안전한 SDK 환경을 준비했고 실제 credential이 필요하지 않을 때만 가능
- Phase 2에서 검증한 non-secret placeholder 입력을 사용한 Gradle 검증

worktree에서 Android build가 SDK 경로 또는 local 설정 부재로 실패하면 기존 `local.properties`를 복사하지 않는다. Local handoff를 선택하거나, credential을 포함하지 않는 별도 환경 준비 방법을 저장소 관리자와 검토한다.

## Local handoff 기준

다음 중 하나면 Codex App의 **Hand off → Local**을 사용한다.

- 실제 OAuth 또는 네트워크 인증 흐름을 확인해야 한다.
- emulator·실기기·Android Studio의 기존 상태가 필요하다.
- signing, keystore, Google service 설정 등 ignore된 machine-specific 파일이 필요하다.
- worktree의 branch를 현재 Local checkout에서 직접 사용해야 한다.

Handoff는 Git 작업을 관리하지만 ignore 파일의 내용을 안전한 credential로 바꾸지 않는다. Local에서 검증한 뒤에도 secret 값, token, `local.properties` 내용은 chat·문서·commit에 기록하지 않는다.

## `.worktreeinclude` 금지 기본값과 예외

현재 repository에는 `.worktreeinclude`를 만들지 않는다. 특히 아래 경로·패턴은 등록하지 않는다.

- `local.properties`
- keystore·signing 설정
- `.env*`, token, API key, OAuth credential
- `google-services.json`
- Android SDK·emulator·IDE의 개인 설정

향후 반드시 필요해질 경우에는 별도 Risky Change로 처리한다. 추가 전에 저장소 관리자가 대상이 credential을 포함하지 않는 독립 파일임을 확인하고, 복사 범위·rollback·새 worktree 검증 결과를 작업 이력에 남겨야 한다.

## Branch와 cleanup

- Git branch는 한 checkout에서만 checkout한다. worktree에서 branch를 만들었다면 Local에서 같은 branch를 checkout하지 말고 Handoff 또는 다른 branch를 사용한다.
- managed worktree는 임시 환경으로 취급한다. 커밋·push가 필요한 변경은 branch를 만든 뒤 수행하고, 장기 환경이 필요하면 permanent worktree를 명시적으로 만든다.
- worktree 삭제·정리는 현재 작업과 branch 상태를 확인한 뒤 수행한다. Local의 변경과 ignore 파일을 정리 대상으로 간주하지 않는다.

## 완료 보고

worktree 작업의 Change Report에는 다음을 구분해 적는다.

- 작업 위치: Worktree 또는 Local handoff
- worktree에서 실행한 검증과 결과
- Local에서만 가능한 검증과 그 이유
- secret·ignore 파일을 복사하거나 출력하지 않았다는 확인
- branch 또는 handoff 관련 후속 조치
