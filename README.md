# UnsplashExplorer

Unsplash의 사진 목록을 둘러보고 상세 정보를 확인하는 Android 앱입니다.

좋아요한 사진은 앱 내부 저장소에 내려받고, 사진 정보와 로컬 파일 경로는 Room에 저장합니다.

## 주요 기능

- Unsplash 사진 목록 조회 및 무한 스크롤
- 고해상도 사진과 상세 정보(설명, 작가, 조회·다운로드 수, 촬영 정보, 위치, 태그) 조회
- 사진 목록과 상세 화면에서 좋아요 등록 및 해제
- 좋아요한 사진 다운로드 및 로컬 저장
- 저장한 사진 목록 조회
- 로딩, 빈 목록, 오류 상태와 재시도 처리

## 기술 스택

| 구분 | 기술 |
|---|---|
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Architecture | MVVM, Clean Architecture, UDF |
| Network | Retrofit, OkHttp, Kotlinx Serialization |
| Asynchronous | Coroutines, Flow, StateFlow |
| Data | Paging 3, Room, Internal File Storage |
| Dependency Injection | Hilt |
| Image | Coil 3 |
| Test | JUnit 4, MockK, Coroutines Test |

## 개발 환경

| 항목 | 버전 |
|---|---|
| Minimum SDK | API 30 (Android 11) |
| Target SDK | API 36 |
| Compile SDK | API 36 |
| JDK | 17 |
| Kotlin | 2.0.21 |
| Gradle | 8.13 |
| Android Gradle Plugin | 8.13.2 |

## 실행 방법

1. 저장소를 Clone한 뒤 Android Studio에서 프로젝트를 엽니다.
2. [Unsplash Developers](https://unsplash.com/developers)에서 Access Key를 발급받습니다.
3. 프로젝트 루트의 `local.properties`에 Access Key를 추가합니다.

```properties
UNSPLASH_ACCESS_KEY=YOUR_ACCESS_KEY
```

4. Gradle Sync 후 `app` 구성을 실행합니다.

## 기술 적용

### Paging 3를 이용한 사진 목록 구성

Unsplash 사진을 페이지 단위로 요청하고 스크롤 위치에 따라 다음 데이터를 불러오도록 Paging 3를 적용했습니다. `PagingData`는 `cachedIn(viewModelScope)`으로 유지하여 화면이 다시 구성될 때 불필요한 요청이 발생하지 않도록 했습니다.

최초 로딩과 추가 페이지 로딩의 `LoadState`를 구분하여, 최초 로딩에 실패하면 전체 오류 화면을 표시하고 다음 페이지 요청에 실패하면 기존 목록을 유지한 채 하단에 재시도 UI를 표시합니다.

### Room과 Flow를 이용한 좋아요 상태 동기화

좋아요한 사진은 Room에 저장하고 `Flow`로 변경 사항을 관찰합니다. 사진 목록의 `PagingData`와 Room의 좋아요 목록을 결합하여 좋아요 상태가 변경되면 목록 UI에도 바로 반영되도록 구성했습니다.

상세 화면에서는 선택한 사진의 저장 상태를 별도로 관찰합니다. 상세 화면에서 좋아요를 변경한 뒤 이전 화면으로 돌아가면 갱신된 목록과 기존 스크롤 위치를 확인할 수 있습니다.

### 내부 저장소를 이용한 이미지 보관

좋아요 등록 시 Unsplash 다운로드 API에서 이미지 URL을 받은 뒤 앱 내부 저장소에 파일을 저장합니다. Room에는 목록 표시에 필요한 사진 정보, 로컬 파일 경로와 저장 시각을 기록합니다.

```text
좋아요 등록
다운로드 URL 조회 → 이미지 파일 저장 → Room 저장

좋아요 해제
이미지 파일 삭제 → Room 데이터 삭제
```

Room 저장에 실패하면 앞서 내려받은 이미지 파일을 삭제하여 사용되지 않는 파일이 남지 않도록 했습니다. 좋아요 해제 시에는 이미지 파일을 먼저 삭제한 뒤 Room 데이터를 제거하며, 좋아요 화면에서는 저장된 로컬 파일을 우선 표시합니다.

### 상태 관리와 오류 처리

화면별 ViewModel에서 `StateFlow`로 UI 상태를 관리하고 Compose에서는 `collectAsStateWithLifecycle()`로 수집합니다. 상세 화면과 좋아요 화면은 `UiState`로 Loading, Success, Error 상태를 구분했습니다.

API, Room, 파일 처리 중 발생한 오류는 `AppError`로 변환합니다. 목록이나 상세 조회에 실패하면 재시도 화면을 보여주고, 좋아요 처리 실패는 Snackbar로 알립니다.

### 계층 간 의존성 분리

ViewModel은 Repository를 직접 호출하지 않고 UseCase를 통해 기능을 실행합니다. Repository 인터페이스는 Domain 계층에 두고 구현체는 Data 계층에서 제공하며, Hilt를 통해 의존성을 연결했습니다.

## 프로젝트 구조

```text
com.bccard.photoexplorer
├── data
│   ├── error          # 네트워크, DB, 파일 예외 변환
│   ├── local          # Room Database, DAO, Entity
│   ├── remote         # Retrofit API, DTO, PagingSource
│   └── repository     # Repository 구현체
├── di                 # Hilt Module
├── domain
│   ├── error          # 공통 AppError
│   ├── model          # Domain Model
│   ├── repository     # Repository Interface
│   └── usecase        # 기능 단위 비즈니스 로직
└── presentation
    ├── common         # 공통 상태 및 오류 UI
    ├── main           # 사진 목록
    ├── detail         # 사진 상세
    ├── favorite       # 좋아요 목록
    └── navigation     # 화면 이동
```

## 테스트

MockK로 API, Repository, 파일 저장소를 대체하고 아래 네 가지 흐름을 테스트했습니다.

| 테스트 대상 | 검증 내용 |
|---|---|
| `MainPhotoPagingSource` | 첫 페이지 API 호출, 사진 모델 변환, 페이지 키 생성 |
| `PhotoRepositoryImpl` | 상세 API 응답을 Domain 모델로 변환 |
| `AddFavoritePhotoUseCase` | 다운로드 URL 조회, 파일 저장, Room 등록 순서 |
| `RemoveFavoritePhotoUseCase` | 이미지 파일과 Room 데이터 삭제 순서 |
