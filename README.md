# project-angler

## Overview

A web app intended to assist the user by simplifying
self informing and statistics tracking related to fishing.

Projects is still in progress at the time of writing this and is deployed at https://project-angler.onrender.com/ and currently serving about 5 users.

## Technologies Used

Backend: Java, Spring Boot, Postgres
Frontend: Thymeleaf, JavaScript, HTML, Tailwind CSS

## Features

Current fishing conditions info for multiple locations. Catch logging with captured fishing conditions data at that time. Partial Serbian and full English language support.

![Image](https://github.com/user-attachments/assets/46fb3a49-5285-4b5a-a183-e9477c8d5ea3)

![Image](https://github.com/user-attachments/assets/7986637e-f9b0-43f5-baec-d47014936319)

## Setup Instructions

Clone the repository:
```
git clone https://github.com/ognjenburmaz/project-angler.git
cd project-angler
```
Install dependencies:
```
npm ci
```
Configure the API keys that are needed by creating a .env file in the root folder, then populating it with key value pairs.

Run the application:
```
mvn spring-boot:run
```

Access the application at http://localhost:8080.

## Possible Updates

Database should be normalized. Should add some statistics processing and an admin dashboard. Implement JWT and Redis, switch to Angular for front.
