## 📱 TaskFlow 프로젝트 소개 📱

<br>

### 📋 TaskFlow란?

<img width="250" alt="Intro" src="https://raw.githubusercontent.com/feralshining/Taskflow/main/assets/appIcon.png">

> 일정 관리와 오늘의 할 일 관리를 위한 안드로이드 모바일 앱

<br>

### 🗓 개발 기간

- 1차: 2025.12 ~ 2025.12

<br>

### 💻 기술 스택

- `Android SDK`, `Java`, `Gradle`

<br>

### 프로젝트 기획 배경

> 현대인들의 바쁜 일상 속에서 효율적으로 일정을 관리하고 할 일을 체계적으로 정리할 수 있는 도구가 필요하다는 생각에서 출발함.

- ✅ 체계적인 관리 : 일정과 계획을 정리하며 효율적으로 시간을 관리하고 싶다.
- ✅ 편리한 사용성 : 바쁜 사용자도 쉽게 사용할 수 있는 직관적인 UX/UI가 필요하다.
- ✅ 일상의 균형 : 일과 생활의 균형을 맞출 수 있어야 한다.

<br>

### 💡 핵심 기능

- `할 일 관리 (To-Do List)`
- `일정 관리 및 캘린더 기능`
- `작업 우선순위 설정`
- `알림 기능`

<br>

### 🙋🏻‍♂️ 타겟 유저

- 효율적인 시간 관리가 필요한 학생 및 직장인

<br>

### 🌟 기대 효과

1. 체계적인 일정 관리로 생산성을 향상시킵니다.
2. 할 일을 체계적으로 정리해 시간 관리 부담을 줄입니다.
3. 직관적인 UI로 모든 연령대의 사용자가 쉽게 접근 가능하며, 장기적인 사용을 유도합니다.

<br>

### 📂 프로젝트 구조
```
com.taskflow/
├── activities/                   # 액티비티 (화면)
│   ├── main/
│   │   ├── MainActivity.java         # 캘린더 화면 (메인)
│   │   └── SplashActivity.java       # 스플래시 화면 (앱 시작)
│   ├── todo/
│   │   └── TodoListActivity.java     # 할 일 목록 화면 (홈)
│   └── settings/
│       └── SettingActivity.java      # 설정 화면 (마이페이지)
├── data/                         # 데이터 관련
│   ├── TodoItem.java                 # 투두(할 일) 항목 모델 클래스
│   ├── TodoDBHelper.java             # SQLite DB 헬퍼
│   └── TodoListAdapter.java          # ListView 어댑터
└── utils/                        # 유틸리티
    └── TaskFlowUI.java                # UI 공통 함수 (네비게이션, 토스트)
```

<br>

### 🎨 페이지 구현

(페이지 스크린샷 추가 예정)

<br>

### 🚧 추가 개발 계획

- **코드 리팩토링** : `모듈화와 주석 정비를 통해 가독성과 유지 보수성을 높이고, 확장 용이성 확보`
- **공유 기능 도입** : `할 일과 일정을 다른 사용자와 손쉽게 공유할 수 있는 옵션 개발`
- **클라우드 동기화** : `여러 기기 간 데이터 동기화 기능 추가`

<br>

### 🖥️ 실행 환경

- `Android 12 이상 (API 31+)`

<br>

### 🚀 배포링크

- `현재 개발 중. 추후 구글 Play Store에서 다운로드 가능 예정`
