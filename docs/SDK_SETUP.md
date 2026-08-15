# T-SDK Setup

TaurusControl은 NovaStar T-SDK의 Windows x64 네이티브 라이브러리를 JNA로 호출합니다. 실제 Taurus 장비에서 확인한 기준 버전은 `3.6.3.0101`이며, 최신 버전과의 호환성은 검증하지 않았습니다.

## 검증한 배포본

- 제품: NovaStar T-SDK / ViplexCore
- 버전: `3.6.3.0101`
- 플랫폼: Windows x64
- [NovaStar/VNNOX 공식 다운로드 페이지](https://www.vnnox.com/download)
- [검증한 배포본 직접 링크](https://download.vnnox.com/vnnox_software/ViplexCore3.6.3.0101_x64.zip)

직접 링크의 확장자는 `.zip`이지만 2026-08-15에 받은 파일은 RAR5 형식이었습니다. 압축을 풀 때 RAR5를 지원하는 도구를 사용하세요.

## 무결성 확인

2026-08-15에 공식 주소에서 받은 배포본과 장비 검증에 사용한 핵심 런타임 파일을 비교한 SHA-256 값입니다.

| 파일 | SHA-256 |
| --- | --- |
| 공식 배포본 `ViplexCore3.6.3.0101_x64.zip` | `29360eecee4aa1a783f4676e3f8f9e384a3909245653ee7a942fece531b36113` |
| `bin/viplexcore.dll` | `3fb5b6f544aa37ee18a2c3ac2e3929fbff7b5ca32e74fee7894e87b18cd314a6` |
| `bin/viplexcommon.dll` | `f392549b2496fec5f35ac909c1406f66d311658ea44e5cc189fb07e2df76d9e7` |
| `bin/nvcommon.dll` | `ecd03c799c70b8162c306e3bbabac189225231ee19e0ee77d4f9c4c8419da83e` |
| `bin/viplex.exe` | `29b0a2c15412a2d16f060883c95a621e70ed6476818343c6dceff8f4d9716327` |

PowerShell에서는 다음 명령으로 파일을 확인할 수 있습니다.

```powershell
Get-FileHash .\ViplexCore3.6.3.0101_x64.zip -Algorithm SHA256
```

공식 서버의 파일이 교체되거나 위 값과 다르면 그대로 실행하지 말고 버전과 배포 출처를 다시 확인하세요.

## 로컬 배치

1. 공식 배포본의 압축을 풉니다.
2. 저장소 루트에 `sdk/win32-x86-64/` 디렉터리를 만듭니다.
3. 압축을 푼 배포본의 `bin/` 내용 전체를 `sdk/win32-x86-64/`에 복사합니다.

기본 구조는 다음과 같습니다.

```text
TaurusControl/
└── sdk/
    └── win32-x86-64/
        ├── viplexcore.dll
        ├── viplexcommon.dll
        ├── nvcommon.dll
        └── ...
```

SDK를 다른 위치에 둘 때는 다음 중 하나로 경로를 지정합니다.

```powershell
$env:TAURUSCONTROL_SDK_PATH = "C:\path\to\ViplexCore\bin"
```

```powershell
.\gradlew run -Dtauruscontrol.sdk.path="C:\path\to\ViplexCore\bin"
```

JVM 시스템 속성이 환경 변수보다 우선하며, 둘 다 없으면 `sdk/win32-x86-64/`를 사용합니다.

## SDK 초기화 정보

1. 개발 실행(`.\gradlew run`)은 `config/credentials.example.json`을 `config/credentials.json`으로 복사합니다.
2. 설치본은 `config/credentials.example.json`을 `%USERPROFILE%\TaurusControl\config\credentials.json`으로 복사합니다.
3. 예제 값을 자신의 회사명 또는 식별 정보, 연락처와 이메일로 교체합니다.
4. 실제 `credentials.json`은 공개 저장소에 커밋하지 않습니다.

다른 경로를 사용할 때는 `TAURUSCONTROL_CREDENTIALS_PATH` 환경 변수 또는 `tauruscontrol.credentials.path` JVM 시스템 속성으로 지정할 수 있습니다.

## 실행

SDK와 설정 파일을 준비한 Windows x64 환경에서 실행합니다.

```powershell
.\gradlew run
```

Taurus 장비를 검색하고 제어하려면 PC와 장비가 동일 LAN에 있어야 합니다. 실제 장비 없이 실행한 결과를 장비 연동 검증으로 간주하지 마세요.

## 라이선스 경계

T-SDK와 배포본에 포함된 네이티브 라이브러리는 NovaStar 또는 각 권리자의 소유이며 TaurusControl의 MIT License 적용 대상이 아닙니다. 공식 다운로드가 가능하다는 사실만으로 공개 저장소나 설치 파일에 재배포할 권리가 확인되는 것은 아닙니다. SDK가 포함된 산출물을 외부에 배포하기 전에는 배포본의 약관을 확인하고, 불명확하면 NovaStar에 별도로 문의하세요.
