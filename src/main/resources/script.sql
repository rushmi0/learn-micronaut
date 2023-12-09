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
    username      VARCHAR(255) UNIQUE,                                    -- ชื่อของผู้ใช้ที่ไม่ซ้ำกัน
    first_name    VARCHAR(255),
    last_name     VARCHAR(255),                                           -- bank_number ต้องมีความยาวที่เท่ากับ 10 ตัวตัวเลข.
    email         VARCHAR(255) CHECK (email LIKE '%_@_%._%') UNIQUE,
    phone_number  VARCHAR(10) UNIQUE,
    created_at    TIMESTAMPTZ  DEFAULT now(),
    user_type     UserType CHECK (user_type IN ('Normal', 'DogWalkers') ) -- Normal, DogWalkers
);


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

-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- สร้างตาราง DogWalkers
CREATE TABLE IF NOT EXISTS DogWalkers
(
    walker_id      SERIAL PRIMARY KEY,
    user_id        INTEGER UNIQUE REFERENCES UserProfiles (user_id),                                                                        -- ทำการเชื่อมและระบุให้ user_id เป็น unique เพื่อป้องกันความซ้ำซ้อน
    location_name  VARCHAR(255) NOT NULL,
    id_card_number INTEGER CHECK (LENGTH(CAST(id_card_number AS VARCHAR)) = 10) DEFAULT 'N/A',
    verification   Verify                                                       DEFAULT 'false' CHECK ( verification IN ('true', 'false')), -- กำหนดค่าเริ่มต้นเป็น false
    price_small    INTEGER      NOT NULL,
    price_medium   INTEGER      NOT NULL,
    price_big      INTEGER      NOT NULL
);


-- //////////////////////////////////////////////////////////////////////////////////////////////////////


-- สร้างตาราง Dogs และ DogWalkBookings
CREATE TABLE IF NOT EXISTS Dogs
(
    dog_id     SERIAL PRIMARY KEY,
    dog_image  VARCHAR(255) NOT NULL,
    breed_name VARCHAR(255) NOT NULL,
    size       DogSize      NOT NULL CHECK (size IN ('Small', 'Medium', 'Big') ) -- small, medium, big
);

-- //////////////////////////////////////////////////////////////////////////////////////////////////////


CREATE TABLE IF NOT EXISTS DogWalkBookings
(
    booking_id SERIAL PRIMARY KEY,
    walker_id  INTEGER REFERENCES DogWalkers (walker_id),
    user_id    INTEGER REFERENCES UserProfiles (user_id),
    dog_id     INTEGER REFERENCES Dogs (dog_id),
    status     State NOT NULL DEFAULT 'Pending' CHECK ( status IN ('Confirm', 'Cancel', 'Pending') ),
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


-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- โค้ดต่อไปนี้จะเป็นการเพิ่ม Index เพื่อเพิ่มประสิทธิภาพในการค้นหา
CREATE INDEX idx_users_email ON UserProfiles (email);
CREATE INDEX idx_reviews_walkerid ON DogWalkerReviews (walker_id);
CREATE INDEX idx_reviews_userid ON DogWalkerReviews (user_id);

CREATE INDEX idx_users_public_key ON UserAuthentication (public_key);
CREATE INDEX idx_users_server_private_key ON UserAuthentication (server_private_key);
CREATE INDEX idx_share_key ON UserAuthentication (shared_key);



-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- สร้างข้อมูลจำลองในตาราง UserProfiles
INSERT INTO UserProfiles (image_profile, username, first_name, last_name, email, phone_number, user_type)
VALUES ('profile1.jpg', 'user1', 'John', 'Doe', 'john.doe@email.com', '1234567890', 'Normal'),
       ('profile2.jpg', 'user2', 'Jane', 'Smith', 'jane.smith@email.com', '9876543210', 'DogWalkers'),
       ('profile3.jpg', 'user3', 'Bob', 'Johnson', 'bob.johnson@email.com', '5555555555', 'Normal'),
       ('profile4.jpg', 'user4', 'Alice', 'Williams', 'alice.williams@email.com', '1111111111', 'DogWalkers'),
       ('profile5.jpg', 'user5', 'Charlie', 'Brown', 'charlie.brown@email.com', '9999999999', 'Normal');

-- สร้างข้อมูลจำลองในตาราง UserAuthentication
INSERT INTO UserAuthentication (user_id, public_key, server_private_key, shared_key)
VALUES (1, 123456, 789012, 345678),
       (2, 987654, 321098, 765432),
       (3, 111222, 333444, 555666),
       (4, 777888, 999000, 123456),
       (5, 555111, 222444, 666999);

-- สร้างข้อมูลจำลองในตาราง DogWalkers
INSERT INTO DogWalkers (user_id, location_name, id_card_number, verification, price_small, price_medium, price_big)
VALUES (1, 'Park1', 1234567890, 'true', 50, 70, 90),
       (2, 'Park2', 0987654321, 'false', 60, 80, 100),
       (3, 'Park3', 1122334455, 'true', 55, 75, 95),
       (4, 'Park4', 6677889900, 'false', 65, 85, 105),
       (5, 'Park5', 5432109876, 'true', 70, 90, 110);

-- สร้างข้อมูลจำลองในตาราง Dogs
INSERT INTO Dogs (dog_image, breed_name, size)
VALUES ('dog1.jpg', 'Golden Retriever', 'Big'),
       ('dog2.jpg', 'Beagle', 'Small'),
       ('dog3.jpg', 'Labrador', 'Medium'),
       ('dog4.jpg', 'Poodle', 'Small'),
       ('dog5.jpg', 'German Shepherd', 'Big');

-- สร้างข้อมูลจำลองในตาราง DogWalkBookings
INSERT INTO DogWalkBookings (walker_id, user_id, dog_id, status, time_start, time_end)
VALUES (1, 2, 1, 'Confirm', '10:00', '11:00'),
       (2, 3, 2, 'Pending', '12:00', '13:00'),
       (3, 4, 3, 'Cancel', '14:00', '15:00'),
       (4, 5, 4, 'Pending', '16:00', '17:00'),
       (5, 1, 5, 'Confirm', '18:00', '19:00');

-- สร้างข้อมูลจำลองในตาราง DogWalkerReviews
INSERT INTO DogWalkerReviews (walker_id, user_id, rating, review_text)
VALUES (1, 2, 4, 'Great service!'),
       (2, 3, 5, 'Amazing walker!'),
       (3, 4, 3, 'Good experience'),
       (4, 5, 2, 'Not satisfied'),
       (5, 1, 5, 'Highly recommended');


-- //////////////////////////////////////////////////////////////////////////////////////////////////////

-- ตัวอย่างการค้นหาข้อมูล
SELECT *
FROM DogWalkBookings;

SELECT *
FROM DogWalkers;

SELECT UP.*
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
