-- การสร้างประเภทข้อมูล ENUM
CREATE TYPE UserType AS ENUM ('Normal', 'DogWalkers');
CREATE TYPE DogSize AS ENUM ('Small', 'Medium', 'Big');
CREATE TYPE State AS ENUM ('Confirm', 'Cancel', 'Pending');
CREATE TYPE Verify AS ENUM ('true', 'false');

-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- สร้างตาราง UserProfiles
CREATE TABLE IF NOT EXISTS UserProfiles
(
    user_id       SERIAL PRIMARY KEY,
    image_profile VARCHAR(255) DEFAULT 'N/A',
    username      VARCHAR(255) UNIQUE,                                                    -- ชื่อของผู้ใช้ที่ไม่ซ้ำกัน
    first_name    VARCHAR(255),
    last_name     VARCHAR(255),
    bank_name     VARCHAR(255) DEFAULT 'N/A',
    bank_number   INTEGER CHECK (LENGTH(CAST(bank_number AS VARCHAR)) = 10),              -- bank_number ต้องมีความยาวที่เท่ากับ 10 ตัวตัวเลข.
    email         VARCHAR(255) CHECK (email LIKE '%_@_%._%') UNIQUE,
    phone_number  VARCHAR(10) UNIQUE,
    created_at    TIMESTAMPTZ  DEFAULT now(),
    user_type     UserType,                                                               -- Normal, DogWalkers
    verification  Verify       DEFAULT 'false' CHECK ( verification IN ('true', 'false')) -- กำหนดค่าเริ่มต้นเป็น false
);

SELECT public_key
FROM userauthentication;
-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- สร้างตาราง UserAuthentication สำหรับเก็บข้อมูลการยืนยันตัวตนของผู้ใช้
CREATE TABLE IF NOT EXISTS UserAuthentication
(
    user_id            SERIAL PRIMARY KEY REFERENCES UserProfiles (user_id)
        ON DELETE CASCADE ON UPDATE CASCADE,                               -- รักษาความสัมพันธ์ที่ถูกต้องระหว่าง UserAuthentication และ UserProfiles
    public_key         BIGINT,                                             -- คีย์สาธารณะของผู้ใช้, มีข้อจำกัดที่ต้องเป็นจำนวนเต็มบวก ใช้ในการยืนยันตัวตนสำหรับการสื่อสารที่เปิดเผย
    CONSTRAINT public_key_positive CHECK (public_key > 0),                 -- ตรวจสอบว่า public_key เป็นจำนวนเต็มบวก
    server_private_key BIGINT,                                             -- คีย์ส่วนตัวของเซิร์ฟเวอร์, มีข้อจำกัดที่ต้องเป็นจำนวนเต็มบวก
    CONSTRAINT server_private_key_positive CHECK (server_private_key > 0), -- ตรวจสอบว่า server_private_key เป็นจำนวนเต็มบวก
    shared_key         BIGINT,                                             -- คีย์ที่แชร์จากการขับเคลื่อน EDCH ของผู้ใช้กับเซิร์ฟเวอร์, มีข้อจำกัดที่ต้องเป็นจำนวนเต็มบวก ใช้ในการเข้ารหัสและถอดรหัสการสื่อสารระหว่างผู้ใช้และเซิร์ฟเวอร์
    CONSTRAINT shared_key_positive CHECK (shared_key > 0)                  -- ตรวจสอบว่า shared_key เป็นจำนวนเต็มบวก
);

-- โค้ดต่อไปนี้จะเป็นการเพิ่ม Index เพื่อเพิ่มประสิทธิภาพในการค้นหา
CREATE INDEX idx_users_public_key ON UserAuthentication (public_key);
CREATE INDEX idx_users_server_private_key ON UserAuthentication (server_private_key);
CREATE INDEX idx_share_key ON UserAuthentication (shared_key);


-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- สร้างตาราง DogWalkers
CREATE TABLE IF NOT EXISTS DogWalkers
(
    walker_id     SERIAL PRIMARY KEY,
    user_id       INTEGER UNIQUE REFERENCES UserProfiles (user_id), -- ทำการเชื่อมและระบุให้ user_id เป็น unique เพื่อป้องกันความซ้ำซ้อน
    location_name VARCHAR(255) NOT NULL,
    price_small   INTEGER      NOT NULL,
    price_medium  INTEGER      NOT NULL,
    price_big     INTEGER      NOT NULL
);


-- //////////////////////////////////////////////////////////////////////////////////////////////////////


-- สร้างตาราง Dogs และ DogWalkBookings
CREATE TABLE IF NOT EXISTS Dogs
(
    dog_id     SERIAL PRIMARY KEY,
    dog_image  VARCHAR(255) NOT NULL,
    breed_name VARCHAR(255) NOT NULL,
    size       DogSize      NOT NULL -- small, medium, big
);

-- //////////////////////////////////////////////////////////////////////////////////////////////////////


CREATE TABLE IF NOT EXISTS DogWalkBookings
(
    booking_id SERIAL PRIMARY KEY,
    walker_id  INTEGER REFERENCES DogWalkers (walker_id),
    user_id    INTEGER REFERENCES UserProfiles (user_id),
    dog_id     INTEGER REFERENCES Dogs (dog_id),
    status     State NOT NULL DEFAULT 'Pending',
    time_start TIME,                        -- เวลาเริ่มจอง
    time_end   TIME,                        -- เวลาสิ้นสุด
    timestamp  TIMESTAMPTZ    DEFAULT now() -- ช่วงเวลาที่ทำการจอง
);


-- //////////////////////////////////////////////////////////////////////////////////////////////////////


-- สร้างตาราง DogWalkerReviews
CREATE TABLE IF NOT EXISTS DogWalkerReviews
(
    review_id   SERIAL PRIMARY KEY,
    walker_id   INTEGER REFERENCES DogWalkers (walker_id),
    user_id     INTEGER REFERENCES UserProfiles (user_id),
    rating      INTEGER CHECK (rating >= 1 AND rating <= 5),
    review_text VARCHAR(500) -- ข้อความรีวิวที่ผู้ใช้ให้
);

-- โค้ดต่อไปนี้จะเป็นการเพิ่ม Index เพื่อเพิ่มประสิทธิภาพในการค้นหา
CREATE INDEX idx_users_email ON UserProfiles (email);
CREATE INDEX idx_reviews_walkerid ON DogWalkerReviews (walker_id);
CREATE INDEX idx_reviews_userid ON DogWalkerReviews (user_id);


-- //////////////////////////////////////////////////////////////////////////////////////////////////////


-- สร้างข้อมูลจำลองในตาราง UserProfiles
INSERT INTO UserProfiles (image_profile, username, first_name, last_name, email, phone_number, user_type, verification)
VALUES ('profile1.jpg', 'user1', 'John', 'Doe', 'john.doe@email.com', '1234567890', 'Normal', 'true'),
       ('profile2.jpg', 'user2', 'Jane', 'Doe', 'jane.doe@email.com', '9876543210', 'DogWalkers', 'false'),
       ('profile3.jpg', 'user3', 'Bob', 'Smith', 'bob.smith@email.com', '5555555555', 'Normal', 'false'),
       ('profile4.jpg', 'user4', 'Alice', 'Johnson', 'alice.johnson@email.com', '6666666666', 'DogWalkers', 'true'),
       ('profile5.jpg', 'user5', 'Charlie', 'Brown', 'charlie.brown@email.com', '7777777777', 'Normal', 'false');

-- สร้างข้อมูลจำลองในตาราง UserAuthentication
INSERT INTO UserAuthentication (user_id, public_key, server_private_key, shared_key)
VALUES (1, 12345, 67890, 1111),
       (2, 54321, 98765, 2222),
       (3, 11111, 99999, 3333),
       (4, 99999, 11111, 4444),
       (5, 88888, 22222, 5555);

-- สร้างข้อมูลจำลองในตาราง DogWalkers
INSERT INTO DogWalkers (user_id, location_name, price_small, price_medium, price_big)
VALUES (1, 'Park1', 50, 70, 90),
       (2, 'Park2', 60, 80, 100),
       (3, 'Park3', 40, 60, 80),
       (4, 'Park4', 70, 90, 110),
       (5, 'Park5', 55, 75, 95);

-- สร้างข้อมูลจำลองในตาราง Dogs
INSERT INTO Dogs (dog_image, breed_name, size)
VALUES ('dog1.jpg', 'Labrador', 'Big'),
       ('dog2.jpg', 'Poodle', 'Small'),
       ('dog3.jpg', 'Beagle', 'Medium'),
       ('dog4.jpg', 'Husky', 'Big'),
       ('dog5.jpg', 'Dachshund', 'Small');

-- สร้างข้อมูลจำลองในตาราง DogWalkBookings
INSERT INTO DogWalkBookings (walker_id, user_id, dog_id, status, time_start, time_end)
VALUES (1, 3, 2, 'Pending', '10:00', '11:00'),
       (2, 4, 1, 'Confirm', '14:00', '15:00'),
       (3, 5, 3, 'Pending', '12:00', '13:00'),
       (4, 1, 4, 'Cancel', '16:00', '17:00'),
       (5, 2, 5, 'Confirm', '18:00', '19:00');

-- สร้างข้อมูลจำลองในตาราง DogWalkerReviews
INSERT INTO DogWalkerReviews (walker_id, user_id, rating, review_text)
VALUES (1, 3, 4, 'Great walker!'),
       (2, 4, 5, 'Excellent service!'),
       (3, 5, 3, 'Average experience'),
       (4, 1, 2, 'Not satisfied'),
       (5, 2, 5, 'Highly recommended');


-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- ตัวอย่างการค้นหาข้อมูล
SELECT *
FROM DogWalkBookings;

SELECT *
FROM DogWalkers;

SELECT UP.user_id,
       UP.image_profile,
       UP.username,
       UP.first_name,
       UP.last_name,
       UP.email,
       UP.phone_number,
       UP.created_at,
       UP.user_type,
       UP.verification,
       UA.public_key,
       UA.server_private_key,
       UA.shared_key
FROM UserProfiles UP
         INNER JOIN
     UserAuthentication UA
     ON
         UP.user_id = UA.user_id;



-- DROP TABLE IF EXISTS dogwalkerreviews;
-- DROP TABLE IF EXISTS dogWalkBookings;
-- DROP TABLE IF EXISTS dogwalkers;
-- DROP TABLE IF EXISTS DogDetail;
-- DROP TABLE IF EXISTS UserAuthen;
-- DROP TABLE IF EXISTS userprofiles;
