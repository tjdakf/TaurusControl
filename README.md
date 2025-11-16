# Taurus Control

Novastar에서 제공하는 Viplex Express를 더욱 쉽게 사용할 수 있도록 개선한 Taurus Series 제어 프로그램입니다.

## Features
- 동일한 LAN에 접속된 Taurus 터미널 탐색
- 터미널 로그인/로그아웃
- 미디어 재생 관리
- 원클릭 터미널 초기 설정
- LED화면 밝기 설정
- ON/OFF 타이머 설정

## 실행 요구사항
- JDK 21 버전 이상
- Windows 64bit 운영체제
- Taurus Series 제품이 동일한 LAN에 연결되어 있어야 모든 기능을 사용할 수 있음

## 기능 요구사항
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
- [ ] 로컬에 저장된 프로그램을 터미널로 전송

### 원클릭 터미널 초기 설정
- [ ] 터미널 객체를 입력받아 해당 터미널의 시간 설정을 현재 PC의 로컬 시간으로 조정
- [ ] 현재 터미널의 재부팅 설정 시간을 반환
- [ ] 객체, 요일, 시간을 입력받아 재부팅 설정

### LED화면 밝기 설정
- [ ] 현재 터미널의 밝기를 조회
- [ ] 현재 터미널의 밝기를 변경

### ON/OFF 타이머 설정
- [ ] 현재 터미널의 타이머 설정을 조회(MANUAL or SCHEDULE)
- [ ] SCHEDULE이면 설정된 시간을 조회
- [ ] 타이머 설정 타입 변경
- [ ] SCHEDULE이면 지정된 시간으로 변경

### CLI View
- [ ] 프로그램 초기 화면 구성
- [ ] 터미널 관리 화면 구성
- [ ] 미디어 재생 관리 화면 구성
- [ ] 원클릭 터미널 초기 설정 화면 구성
- [ ] LED화면 밝기 설정 화면 구성
- [ ] 타이머 설정 화면 구성

### 보류 기능
- [ ] 터미널 탐색 후 비밀번호를 가지고 있으면 자동 로그인