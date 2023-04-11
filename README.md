# DAEGU-safe-return-home-service
대구광역시 골목 위험도 분석을 통해 안심귀가를 도와주는 안드로이드 앱 서비스 입니다.
### [ 프로젝트에 대한 설명 ]
#### ✔️ 프로젝트 소개
  - 공공데이터를 활용해 범죄율에 영향을 미치는 요인을 분석해 위험도를 산출했습니다. 이를 가시적으로 보여주고, 안심 귀가를 도와주는 “안심 귀가 앱”을 개발하였습니다.
  - 이 레포는 "안심 귀가 앱" 개발 레포지토리입니다.

#### ✔️ 제안배경
  - 2020년 통계청 사회조사의 ‘사회 안전에 대한 인식도 조사 결과’에 따르면 우리나라 13세 이상의 인구 3명 중 1명은 야간 보행 시 불안감을 느낀다고 답변했습니다.
  - 불안감을 느끼는 지역에 보안등, 벽화그리기 등 CPTED 디자인만으로도 시민의 불안감을 크게 낮출 수 있고, 범죄 예방에도 효과가 있습니다.
 

#### ✔️ 목적
  - 공공데이터를 활용해 범죄 위험도가 높은 골목을 분석 및 선정하고, 이를 활용한 어플리케이션 개발을 통해 시민의 불안감 개선 및 입지 추천에 활용하는 것입니다.
  
### [ 데이터 분석 ]
<img width="700" src="https://user-images.githubusercontent.com/75965560/231138789-d8aa0627-fd95-4f21-b67a-1c1aaafa8f2f.png">

### [ 주요 기능 소개 ]

#### 1️⃣ 위험도 가시화<br>
  - 대구광역시 내 위험 골목 분석한 데이터를 활용해, 사용자의 목적지까지 가는 길에 위험골목이 있는지 표시합니다. <br> 
  - 위험 수치가 높을수록 **진한 색**으로 표시하여 사용자가 해당 골목의 위험도를 알 수 있도록 합니다. <br>
  
<img width="700" alt="스크린샷 2023-04-11 오후 7 48 18" src="https://user-images.githubusercontent.com/75965656/231138177-2641fd8f-ecfc-416c-9979-dcedc462525a.png">

  
#### 2️⃣ 대피시설 위치 가시화<br>
  - 사용자가 언제든지 위험한 상황에서 대피할 수 있도록 CCTV, 경찰서 및 치안센터, 편의점 위치 등 가시화합니다.<br>
  - 공공데이터 및 크롤링을 통하여 대피시설 위치를 수집하였습니다.<br>
  
  <img width="800" alt="스크린샷 2023-04-11 오후 7 50 20" src="https://user-images.githubusercontent.com/75965656/231138617-913f9cb9-a331-45cc-9358-a7ea20aa8535.png">
  
#### 3️⃣ 신고 서비스<br>
  - 사용자가 위험 상황에 처했을 때 대처할 수 있는 안전 서비스입니다.<br>
  - 사용자가 신고 버튼 클릭 시, 자동 녹음이 시작됨과 동시에 보호자 연락처로 신고자의 현재 위치를 전송합니다.<br>
  - 신고 취소하기 버튼 클릭 시, 자동 녹음이 종료됩니다.<br>
  - 녹음 목록 화면에서 녹음하였던 파일을 다시 확인할 수 있습니다.<br>
  
  <img width="500" alt="스크린샷 2023-04-11 오후 7 51 20" src="https://user-images.githubusercontent.com/75965656/231138837-6bd49c0a-3f3c-43d9-a65f-95b14838f610.png">

#### 4️⃣ 귀가 모니터링<br>
  - 출발지와 목적지 입력 후 길찾기 버튼 클릭 시, NaverMap 길찾기 API를 통해 보행자 경로가 지도에 표시됩니다.<br>
  - 또한, 귀가 모니터링동안 보호자에게 자동 문자 전송 및 자동 녹음이 시작됩니다. <br>
  - 길찾기가 종료시, 녹음은 종료됩니다. 또한 사용자가 종료하지 못했을 경우, 녹음은 최대 2시간이 지나면 자동종료됩니다.<br>
  - 귀가모니터링동안 사용자가 스마트폰을 세게 3번 흔든다면, 사용자가 위험상황에 처해있다고 인식하고 경찰서로 신고자의 현재위치와 함께 신고가 됩니다.<br>
  
  <img width="700" alt="스크린샷 2023-04-11 오후 7 50 57" src="https://user-images.githubusercontent.com/75965656/231138778-0e8e4ab0-360f-498c-a50a-72a34a6f99cd.png">

### [ 프로젝트 기대효과 및 활용방안 ]
- 골목길 범죄율이 높은 곳에 추가적인 조치(보안등이나 안전벨, 벽화그리기 등)가 가능해집니다. 
- 범최 취약 지역을 중심으로 경찰관 순찰 시 활용 지표로 사용될 수 있습니다.
- 범죄율에 영향을 미치는 요소들 바탕으로 범죄 예방 환경 설계에 활용될 수 있습니다.
- 안심 골목길 조성 사업을 진행할 위치결정에 활용될 수 있습니다.
- **빠른 대처를 통해 골목길 범죄로 인한 피해의 규모를 최소화할 수 있습니다.**

### [ 프로젝트에 활용된 기술 ]
<img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=white"/> - Firebase Cloud Firestore Database를 활용한 데이터베이스 개발

<img src="https://img.shields.io/badge/AndroidStudio-0c70f2?style=flat-square&logo=AndroidStudio&logoColor=92b8b1"/> <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=Kotlin&logoColor=white"/> - Kotlin을 사용한 안드로이드 어플리케이션 개발

<img src="https://img.shields.io/badge/NaverCloudPlatform-03C75A?style=flat-square&logo=Naver&logoColor=white"/> - 네이버 클라우드 플랫폼의 지도 API, 길찾기 API 사용하여 기능 구현

<img src="https://img.shields.io/badge/Qgis-E0E0E0?style=flat-square&logo=Qgis&logoColor=589632"/> - Qgis분선 프로그램을 이용한 후보군 시각화

### [ 시연영상 및 관련 링크 ]
- 시연영상 : https://www.youtube.com/watch?v=63wvwRpgnAw
- 피그마 링크 : https://www.figma.com/file/GvFI9zCTCnfTMEt1tsDHkj/application-for-safety?node-id=21%3A4702&t=wz6kfaRMkVjdx5mi-1
