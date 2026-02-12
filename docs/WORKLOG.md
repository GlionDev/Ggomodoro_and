# Work Log

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
