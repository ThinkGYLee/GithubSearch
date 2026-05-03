# 🤖 AI_CODING_GUIDELINES.md

## 1. 🧠 사고 및 통신 원칙 (Communication Principles)

GitHub Search 프로젝트의 AI는 답변을 생성하기 전 다음 지침을 내부적으로 먼저 검토합니다.

### 💡 프롬프트 해석 가이드
* **Zero-Inference (추측 금지):** 요구사항이 모호한 경우 추측하여 코딩하지 말고, 반드시 사용자에게 질문하여 맥락을 확정합니다.
* **Surgical Updates:** 기존 아키텍처와 컨벤션을 엄격히 준수하며 필요한 부분만 정밀하게 수정합니다.

---

## ⚙️ 2. 4단계 추론 워크플로우 (The 4-Step Thinking)

모든 코딩 작업은 아래의 논리적 단계를 생략하지 않고 수행합니다.

| 단계 | 명칭 | AI의 액션 |
| --- | --- | --- |
| **1단계** | **Analyze** | 요구사항과 관련된 모듈(`feature`, `domain`, `data` 등)의 코드를 검색하고 의존성을 파악합니다. |
| **2단계** | **Design** | 구현할 클래스/인터페이스 구조를 텍스트로 먼저 제안하여 사용자 승인을 받습니다. |
| **3단계** | **Implement** | 스타일 가이드 및 `CONVENTION.md`에 따라 코드를 작성합니다. |
| **4단계** | **Verify** | Hooks를 통해 Spotless, Compile, Unit Test를 자동으로 실행하여 검증합니다. |

---

## 🛠 3. AI 전용 파워 툴 활용

* **최신성 유지 (`google_web_search`):** AndroidX, Compose 등 라이브러리 업데이트가 잦은 분야는 실시간 검색을 통해 최신 API를 확인합니다.
* **대규모 코드 분석:** 특정 파일만 보는 것이 아니라, 관련 모듈 전체를 훑어 아키텍처 일관성을 유지합니다.

---

## 🧩 4. 코드 품질 통제 (Quality Control)

AI가 스스로 작성한 코드를 검열하는 기준입니다.
* **Single Responsibility:** 하나의 클래스나 Composable이 너무 많은 역할을 수행하지 않는가?
* **Error Resilience:** `try-catch` 및 명시적 에러 처리가 되어 있는가? (`runCatching` 금지 규칙 준수)
* **Clean Dependency:** 상위 레이어가 하위 레이어의 상세 구현에 의존하지 않는가?

---
