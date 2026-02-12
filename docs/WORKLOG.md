# Work Log

## 2026-02-12
- **[Refactor] Global Rule 적용 및 프로젝트 구조 개선**
    - `build-logic`의 `KotlinAndroid.kt`에서 `compileSdk`를 36으로 다시 변경 (중복된 Hilt App 클래스 제거 후 정상 빌드 확인).
    - `:app` 모듈에서 중복된 `GgomodoroApp.kt` 삭제 (`GgomodoroApplication.kt` 유지).
    - KDoc(한국어) 적용 시작.
    - `docs/` 디렉토리에 필수 문서(`README.md`, `WORKLOG.md`, `DECISIONS.md`) 생성.

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
