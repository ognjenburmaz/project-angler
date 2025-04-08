# project-angler

## Overview

A web app intended to assist the user by simplifying
self informing and statistics tracking related to fishing.

## Technologies Used

Backend: Java, Spring Boot, Postgres
Frontend: Thymeleaf, JavaScript, HTML, Tailwind CSS

## Features

Current fishing conditions info for multiple locations. Catch logging. Partial Serbian and full English language support.

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

Run the application:
```
mvn spring-boot:run
```

Configure the API keys that are needed.

Access the application at http://localhost:8080.

## Possible Updates

Database should be normalized. Should add same images of the site to this README. Implement JWT and Redis, switch to Angular for front.