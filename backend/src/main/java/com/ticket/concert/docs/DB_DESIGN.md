# DB 설계

## 1. User

회원 정보를 저장한다.

| 컬럼 | 타입 | 키 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 회원 식별자 |
| email | VARCHAR(255) | UNIQUE | 로그인 이메일 |
| password | VARCHAR(255) | - | 암호화된 비밀번호 |
| name | VARCHAR(50) | - | 회원 이름 |
| role | VARCHAR(20) | - | USER / ADMIN |
| created_at | DATETIME | - | 가입일 |
| updated_at | DATETIME | - | 수정일 |

**관계**
- User 1 : N Reservation
    - Reservation의 `user_id`가 User의 `id`를 참조한다.


## 2. Concert

공연 자체의 기본 정보를 저장한다.
공연 시간은 `ConcertSchedule`, 좌석은 `Seat`에서 관리하므로 공연에 대한 기본 정보만 저장한다.

| 컬럼 | 타입 | 키 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 공연 식별자 |
| title | VARCHAR(200) | - | 공연 제목 |
| description | TEXT | - | 공연 설명 |
| created_at | DATETIME | - | 공연 등록일 |
| updated_at | DATETIME | - | 공연 수정일 |

**관계**
- Concert 1 : N ConcertSchedule
    - ConcertSchedule의 `concert_id`가 Concert의 `id`를 참조한다.


## 3. Venue

공연이 열리는 공연장 정보를 저장한다.
하나의 공연장은 여러 공연에서 사용될 수 있으므로 공연 정보와 분리해서 관리한다.

| 컬럼 | 타입 | 키 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 공연장 식별자 |
| name | VARCHAR(100) | - | 공연장 이름 |
| address | VARCHAR(255) | - | 공연장 주소 |
| created_at | DATETIME | - | 공연장 등록일 |
| updated_at | DATETIME | - | 공연장 수정일 |

**관계**
- Venue 1 : N Seat
    - Seat의 `venue_id`가 Venue의 `id`를 참조한다.
- Venue 1 : N ConcertSchedule
    - ConcertSchedule의 `venue_id`가 Venue의 `id`를 참조한다.


## 4. Seat

공연장에서 실제로 예매할 수 있는 좌석 정보를 저장한다.
좌석의 위치와 등급을 관리하며, 좌석별 가격을 어떻게 관리할지는 이후 예매 구조와 함께 결정한다.

| 컬럼 | 타입 | 키 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 좌석 식별자 |
| venue_id | BIGINT | FK | 공연장 식별자 |
| seat_number | VARCHAR(20) | - | 좌석 번호 |
| seat_grade | VARCHAR(20) | - | 좌석 등급 (VIP / R / S 등) |
| created_at | DATETIME | - | 좌석 등록일 |
| updated_at | DATETIME | - | 좌석 수정일 |

**관계**
- Venue 1 : N Seat
    - Seat의 `venue_id`가 Venue의 `id`를 참조한다.


## 5. ConcertSchedule

공연이 언제, 어떤 공연장에서 진행되는지 관리한다.
하나의 공연은 여러 날짜와 시간에 진행될 수 있으므로 `Concert`와 분리한다.

| 컬럼 | 타입 | 키 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 공연 일정 식별자 |
| concert_id | BIGINT | FK | 공연 식별자 |
| venue_id | BIGINT | FK | 공연장 식별자 |
| start_at | DATETIME | - | 공연 시작 일시 |
| end_at | DATETIME | - | 공연 종료 일시 |
| created_at | DATETIME | - | 일정 등록일 |
| updated_at | DATETIME | - | 일정 수정일 |

**관계**
- Concert 1 : N ConcertSchedule
    - ConcertSchedule의 `concert_id`가 Concert의 `id`를 참조한다.
- Venue 1 : N ConcertSchedule
    - ConcertSchedule의 `venue_id`가 Venue의 `id`를 참조한다.

### 6. Reservation

회원이 특정 공연 일정을 예매한 정보를 저장한다.
한 명의 회원은 여러 예약을 만들 수 있고, 하나의 공연 일정에도 여러 예약이 발생할 수 있다.

| 컬럼 | 타입 | 키 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 예약 식별자 |
| user_id | BIGINT | FK | 예약한 회원 식별자 |
| concert_schedule_id | BIGINT | FK | 예매한 공연 일정 식별자 |
| status | VARCHAR(20) | - | 예약 상태 (PENDING / CONFIRMED / CANCELLED) |
| total_price | DECIMAL(10,2) | - | 총 결제 금액 |
| created_at | DATETIME | - | 예약 생성일 |
| updated_at | DATETIME | - | 예약 수정일 |

**관계**
- User 1 : N Reservation
- ConcertSchedule 1 : N Reservation


### 7. ReservationSeat

하나의 예약에 어떤 좌석이 포함되어 있는지 저장한다.
예약과 좌석의 관계를 연결하며, 하나의 예약에서 여러 좌석을 선택할 수 있도록 한다.

| 컬럼 | 타입 | 키 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 예약 좌석 식별자 |
| reservation_id | BIGINT | FK | 예약 식별자 |
| seat_id | BIGINT | FK | 좌석 식별자 |
| price | DECIMAL(10,2) | - | 해당 좌석의 예매 금액 |
| created_at | DATETIME | - | 예약 좌석 생성일 |

**관계**
- Reservation 1 : N ReservationSeat
  - ReservationSeat의 `reservation_id`가 Reservation의 `id`를 참조한다.
  
- Seat 1 : N ReservationSeat
  - ReservationSeat의 `seat_id`가 Seat의 `id`를 참조한다.

### 8. Payment

예약에 대한 결제 정보를 저장한다.
하나의 예약에 대한 결제 상태와 결제 금액, 결제 수단 등을 관리한다.

| 컬럼 | 타입 | 키 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 결제 식별자 |
| reservation_id | BIGINT | FK, UNIQUE | 결제 대상 예약 식별자 |
| payment_method | VARCHAR(20) | - | 결제 수단 |
| status | VARCHAR(20) | - | 결제 상태 |
| amount | DECIMAL(10,2) | - | 결제 금액 |
| paid_at | DATETIME | - | 결제 완료 일시 |
| created_at | DATETIME | - | 결제 정보 생성일 |
| updated_at | DATETIME | - | 수정일 |

**관계**
- Reservation 1 : 1 Payment
  - Payment의 `reservation_id`가 Reservation의 `id`를 참조한다.
  - `reservation_id`에 UNIQUE를 설정하여 하나의 Reservation에 하나의 Payment만 연결한다.