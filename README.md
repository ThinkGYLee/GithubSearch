# Github Search
채용 과제로 많이 나오는 항목들을 구현하기 위한 테스트 앱입니다.

## 주요기능
- 깃허브 Api를 이용한 유저 검색
- 즐겨찾기
- 깃허브 OAuth를 이용한 인증, 로그인
- 네트워크 콜 최적화
- 페이징

## 기술 스택
 구분 | 내용
-- | --
Architecture | MVVM, 안드로이드 권장 아키텍쳐
Jetpack | Navigation, Compose, Lifecycle, ViewModel, Room, Paging3
Network | Retrofit, OkHttp, Gson
Asynchronous Processing | Coroutine, Flow
Dependency Injection | Hilt
Third Party Library | Glide

## 스크린샷
| 목록 | 상세 | 검색 |
| --- | --- | --- |
| <img src="https://github.com/user-attachments/assets/4c1061cd-1ad6-4e81-b1a2-b1ee4a620191" width="200"/> | <img src="https://github.com/user-attachments/assets/608b71c3-7129-48fe-97d7-f6e18df4509b" width="200"/> | <img src="https://github.com/user-attachments/assets/ccd59b9f-fea9-429e-8a3c-cc8af3b2bb68" width="200"/> |

| 즐겨찾기 | 인증 | 설정 |
| --- | --- | --- |
| <img src="https://github.com/user-attachments/assets/ca90974a-b0e2-45eb-9737-80229fcca313" width="200"/> | <img src="https://github.com/user-attachments/assets/92cbf7d1-c70f-42a4-b5a1-36291caa2538" width="200"/> | <img src="https://github.com/user-attachments/assets/9bc4015e-0ce5-4e06-ab15-5a8f8a3184cc" width="200"/> |

## Architecture
![img](https://lh6.googleusercontent.com/jIm6sL0mqukk0OROYyStYNsBulEFLZki-z2Y9OD73K-cpvEre-VP1wmdSC-bDpNJrGdhB4bOZbABRspBcn4FJCtJs4uQKKwWesOdThS-B75HwnCdTCqEKXAClxOimOtIu9WbabaP_Mpel6dDpLSSQVk)

### Module
본 프로젝트는 Single-module 구조입니다.

