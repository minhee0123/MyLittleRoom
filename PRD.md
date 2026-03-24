# 🏠 [PRD] 마이 리틀 룸 (My Little Room)

## 1. 프로젝트 비전 (Project Vision)
- **슬로건:** "나의 성장이 채우는 작은 공간"
- **핵심 가치:** 갓생(생산성) + 다꾸/방꾸(감성) + 캐릭터 육성(재미)
- **주요 타겟:** 1020 여성 (Gen-Z & Alpha)
- **핵심 목표:** 위젯 중심의 원터치 습관 관리와 시각적 보상을 통한 지속적인 사용자 리텐션 확보

---

## 2. 핵심 기능 요구사항 (Functional Requirements)

### 2.1. 습관 엔진 (Habit Engine)
- **습관 등록:** 이름, 이모지, 요일 반복(매일/주말/특정 요일), 목표 횟수 설정.
- **체크 로직:** 하루 1회 완료 체크. 연속 달성 시 **'스트릭(Streak)'** 카운트 및 경험치 보너스.
- **데이터 동기화:** 로컬 DB(Room KMP) 기반으로 위젯과 앱 간 실시간 정합성 유지.

### 2.2. 게이미피케이션 (Gamification)
- **캐릭터 성장:** 습관 완료 시 EXP 획득. 레벨에 따라 외형 진화 (총 5단계).
- **방 꾸미기:** - 특정 레벨 달성 또는 스트릭 달성 시 '가구 랜덤 박스' 지급.
    - 가구 슬롯 배치: 벽, 바닥, 책상 등 정해진 위치에 가구 배치 및 교체.
- **애니메이션:** Lottie를 활용한 캐릭터 상태 피드백 (달성 시 기쁨, 미달성 시 시무룩).

### 2.3. 초강력 위젯 (Widget-First UX)
- **Android (Jetpack Glance):** - 투명/파스텔 테마 지원.
    - 앱 진입 없이 즉시 체크 가능한 액션 버튼 제공.
- **iOS (SwiftUI WidgetKit):** - 캐릭터 상태 대시보드 및 잔여 습관 카운트 표시.
    - 잠금 화면 위젯 지원 (iOS 16+).

---

## 3. 기술 스택 (Technical Stack)

| 구분 | 기술 | 상세 설명 |
| :--- | :--- | :--- |
| **Framework** | **Kotlin Multiplatform (KMP)** | Android/iOS 로직 공유 |
| **UI Framework** | **Compose Multiplatform (CMP)** | 공통 UI 코드 작성 |
| **Database** | **Room KMP** | 멀티플랫폼 로컬 데이터 저장소 |
| **DI** | **Koin** | KMP 전용 의존성 주입 |
| **Widget** | **Glance (AOS) / WidgetKit (iOS)** | 플랫폼 특화 위젯 구현 |
| **Asset** | **Coil3 & Lottie-CMP** | 이미지 및 애니메이션 처리 |

---

## 4. UI/UX 디자인 가이드

- **디자인 톤:** 키치(Kitsch) & 파스텔(Pastel) 감성.
- **주요 컬러:** - Primary: #E6E6FA (Lavender)
    - Point: #FFB6C1 (Soft Pink), #F0FFF0 (Creamy Mint)
- **인터랙션:** 모든 터치 포인트에 `Haptic Feedback` 및 `Scale-up` 애니메이션 적용.
- **타이포그래피:** Pretendard (Bold/Medium) 기반의 가독성 중심 배치.

---

## 5. 데이터 스키마 (Draft)

- **User:** `id`, `name`, `level`, `exp`, `unlocked_items_json`
- **Habit:** `id`, `title`, `emoji`, `repeat_days`, `last_completed_at`, `streak`
- **Furniture:** `id`, `name`, `category`, `is_unlocked`, `is_placed`

---

## 6. 개발 로드맵 (Roadmap)

1. **Phase 1 (Setup):** KMP 멀티모듈 환경 구축 및 버전 카탈로그 설정.
2. **Phase 2 (Data):** Room KMP 엔티티 정의 및 리포지토리 로직 구현.
3. **Phase 3 (UI):** 공통 테마 설정 및 메인 캐릭터/습관 리스트 화면 개발.
4. **Phase 4 (Widget):** Glance 및 WidgetKit 연동 및 동기화 테스트.
5. **Phase 5 (Polish):** Lottie 애니메이션 및 아이템 획득 로직 고도화.