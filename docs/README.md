# Ggomodoro

Ggomodoro는 뽀모도로 타이머 안드로이드 애플리케이션입니다.
Clean Architecture와 Multi-module 구조를 따르며, Hilt를 사용하여 의존성을 주입합니다.

## 빌드 및 실행 방법

### 요구 사항
- JDK 1.8 이상
- Android Studio Koala 이상 (권장)
- Android SDK 36

### 빌드 명령어
```bash
# Debug 빌드
./gradlew assembleDebug

# Release 빌드
./gradlew assembleRelease

# Unit Test 실행
./gradlew test
```

## 프로젝트 구조
- `:app`: 메인 애플리케이션 모듈
- `:domain`: 핵심 비즈니스 로직 (순수 Kotlin)
- `:data`: 데이터 처리 및 저장 (Room, DataStore 등)
- `:feature`: 사용자 기능 모듈 (타이머, 기록 등)
- `:core`: 공통 유틸리티 및 디자인 시스템
- `:build-logic`: Gradle Convention Plugins
