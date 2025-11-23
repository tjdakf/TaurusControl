# Changelog

All notable changes to TaurusControl will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.0.0] - 2025-11-23

### Added

- **터미널 검색 기능**
  - 동일 LAN 내 Taurus Series 터미널 자동 탐색
  - 기존에 로그인했던 터미널은 자동 로그인 수행
  - 로그인 상태별 색상 표시 (녹색: 로그인됨, 노란색: 다른 기기, 회색: 미로그인)

- **재생 관리 기능**
  - 미디어 파일 추가/삭제/순서 변경
  - 지원 포맷: mp4, avi, jpg, png, gif
  - 한글 파일명 완벽 지원 (MD5 해시 기반 처리)
  - 키보드 단축키 지원 (DELETE: 삭제, UP/DOWN: 선택 변경)
  - 진행률 표시와 함께 프로그램 전송

- **스케줄 관리 기능**
  - ON/OFF 스케줄 설정
  - 요일별 여러 시간대 설정 가능
  - 수동 모드 / 자동 모드 전환
  - 키보드 단축키 지원 (DELETE: 삭제, ENTER: 편집, UP/DOWN: 탐색)
  - 한글 스케줄 표시 (예: "월화수 09시 00분")

- **터미널 설정 기능**
  - 시간 동기화 (PC 시간과 동기화 설정)
  - 재부팅 스케줄 설정 (주 1회 재부팅 설정)
  - LED 밝기 조절 (0-100%)

- **Windows 네이티브 인스톨러**
  - .msi 패키지로 배포
  - JRE 21 번들 포함 (Java 설치 불필요)
  - 시작 메뉴 및 바탕화면 바로가기 자동 생성
  - 중복 실행 방지 기능

### Technical Details

- **기술 스택**
  - Java 21 + JavaFX 21
  - T-SDK (JNA 기반 네이티브 연동)
  - Gradle 빌드 시스템
  - jpackage (Windows 인스톨러 생성)

- **아키텍처**
  - MVC 패턴 기반 설계
  - JavaFX Task를 활용한 비동기 처리
  - 재사용 가능한 다이얼로그 컴포넌트 체계
  - CSS 기반 다크 테마 UI

- **주요 특징**
  - UTF-8 인코딩 완벽 지원
  - CRON 기반 스케줄링
  - 스마트 스크롤 및 키보드 단축키
  - Thread-safe 비동기 처리
