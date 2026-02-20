# Architecture Decisions

## 1. Clean Architecture
- **Decision**: Domain, Data, Presentation(Feature) 레이어로 분리.
- **Reason**: 비즈니스 로직의 독립성을 보장하고 테스트 용이성을 높이기 위함.

## 2. Dependency Injection (Hilt)
- **Decision**: Hilt 사용.
- **Reason**: 안드로이드 표준 DI 라이브러리로, 설정이 간편하고 컴포넌트 수명주기 관리가 용이함.

## 3. Build Logic (Convention Plugins)
- **Decision**: `build-logic` 모듈을 통한 공통 Gradle 설정 관리.
- **Reason**: 멀티 모듈 프로젝트에서 빌드 설정의 중복을 제거하고 일관성을 유지하기 위함.

## 4. Java 1.8 Compatibility
- **Decision**: Java 1.8 (`JavaVersion.VERSION_1_8`) 타겟팅.
- **Reason**: 레거시 디바이스 지원 및 안정성 확보를 위한 프로젝트 규칙 준수.

## 5. DataSource Layer 추가
- **Decision**: Repository와 Dao 사이에 LocalDataSource 계층 추가.
- **Reason**: Repository가 로컬 데이터베이스 기술(Room, Dao 등)에 직접 의존하지 않고 인터페이스를 통해 통신하도록 하여, 추후 데이터 원천이 변경되더라도 Repository의 변경을 최소화하기 위함.
