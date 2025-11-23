# Taurus Control - Development Guide

이 문서는 TaurusControl 프로젝트를 개발하기 위한 환경 설정 및 빌드 방법을 안내합니다.

---

## Development Setup

### Prerequisites

- **JDK**: 21 이상
- **Gradle**: 포함됨 (gradlew 사용)
- **운영체제**: Windows 64bit (ViplexCore SDK 제약)
- **네트워크**: Taurus 터미널과 동일 LAN 연결 (실제 테스트용 터미널이 필요합니다.)
- **IDE**: IntelliJ IDEA 권장

### Clone Repository

```bash
git clone https://github.com/tjdakf/TaurusControl.git
cd TaurusControl
```

### SDK Setup

ViplexCore SDK 파일들이 다음 디렉토리에 위치해야 합니다:

**경로**: `src/main/resources/win32-x86-64/`

**필수 파일**:
- `viplexcore.dll` (핵심 네이티브 라이브러리)
- 기타 SDK 종속 DLL 파일들

**주의**: SDK 파일들은 Windows 전용이며, Git에 포함되어 있습니다.

### Credentials Configuration

SDK 인증을 위해 `src/main/resources/templates/credentials.json` 파일이 필요합니다.

**설정 방법**:
1. 템플릿 파일 확인
2. 인증 정보 입력
3. UTF-8 인코딩 유지

---

## Build & Run

### Build

```bash
./gradlew build
```

### Run (Development Mode)

```bash
./gradlew run
```

**주의**: SDK는 Windows 전용이므로 실제 실행 및 테스트는 Windows에서 수행하세요.

### Test

```bash
./gradlew test
```

### Package (Windows Installer)

Windows 환경에서 WiX Toolset 설치 후:

```bash
./gradlew jpackage
```

생성된 `.msi` 파일은 `build/jpackage/` 디렉토리에 위치합니다.

**요구사항**:
- Windows 64bit
- WiX Toolset 3.11 이상

---

## Project Architecture

### Tech Stack

- **Language**: Java 21
- **UI Framework**: JavaFX 21
- **Build Tool**: Gradle
- **Native Integration**: JNA (Java Native Access)
- **SDK**: T-SDK (Novastar)

### Package Structure

```
tauruscontrol/
├── view/              # JavaFX UI components
│   └── components/    # Reusable dialogs
├── controller/        # JavaFX Task-based async controllers
├── service/           # Business logic layer
├── domain/            # Domain models and managers
├── sdk/               # ViplexCore JNA interface
└── util/              # Utility classes
```

### Key Design Patterns

**MVC Pattern**:
- View: JavaFX UI
- Controller: Async adapters (JavaFX Task)
- Service/Domain: Business logic + SDK calls

**Singleton**: SDKManager (ViplexCore 초기화)

**Template Pattern**: JSON 템플릿 기반 SDK 요청

---

## Development Notes

### SDK Async Callbacks

모든 SDK 메서드는 비동기 콜백 방식입니다:

```java
sdk.someMethodAsync(request, (code, data) -> {
    if (code == 0) {
        // Success
    } else {
        // Error
    }
    AsyncHelper.setApiReturn(true);
});
AsyncHelper.waitAPIReturn();  // Block until callback
```

### JavaFX Thread Safety

UI 업데이트는 반드시 JavaFX Application Thread에서:

```java
Platform.runLater(() -> {
    // Update UI here
});
```

### UTF-8 Encoding

모든 파일, 템플릿, SDK 요청은 UTF-8 인코딩을 사용합니다.

### Windows-Only Limitation

ViplexCore SDK는 Windows 64bit 전용입니다.

### Application Data Location

프로그램 실행 시 다음 위치에 데이터가 생성됩니다:

```
C:\Users\(사용자명)\TaurusControl\
```

**저장되는 파일**:
- 애플리케이션 실행 로그
- 데이터베이스 (터미널 정보, 로그인 비밀번호 등)
- 프로그램 생성 정보

**디버깅 팁**:
- 로그 파일 확인: `C:\Users\tjdak\TaurusControl\temp\log\*.log`
- 데이터 초기화: 위 폴더 전체 삭제 후 재실행

---

## Initial Requirements (Historical)

<details>
<summary>초기 개발 요구사항 명세 (클릭하여 펼치기)</summary>

### 프로그램 초기화
- ViplexCore 객체 생성(싱글턴 패턴)
- 객체 생성 시 `int nvInit(const char *sdkRootDir, const char *credentials)`으로 프로그램 초기화 진행

### 터미널 관리
- `void nvSearchTerminalAsync(ExportViplexCallback callback)`를 이용하여 동일한 LAN에 접속된 터미널을 탐색
- 터미널 이름과 해상도, 로그인 여부를 표시
- 비밀번호를 알고 있는 경우 자동 로그인

### 로그인 기능
- 검색된 터미널 중 로그인되지 않은 터미널을 선택하여 로그인
- 사용자에게 비밀번호를 입력받아 로그인
- `void nvLoginAsync(const char *data,ExportViplexCallback callback)` 사용

### 미디어 재생 관리
- 로그인 된 터미널은 미디어 재생 관리를 할 수 있음
- 로그인 된 터미널은 터미널 SN번호와 같은 이름의 재생 목록을 한 개 가짐
- `void nvGetProgramAsync(const char *data, ExportViplexCallback callback)`로 프로그램 목록을 조회하여
미디어 재생 관리를 하려는 프로그램을 선택
- 미디어 파일(VIDEO, IMAGE, GIF)의 경로를 입력받아 재생 목록에 추가
- 미디어 파일 경로 입력은 엔터로 구분
- 아무것도 입력하지 않고 엔터 입력 시 입력 종료
- 미디어 파일 추가가 종료되면 
  - `void nvSetPageProgramAsync(const char *data, ExportViplexCallback callBack)`를 이용하여 프로그램 수정
  - `void nvMakeProgramAsync(const char *data, ExportViplexCallback callBack)` 를 이용하여 로컬에 프로그램 생성
- 사용자에게 전송 승인 입력 받기
- 승인 후 터미널에 프로그램 전송

### 원클릭 터미널 초기 설정
- 터미널의 시간 동기화 설정과 재부팅 타이머를 설정함
- 로그인 된 터미널 목록을 불러오며, 사용자는 설정할 터미널을 선택
- 재부팅 요일과 시간을 입력받음(1주일에 1회 설정)
- `void void nvSetReBootTaskAsync(const char *data, ExportViplexCallback callback)`를 이용하여 재부팅 설정
- 터미널 시간 설정을 현재 PC의 로컬 시간과 동기화 시킴
- `void nvCalibrateTimeAsync(const char *data, ExportViplexCallback callback)` 사용

### LED화면 밝기 설정
- 로그인 된 터미널 목록을 불러오며, 사용자는 설정할 터미널을 선택
- `void nvGetScreenBrightnessAsync(const char *data, ExportViplexCallback callback)`를 이용하여 현재 밝기 값을 표시하며,
변경할 밝기 값을 입력받음
- `void nvSetScreenBrightnessAsync(const char *data, ExportViplexCallback callback)`를 이용하여 밝기값 변경

### ON/OFF 타이머 설정
- 로그인 된 터미널 목록을 불러오며, 사용자는 설정할 터미널을 선택
- 현재 설정 값을 표시
  - MANUAL: 항상 ON 상태
  - SCHEDULE: 지정된 시간에 따라 ON/OFF, 설정된 시간도 함께 표시
- 사용자는 설정 값을 변경할 수 있음
  - MANUAL로 변경: 항상 ON 상태로 변경
  - SCHEDULE로 변경 :
    - ON/OFF 시간을 입력받음
    - `void nvSetScreenPowerPolicyAsync(const char *data, ExportViplexCallback callback)`를 사용하여 설정

## 구현할 기능 목록
### 프로그램 초기화
- [x] ViplexCore 객체를 생성하고 SDK초기화 진행
- [x] 싱글턴 패턴을 적용하여 전역적으로 한 번의 초기화와 한 가지의 객체만 사용되도록 구현

### 터미널 관리
- [x] 동일한 LAN에 접속된 터미널을 탐색
- [x] 터미널 이름, SN번호, 해상도, 로그인 여부, 비밀번호 여부 저장
- [x] 검색된 터미널 개수 확인 기능
- [x] 로그인 여부, 터미널 이름순으로 정렬

### 로그인 기능
- [x] 터미널 로그인 기능 구현
- [x] 비밀번호가 다르면 예외 처리
- [x] 터미널 탐색 후 비밀번호를 가지고 있으면 자동 로그인

### 미디어 재생 관리
- [x] 터미널 객체를 입력받아 같은 이름의 프로그램이 로컬에 존재하는지 확인
  - 프로그램이 존재하면 프로그램 ID 반환
  - 프로그램이 존재하지 않으면 SN번호를 이름으로, 해상도에 맞게 프로그램을 생성하여 ID 반환
- [x] 미디어 파일 경로를 입력받아 존재하는 파일인지 확인
- [x] 미디어 파일의 MD5 해시값을 구함
- [x] 미디어 데이터 저장 기능
- [x] 미디어 파일 전송 데이터를 가진 JSON 객체 생성
- [x] 전송할 미디어 데이터 JSON 객체를 프로그램 수정 JSON템플릿으로 생성한 JSON 객체에 주입
- [x] 최종 JSON 객체로 프로그램 수정
- [x] 수정된 프로그램 정보 로컬에 저장
- [x] 로컬에 저장된 프로그램을 터미널로 전송

### 원클릭 터미널 초기 설정
- [x] 터미널 객체를 입력받아 해당 터미널의 시간 설정을 현재 PC의 로컬 시간으로 조정
- [x] 현재 터미널의 재부팅 설정 시간을 반환
- [x] 객체, 요일, 시간을 입력받아 재부팅 설정

### LED화면 밝기 설정
- [x] 현재 터미널의 밝기를 조회
- [x] 현재 터미널의 밝기를 변경

### ON/OFF 타이머 설정
- [x] 현재 터미널의 타이머 설정을 조회(MANUAL or AUTO)
- [x] 현재 터미널의 ON/OFF 상태를 조회
- [x] AUTO면 설정된 시간을 조회
- [x] 타이머 설정 타입 변경(MANUAL or SCHEDULE)
- [x] MANUAL ON/OFF 기능
- [x] SCHEDULE 타임 설정

### GUI View
- JavaFX로 간단한 프로그램 화면 구성

</details>
