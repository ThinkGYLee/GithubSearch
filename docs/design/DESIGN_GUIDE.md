# 🎨 DESIGN_GUIDE.md

## 🎯 Mission
GitHub Search의 디자인은 개발자 친화적이고 직관적이어야 합니다. 구글의 최신 디자인 시스템인 Material 3를 기반으로 사용자에게 현대적이고 생동감 넘치는 경험을 제공합니다.

---

## 🏗️ Design System Standards

### 1. Material 3 & Expressive
* **적극적 도입**: Material 3 (M3) 디자인 가이드라인을 프로젝트 전반에 적극적으로 적용합니다.
* **표현력 강화**: 사용자 인터페이스 전반에 걸쳐 생동감 있는 색상, 역동적인 모션, 그리고 풍부한 타이포그래피를 사용하여 서비스의 개성을 드러냅니다.

### 2. Component Design Principles
* **일관성**: 모든 UI 컴포넌트(Card, Button, Sheet 등)는 Material 3 규격을 따르며, 서비스 전반에서 일관된 스타일을 유지합니다.
* **유연성**: 다양한 화면 크기와 사용자 설정(다크 모드 등)에 유연하게 대응할 수 있도록 설계합니다.

---

## 🌈 Theme & Styling

### 1. Color Palette
* **Theme Tokens**: 하드코딩된 색상 코드 대신 `MaterialTheme.colorScheme`의 토큰을 사용하여 테마에 따른 자동 색상 전환을 지원합니다.
* **Accessibility**: 배경과 텍스트 간의 충분한 대비를 확보하여 모든 사용자가 정보를 쉽게 읽을 수 있도록 합니다.

### 2. Typography
* **Semantic Usage**: `Display`, `Headline`, `Title`, `Body`, `Label` 등 M3의 의미적 타이포그래피 체계를 목적에 맞게 사용합니다.

---

## ✅ Final Compliance Checklist
1. 모든 UI 요소가 Material 3 디자인 원칙을 따르고 있는가?
2. 테마 토큰을 사용하여 Light/Dark 모드에 완벽히 대응하는가?
