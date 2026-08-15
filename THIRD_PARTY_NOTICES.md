# Third-Party Notices

이 문서는 TaurusControl이 사용하는 외부 소프트웨어와 프로젝트 라이선스의 적용 범위를 구분하기 위한 안내입니다.

## TaurusControl source code

이 저장소에서 직접 작성한 TaurusControl 소스 코드는 저장소의 [MIT License](LICENSE)를 따릅니다.

## NovaStar T-SDK

TaurusControl은 실제 장비 연동 시 NovaStar T-SDK의 네이티브 런타임을 사용합니다.

- 검증한 버전: T-SDK `3.6.3.0101` / Windows x64
- 배포자: NovaStar/VNNOX
- [공식 다운로드 페이지](https://www.vnnox.com/download)
- 설정 방법: [docs/SDK_SETUP.md](docs/SDK_SETUP.md)

T-SDK, ViplexCore, SDK와 함께 제공되는 실행 파일·라이브러리·인증서 및 기타 자료에는 TaurusControl의 MIT License가 적용되지 않습니다. 이 프로젝트는 해당 파일에 대한 재배포 권리를 주장하지 않습니다. 사용 및 재배포 가능 여부는 NovaStar가 제공하는 약관과 각 구성 요소의 라이선스를 사용자가 직접 확인해야 합니다.

## Build dependencies

JavaFX, JNA, org.json과 테스트 라이브러리 등 Gradle을 통해 내려받는 의존성은 각 프로젝트의 라이선스를 따릅니다. 정확한 버전은 [build.gradle](build.gradle)을 기준으로 확인하세요.

이 문서는 법률 자문이나 제3자 소프트웨어의 전체 고지문을 대신하지 않습니다.
