# โครงสร้างฐานข้อมูล

ในที่นี้มีสกีมาเบส SQL สำหรับฐานข้อมูลแอปพลิเคชัน DogWalking ประกอบด้วยตารางสำหรับโปรไฟล์ผู้ใช้, การยืนยันตัวตน, ผู้เดินสุนัข, สุนัข, การจองการเดินสุนัข, และรีวิว

## สารบัญ
- [ENUM](#ENUM)
- [UserProfiles](#userprofiles)
- [UserAuthentication](#userauthentication)
- [DogWalkers](#dogwalkers)
- [Dogs](#dogs)
- [DogWalkBookings](#dogwalkbookings)
- [DogWalkerReviews](#dogwalkerreviews)
- [Indexes](#indexes)


## ENUM

ก่อนที่จะทำการสร้างตาราง เราจำเป็นต้องสร้างประเภทข้อมูล ENUM ก่อน เพื่อให้สามารถกำหนดค่าตัวแปรของบางคอลัมน์ได้แบบจำกัด

### UserType (ประเภทผู้ใช้)
 - ประกอบด้วยค่า: 'Normal' (ผู้ใช้ทั่วไป), 'DogWalkers' (ผู้เดินสุนัข)

### DogSize (ขนาดของสุนัข)
 - ประกอบด้วยค่า: 'Small' (ขนาดเล็ก), 'Medium' (ขนาดกลาง), 'Big' (ขนาดใหญ่)

### State (สถานะ)
 - ประกอบด้วยค่า: 'Confirm' (ยืนยัน), 'Cancel' (ยกเลิก), 'Pending' (รอดำเนินการ)

### Verify (การยืนยัน)
 - ประกอบด้วยค่า: 'true' (จริง), 'false' (เท็จ)

## UserProfiles

ตาราง `UserProfiles` เก็บข้อมูลเกี่ยวกับผู้ใช้ รวมถึงภาพโปรไฟล์, ชื่อผู้ใช้, ชื่อ, อีเมล, เบอร์โทร, และประเภทผู้ใช้

| Column Name   | Data Type          | Constraints                           |
|---------------|--------------------|----------------------------------------|
| user_id       | SERIAL             | PRIMARY KEY                            |
| image_profile | VARCHAR(255)       | DEFAULT 'N/A'                         |
| username      | VARCHAR(255)       | UNIQUE                                 |
| first_name    | VARCHAR(255)       |                                        |
| last_name     | VARCHAR(255)       |                                        |
| email         | VARCHAR(255)       | UNIQUE, CHECK (email LIKE '%_@_%._%')  |
| phone_number  | VARCHAR(10)        | UNIQUE                                 |
| created_at    | TIMESTAMPTZ        | DEFAULT now()                          |
| user_type     | UserType           | CHECK (user_type IN ('Normal', 'DogWalkers'))

...

## UserAuthentication

ตาราง `UserAuthentication` เก็บข้อมูลเกี่ยวกับการยืนยันตัวตนของผู้ใช้ รวมถึง public key, server private key, และ shared key

| Column Name         | Data Type  | Constraints                                   |
|---------------------|------------|-----------------------------------------------|
| user_id             | SERIAL     | PRIMARY KEY, REFERENCES UserProfiles(user_id) |
| public_key          | BIGINT     |                                               |
| server_private_key  | BIGINT     |                                               |
| shared_key          | BIGINT     |                                               |

...

## DogWalkers

ตาราง `DogWalkers` เก็บข้อมูลเกี่ยวกับผู้เดินสุนัข รวมถึงที่ตั้ง, เลขบัตรประชาชน, สถานะการยืนยันตัวตน, และราคาต่างๆ สำหรับขนาดสุนัขที่ต่างกัน

| Column Name     | Data Type  | Constraints                                        |
|-----------------|------------|----------------------------------------------------|
| walker_id       | SERIAL     | PRIMARY KEY                                        |
| user_id         | INTEGER    | UNIQUE, REFERENCES UserProfiles(user_id)           |
| location_name   | VARCHAR(255)| NOT NULL                                           |
| id_card_number  | INTEGER    | CHECK (LENGTH(CAST(id_card_number AS VARCHAR)) = 10) |
| verification    | Verify     | DEFAULT 'false', CHECK (verification IN ('true', 'false')) |
| price_small     | INTEGER    | NOT NULL                                           |
| price_medium    | INTEGER    | NOT NULL                                           |
| price_big       | INTEGER    | NOT NULL                                           |

...

## Dogs

ตาราง `Dogs` เก็บข้อมูลเกี่ยวกับสุนัข รวมถึงภาพ, สายพันธุ์, และขนาด

| Column Name | Data Type  | Constraints                               |
|-------------|------------|-------------------------------------------|
| dog_id      | SERIAL     | PRIMARY KEY                               |
| dog_image   | VARCHAR(255)| NOT NULL                                  |
| breed_name  | VARCHAR(255)| NOT NULL                                  |
| size        | DogSize     | NOT NULL, CHECK (size IN ('Small', 'Medium', 'Big'))

...

## DogWalkBookings

ตาราง `DogWalkBookings` เก็บข้อมูลเกี่ยวกับการจองการเดินสุนัข รวมถึงเดินสุนัข, ผู้ใช้, สุนัข, สถานะ, และเวลา

| Column Name | Data Type     | Constraints                                  |
|-------------|---------------|----------------------------------------------|
| booking_id  | SERIAL        | PRIMARY KEY                                  |
| walker_id   | INTEGER       | REFERENCES DogWalkers(walker_id)             |
| user_id     | INTEGER       | REFERENCES UserProfiles(user_id)             |
| dog_id      | INTEGER       | REFERENCES Dogs(dog_id)                       |
| status      | State         | NOT NULL, DEFAULT 'Pending', CHECK (status IN ('Confirm', 'Cancel', 'Pending')) |
| time_start  | TIME          |                                              |
| time_end    | TIME          |                                              |
| timestamp   | TIMESTAMPTZ   | DEFAULT now()                                |

...

## DogWalkerReviews

ตาราง `DogWalkerReviews` เก็บรีวิวจากผู้ใช้เกี่ยวกับผู้เดินสุนัข

| Column Name | Data Type     | Constraints                                  |
|-------------|---------------|----------------------------------------------|
| review_id   | SERIAL        | PRIMARY KEY                                  |
| walker_id   | INTEGER       | REFERENCES DogWalkers(walker_id)             |
| user_id     | INTEGER       | REFERENCES UserProfiles(user_id)             |
| rating      | INTEGER       | CHECK (rating >= 1 AND rating <= 5)          |
| review_text | VARCHAR(500)  |                                              |

...

## Indexes

เพื่อเพิ่มประสิทธิภาพในการค้นหา ได้ทำการเพิ่มดัชนีต่อไปนี้:

- `idx_users_email` on `UserProfiles(email)`
- `idx_reviews_walkerid` on `DogWalkerReviews(walker_id)`
- `idx_reviews_userid` on `DogWalkerReviews(user_id)`
- `idx_users_public_key` on `UserAuthentication(public_key)`
- `idx_users_server_private_key` on `UserAuthentication(server_private_key)`
- `idx_share_key` on `UserAuthentication(shared_key)`

