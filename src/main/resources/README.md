# DogWalking App Database Schema

This repository contains the SQL schema for a DogWalking application's database. The schema includes tables for user profiles, authentication, dog walkers, dogs, dog walk bookings, and reviews.

## Table of Contents
- [UserProfiles](#userprofiles)
- [UserAuthentication](#userauthentication)
- [DogWalkers](#dogwalkers)
- [Dogs](#dogs)
- [DogWalkBookings](#dogwalkbookings)
- [DogWalkerReviews](#dogwalkerreviews)
- [Indexes](#indexes)
- [Sample Data](#sample-data)

## UserProfiles

The `UserProfiles` table stores information about users, including their profile image, username, name, email, phone number, and user type.

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

The `UserAuthentication` table stores information related to user authentication, including public key, server private key, and shared key.

| Column Name         | Data Type  | Constraints                                   |
|---------------------|------------|-----------------------------------------------|
| user_id             | SERIAL     | PRIMARY KEY, REFERENCES UserProfiles(user_id) |
| public_key          | BIGINT     |                                               |
| server_private_key  | BIGINT     |                                               |
| shared_key          | BIGINT     |                                               |

...

## DogWalkers

The `DogWalkers` table stores information about dog walkers, including their location, ID card number, verification status, and pricing for different dog sizes.

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

The `Dogs` table stores information about dogs, including their image, breed, and size.

| Column Name | Data Type  | Constraints                               |
|-------------|------------|-------------------------------------------|
| dog_id      | SERIAL     | PRIMARY KEY                               |
| dog_image   | VARCHAR(255)| NOT NULL                                  |
| breed_name  | VARCHAR(255)| NOT NULL                                  |
| size        | DogSize     | NOT NULL, CHECK (size IN ('Small', 'Medium', 'Big'))

...

## DogWalkBookings

The `DogWalkBookings` table stores information about dog walk bookings, including the walker, user, dog, status, and timestamps.

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

The `DogWalkerReviews` table stores reviews from users about dog walkers.

| Column Name | Data Type     | Constraints                                  |
|-------------|---------------|----------------------------------------------|
| review_id   | SERIAL        | PRIMARY KEY                                  |
| walker_id   | INTEGER       | REFERENCES DogWalkers(walker_id)             |
| user_id     | INTEGER       | REFERENCES UserProfiles(user_id)             |
| rating      | INTEGER       | CHECK (rating >= 1 AND rating <= 5)          |
| review_text | VARCHAR(500)  |                                              |

...

## Indexes

To improve search performance, the following indexes have been added:

- `idx_users_email` on `UserProfiles(email)`
- `idx_reviews_walkerid` on `DogWalkerReviews(walker_id)`
- `idx_reviews_userid` on `DogWalkerReviews(user_id)`
- `idx_users_public_key` on `UserAuthentication(public_key)`
- `idx_users_server_private_key` on `UserAuthentication(server_private_key)`
- `idx_share_key` on `UserAuthentication(shared_key)`

## Sample Data

Sample data has been added to the tables for testing purposes. You can find the sample data insertion queries at the end of the SQL script.

Feel free to use or modify this schema for your DogWalking application's database.
