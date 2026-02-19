# Work Log

## 2026-02-19
- **[Feature] 타이머 진동 효과 구현 및 디자인 변경, 코드 정리**
    - 타이머 다이얼 조정 시 분 단위 변경마다 진동 피드백(`VibrationEffect.EFFECT_TICK`) 추가.
    - AndroidManifest.xml에 `VIBRATE` 권한 추가.
    - **[Design] Toy-like Aesthetic Redesign**
        - 꼬꼬(닭) 의 색상과 맞춘 color theme 적용
        - **Timer UI**: 두꺼운(50dp) 트랙, 둥근 캡(Round Cap), 큰 원형 손잡이(Knob) 추가.
        - **Controls**: 텍스트 버튼을 둥근(RoundedCornerShape 50) '청키'한 스타일로 변경.
    - **[Refactor] Dependency Management**:
        - `:core:designsystem` 모듈의 Compose UI 라이브러리(`ui`, `material3` 등) 의존성을 `api`로 변경하여 피쳐 모듈에 일관되게 노출.
        - `:feature:timer` 모듈의 불필요한 명시적 `foundation` 의존성 제거.
    - **[Fix] Build Stability**:
        - Gradle Heap Size를 4GB로 증설(`org.gradle.jvmargs=-Xmx4g`)하여 Dex Merging 단계의 `OutOfMemoryError` 해결.
    - **[Fix]** Galaxy A31 (Android 12) 등 일부 기기 호환성 문제 해결: `EFFECT_TICK` 대신 `createOneShot(30ms)` 및 `USAGE_TOUCH` 속성 사용.
    - **[Fix]** `NoSuchMethodError` 해결: `vibrate(VibrationEffect, VibrationAttributes)`는 API 33 이상에서만 호출하도록 조건 분기 수정.
    - **[Refactor]** `VIBRATE` 권한을 `:app` 모듈에서 `:feature:timer` 모듈로 이동.
    - Android 12 이상(VibratorManager) 및 레거시 디바이스(Vibrator) 호환성 처리.
    - **[Refactor] Import 정리**
    - `android.os`, `androidx.compose` 등 Fully Qualified Name을 `import` 문으로 교체.
    - 대상: `TimerScreen.kt`, `MainActivity.kt`, `NotificationHelper.kt`, `Theme.kt`, `TimerService.kt`.
    - 빌드 및 기능 검증 완료.

## 2026-02-12
- **[Refactor] Global Rule 적용 및 프로젝트 구조 개선**
    - `build-logic`의 `KotlinAndroid.kt`에서 `compileSdk`를 36으로 다시 변경 (중복된 Hilt App 클래스 제거 후 정상 빌드 확인).
    - `:app` 모듈에서 중복된 `GgomodoroApp.kt` 삭제 (`GgomodoroApplication.kt` 유지).
    - KDoc(한국어) 적용 시작.
    - `docs/` 디렉토리에 필수 문서(`README.md`, `WORKLOG.md`, `DECISIONS.md`) 생성.
    - **[Feature] History 기능 개선**
        - 기록 아이템 스와이프 삭제 기능 추가 (좌->우 스와이프, 삭제 확인 다이얼로그).
        - 북마크 기능(별 아이콘) 제거.
        - 메모 글자 수 제한을 100자에서 30자로 축소 및 유효성 검사 UI 버그 수정.
    - **[Feature] Timer 기능 및 디자인 개선**
        - 타이머 디자인 변경: 굵기 유지(40dp), 크기 확대(여백 16dp).
        - 인터렉션 개선: 12시 방향에서만 드래그 시작 가능하도록 제한.
        - 시작 가이드 추가: 상단 "Swipe Start!"(Bold) 텍스트 및 화살표 표시 (원 상단 8dp 간격)
        - 진행 시각화 변경: 남은 시간에 비례하여 원호가 줄어드는(Shrinking Arc) 방식으로 변경.
    - **[Feature] 기타 편의 기능**
        - 화면 유지(Keep Screen On): 타이머 작동 중 화면 꺼짐 방지 기능 추가.
        - 알림 클릭 이동: 진행 중인 타이머 알림 클릭 시 앱으로 이동하도록 `PendingIntent` 추가.
        - 타이머 UI 개선: 타이머 작동 중 시간 텍스트 숨김, Stop 버튼 중앙 배치 및 확인 다이얼로그 추가.
        - 알림 UI 개선: 시간 텍스트 제거 및 프로그레스 바(Progress Bar) 형태로 변경.
        - 인터렉션 개선: 12시 방향(Top-Center) 시작 시 반시계 방향(역방향) 스와이프를 명시적 각도 범위(0-90도 vs 270-360도) 체크로 차단.
        - 권한 요청: Android 13(API 33) 이상 대응을 위한 알림 권한(`POST_NOTIFICATIONS`) 요청 및 거부 시 설정 이동 다이얼로그 추가.

## 2026-02-11
- **[Configuration] Gitignore 설정**
    - Android 프로젝트용 `.gitignore` 파일 생성.

## 2026-02-06
- **[Migration] AndroidX 마이그레이션 계획 및 프로젝트 구조 탐색**
    - 프로젝트 구조 및 의존성 분석.
    - AndroidX 마이그레이션 전략 수립.

## 2026-02-04
- **[Init] Ggomodoro 프로젝트 초기 개발**
    - Clean Architecture 및 Multi-module 구조셋업.
    - `:feature:timer`: 원형 타이머 UI 및 서비스 구현.
    - `:feature:history`: 기록 및 메모 기능 구현.
    - `:core:database`: Room Database 연동.
    - `:core:designsystem`: Pastel Material 3 테마 적용.
