# My Little Room - 프로젝트 매뉴얼

## 1. 프로젝트 개요

**My Little Room**은 습관 추적 + 방 꾸미기 게이미피케이션 앱입니다.
타겟 유저는 10~20대 여성이며, 매일 습관을 체크하면 경험치를 얻고 캐릭터가 성장하며 가구를 획득하여 방을 꾸밀 수 있습니다.

| 항목 | 내용 |
|------|------|
| **플랫폼** | Android / iOS (Kotlin Multiplatform) |
| **UI 프레임워크** | Compose Multiplatform |
| **아키텍처** | MVVM (ViewModel + StateFlow + Compose) |
| **DI** | Koin |
| **로컬 DB** | Room KMP |
| **최소 지원** | Android SDK 26 / iOS 15+ |

---

## 2. 모듈 구조

```
MyLittleRoom/
├── shared/              # 공유 모듈 (데이터, 도메인, DI)
│   ├── commonMain/      # 플랫폼 공통 코드
│   ├── androidMain/     # Android 전용 구현
│   └── iosMain/         # iOS 전용 구현
├── composeApp/          # 앱 모듈 (UI, ViewModel, 내비게이션)
│   ├── commonMain/      # 공통 UI
│   ├── androidMain/     # Android 전용 (위젯, Application 등)
│   └── iosMain/         # iOS 전용 (MainViewController)
├── iosApp/              # Xcode 네이티브 iOS 래퍼
│   └── MyLittleRoomWidget/  # iOS WidgetKit 확장
└── gradle/              # 버전 카탈로그 (libs.versions.toml)
```

---

## 3. 핵심 의존성

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Kotlin | 2.1.10 | 언어 |
| Compose Multiplatform | 1.8.0 | UI 프레임워크 |
| Room | 2.7.1 | 로컬 데이터베이스 |
| Koin | 4.0.2 | 의존성 주입 |
| kotlinx-datetime | 0.6.1 | 날짜/시간 처리 |
| kotlinx-serialization | 1.7.3 | 직렬화 |
| Navigation Compose | 2.8.0-alpha10 | 화면 내비게이션 |
| Jetpack Glance | 1.1.1 | Android 홈 위젯 |
| KSP | 2.1.10-1.0.29 | Room 코드 생성 |

---

## 4. 데이터 레이어

### 4.1 Entity (테이블)

#### HabitEntity (`habits`)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | Long (PK, auto) | 습관 ID |
| `title` | String | 습관 이름 |
| `emoji` | String | 대표 이모지 |
| `repeatDays` | String | 반복 요일 CSV (0=월~6=일) |
| `createdAt` | Long | 생성 시각 (epoch ms) |

#### HabitLogEntity (`habit_logs`)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| `habitId` | Long (PK, FK) | 습관 ID |
| `completedDate` | String (PK) | 완료 날짜 (ISO: "2026-04-07") |

#### UserStatusEntity (`user_status`)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | Int (PK) | 항상 1 (싱글톤) |
| `level` | Int | 현재 레벨 |
| `currentExp` | Int | 현재 경험치 |

#### FurnitureEntity (`furniture`)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | String (PK) | 가구 고유 ID |
| `name` | String | 가구 이름 |
| `category` | String | 카테고리 |
| `isUnlocked` | Boolean | 잠금 해제 여부 |
| `isPlaced` | Boolean | 배치 여부 |
| `slotPosition` | String? | 배치 슬롯 위치 |

### 4.2 DAO (Data Access Object)

| DAO | 주요 메서드 |
|-----|-----------|
| `HabitDao` | `insert`, `update`, `delete`, `getAllHabits()` (Flow), `getHabitById()`, `getAllHabitsOnce()` |
| `HabitLogDao` | `insertLog`, `deleteLog`, `isCompletedOn()`, `getLogsForDate()`, `getLogDatesForHabit()` |
| `UserStatusDao` | `upsert`, `getUserStatus()` (Flow), `updateLevelAndExp()`, `getUserStatusOnce()` |
| `FurnitureDao` | `insertAll`, `getUnlockedFurniture()` (Flow), `getPlacedFurniture()` (Flow), `placeFurniture()`, `getLockedFurniture()` |

### 4.3 Repository

| Repository | 역할 |
|-----------|------|
| `HabitRepository` | 습관 CRUD, 완료 토글, 연속일수 계산 |
| `UserRepository` | 유저 상태 관리, 경험치 추가 및 레벨업 판정 |
| `FurnitureRepository` | 가구 초기화(8개 기본), 배치, 잠금 해제, 랜덤 언락 |

---

## 5. 도메인 레이어

### 5.1 게이미피케이션 엔진

**경험치 공식:**
```
EXP = 10(기본) + min(연속일수 × 2, 20)
```

**레벨업 공식:**
```
필요 EXP = 현재 레벨 × 100
```

예시: Lv.1→2는 100 EXP, Lv.10→11은 1000 EXP 필요

### 5.2 캐릭터 진화 단계

| 단계 | 이름 | 이모지 | 필요 레벨 |
|------|------|--------|----------|
| 1 | 별먼지 | ✨ | Lv.1 |
| 2 | 작은 별 | ⭐ | Lv.5 |
| 3 | 반짝 별 | 🌟 | Lv.10 |
| 4 | 큰 별 | 💫 | Lv.20 |
| 5 | 별자리 | 🌌 | Lv.35 |

### 5.3 보상 시스템

| 이벤트 | 트리거 조건 | 보상 |
|--------|-----------|------|
| `LevelUp` | 레벨 상승 시 | 축하 다이얼로그 + 랜덤 가구 박스 |
| `StreakMilestone` | 연속 3, 7, 14, 21, 30, 50, 100일 | 축하 다이얼로그 |
| `FurnitureUnlocked` | 레벨업 시 랜덤 | 잠긴 가구 중 1개 해제 |

---

## 6. UI 레이어

### 6.1 화면 구성

```
┌─────────────────────────────────┐
│        AppNavigation            │
│  ┌───────────┬───────────────┐  │
│  │  마이룸   │     습관      │  │  ← Bottom Navigation
│  └───────────┴───────────────┘  │
│                                 │
│  [RoomRoute]      캐릭터 방     │  ← 캐릭터, 레벨, EXP바, 가구
│  [HabitsRoute]    습관 목록     │  ← 오늘의 습관 리스트
│  [AddHabitRoute]  습관 추가     │  ← 이모지, 제목, 반복요일
│  [EditHabitRoute] 습관 수정     │  ← 기존 습관 편집
│  [FurniturePlacement] 가구 배치 │  ← 4슬롯 + 가구 그리드
└─────────────────────────────────┘
```

### 6.2 ViewModel

| ViewModel | UiState | 주요 액션 |
|-----------|---------|----------|
| `CharacterRoomViewModel` | level, exp, stage, placedFurniture, 오늘 습관 현황 | 초기화, Flow 결합 |
| `HabitListViewModel` | habits(HabitWithStatus[]), isLoading | `toggleHabitCompletion`, `addHabit`, `updateHabit`, `deleteHabit` |
| `FurniturePlacementViewModel` | unlockedFurniture, placedFurniture | `placeFurniture(id, slot)` |

### 6.3 보상 이벤트 흐름

```
습관 체크 → toggleHabitCompletion()
  ├→ HabitRepository.toggleCompletion()
  ├→ calculateStreak()
  ├→ UserRepository.addExp(streak) → ExpResult
  │   └→ 레벨업 감지 시 → RewardEvent.LevelUp 발행
  │       └→ tryRandomUnlock() → RewardEvent.FurnitureUnlocked 발행
  └→ 연속 마일스톤 감지 시 → RewardEvent.StreakMilestone 발행
       ↓
  HabitListScreen에서 SharedFlow 수집 → RewardDialog 표시
```

---

## 7. 디자인 시스템

### 7.1 컬러 팔레트

| 역할 | 컬러 | Hex |
|------|------|-----|
| **Primary** | Soft Pink | `#AD3362` (40) ~ `#FFFBFC` (99) |
| **Secondary** | Lavender | `#6733AD` (40) ~ `#FCFBFF` (99) |
| **Tertiary** | Creamy Mint | `#33AD84` (40) ~ `#FBFFFD` (99) |
| **Neutral** | Warm Gray | `#1C1B1E` (10) ~ `#FFFBFF` (99) |
| **Accent** | Peach / Baby Blue / Butter Yellow | 각각 40/80/90 톤 |

### 7.2 애니메이션

| 위치 | 종류 | 스펙 |
|------|------|------|
| 캐릭터 | 바운스 | `infiniteRepeatable`, tween 1000ms |
| 습관 카드 | 누름 스케일 | spring(stiffness=500), 0.96f |
| 보상 다이얼로그 | 팝업 스케일 | spring(damping=0.5, stiffness=300), 0.5f→1f |
| 습관 카드 | 컬러 전환 | `animateColorAsState` |

---

## 8. 플랫폼별 구현

### 8.1 expect/actual 패턴

| 기능 | common (expect) | Android (actual) | iOS (actual) |
|------|-----------------|-------------------|--------------|
| DB 생성 | `DatabaseFactory` | `context.getDatabasePath()` | `NSHomeDirectory()/Documents/` |
| 햅틱 | `HapticFeedback` | `Vibrator` / `VibrationEffect` | `UIImpactFeedbackGenerator` |
| 위젯 동기화 | `syncWidgets()` | placeholder (30분 자동 갱신) | placeholder (WidgetKit) |
| DI | `platformModule` | `AndroidDatabaseFactory(context)` | `IosDatabaseFactory()` |

### 8.2 Android 위젯 (Jetpack Glance)

- **크기**: 최소 250×180dp (3×3 셀)
- **갱신 주기**: 30분
- **내용**: 캐릭터 레벨/이모지 + 오늘 습관 목록(최대 5개) + 체크 토글
- **데이터**: Room DB 직접 접근 (`getAllHabitsOnce()`, `getUserStatusOnce()`)

### 8.3 iOS 위젯 (WidgetKit)

- SwiftUI 기반 (`MyLittleRoomWidget.swift`)
- Small / Medium / Lock Screen 대응
- 현재 플레이스홀더 데이터 사용 (Xcode 타겟 설정 필요)

---

## 9. DI 구성 (Koin)

```
startKoin {
    modules(platformModule, sharedModule, appModule)
}
```

| 모듈 | 제공 인스턴스 |
|------|-------------|
| `platformModule` | `DatabaseFactory` (플랫폼별) |
| `sharedModule` | `AppDatabase`, `HabitDao`, `UserStatusDao`, `FurnitureDao`, `HabitLogDao`, `HabitRepository`, `UserRepository`, `FurnitureRepository` |
| `appModule` | `CharacterRoomViewModel`, `HabitListViewModel`, `FurniturePlacementViewModel` |

**초기화 위치:**
- Android: `MyLittleRoomApp.onCreate()` (Application 클래스)
- iOS: `initKoin()` (MainViewController.kt에서 export)

---

## 10. 빌드 & 실행

### Android
```bash
./gradlew :composeApp:assembleDebug
```

### iOS
```bash
./gradlew :composeApp:compileKotlinIosSimulatorArm64
# 이후 Xcode에서 iosApp 프로젝트 열어서 실행
```

### 전체 빌드 확인
```bash
./gradlew :composeApp:compileDebugKotlinAndroid
./gradlew :composeApp:compileKotlinIosSimulatorArm64
```

---

## 11. 주요 파일 경로 맵

### shared 모듈
```
shared/src/commonMain/kotlin/com/mylittleroom/
├── data/
│   ├── entity/
│   │   ├── HabitEntity.kt
│   │   ├── HabitLogEntity.kt
│   │   ├── UserStatusEntity.kt
│   │   └── FurnitureEntity.kt
│   ├── dao/
│   │   ├── HabitDao.kt
│   │   ├── HabitLogDao.kt
│   │   ├── UserStatusDao.kt
│   │   └── FurnitureDao.kt
│   ├── repository/
│   │   ├── HabitRepository.kt
│   │   ├── UserRepository.kt
│   │   └── FurnitureRepository.kt
│   ├── AppDatabase.kt
│   └── DatabaseFactory.kt
├── domain/
│   ├── GamificationEngine.kt
│   ├── RewardEngine.kt
│   └── model/CharacterStage.kt
├── di/
│   ├── SharedModule.kt
│   └── PlatformModule.kt
└── platform/
    └── HapticFeedback.kt
```

### composeApp 모듈
```
composeApp/src/commonMain/kotlin/com/mylittleroom/
├── designsystem/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
├── ui/
│   ├── navigation/AppNavigation.kt
│   ├── screen/
│   │   ├── CharacterRoomScreen.kt
│   │   ├── HabitListScreen.kt
│   │   ├── AddHabitScreen.kt
│   │   ├── EditHabitScreen.kt
│   │   └── FurniturePlacementScreen.kt
│   ├── viewmodel/
│   │   ├── CharacterRoomViewModel.kt
│   │   ├── HabitListViewModel.kt
│   │   └── FurniturePlacementViewModel.kt
│   └── component/RewardDialog.kt
├── di/AppModule.kt
├── platform/WidgetSync.kt
└── App.kt
```

---

## 12. 미구현 항목

| 항목 | 상태 | 비고 |
|------|------|------|
| Lottie 캐릭터 애니메이션 | 미구현 | 디자이너 에셋(.json) 필요 |
| Pretendard 커스텀 폰트 | 미구현 | 폰트 파일 필요 |
| iOS Xcode 위젯 타겟 | 스캐폴딩만 | Xcode에서 Widget Extension 추가 필요 |
| iOS initKoin() 연결 | 미구현 | Swift AppDelegate에서 `MainViewControllerKt.doInitKoin()` 호출 필요 |
| 위젯 즉시 동기화 | 미구현 | 앱에서 습관 토글 시 위젯 즉시 갱신 |
| 다크 모드 테스트 | 미검증 | 테마 정의 완료, 실제 검증 필요 |
