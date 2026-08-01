# 🔗 DEVELOPMENT_FLOW.md

## 🎯 Mission
GitHub Search 프로젝트의 투명한 이력 관리와 고품질 코드 리뷰 문화를 위해 협업 컨벤션을 엄격히 준수합니다.

---

## 🏗️ Git Commit Convention

커밋 컨벤션의 source of truth는 `docs/ai/android-coding-conventions.md`입니다.

### 1. Commit Message Format
```text
<Type>(Scope) : Subject

Body (선택: 상세 변경 내용 설명)

Tail (선택: 이슈 번호 등)
```

### 2. Type Rules
| Type | 설명 |
| --- | --- |
| **Feat** | 새로운 기능 추가 |
| **Fix** | 버그 수정 |
| **Design** | UI/UX 디자인 변경 |
| **Refactor** | 코드 리팩터링 |
| **Docs** | 문서 수정/추가 |
| **Test** | 테스트 코드 관련 |
| **Chore** | 빌드/설정/의존성 변경 |
| **Rename** | 파일/패키지명 변경 |

---

## 🚀 Pull Request (PR) Strategy

PR 작성 시 다음 항목을 포함하여 리뷰어의 이해를 돕습니다.

1. **Context**: 변경 목적 및 관련 이슈 번호.
2. **Changes**: 기술적인 변경 사항 리스트 (모듈별 구분 권장).
3. **Verification**: `docs/ai/quality-gates.md`에 따른 테스트 방법 및 결과.
4. **Checklist**: Spotless, Compile, Test 통과 여부 확인.

---

## ✅ Pre-Commit Checklist (Manual Review Mode)
1. **AI는 절대로 스스로 커밋하지 않습니다.** 작업 완료 후에는 오직 커밋 메시지만 제안합니다.
2. 사용자는 제안된 코드를 직접 검토하고 터미널에서 수동으로 커밋하거나, AI에게 명시적으로 대행을 요청합니다.
3. 커밋이 기능 단위로 적절히 분리되었는가? (Atomic Commit)
4. 변경 범위에 맞는 검증을 실행했거나, 생략 사유를 명확히 기록했는가?
5. 커밋 메시지가 프로젝트 컨벤션을 따르는가?
6. 파괴적 변경(!BREAKING CHANGE)이 있다면 명시했는가?
