# Spring Boot User & Auth API

This is a simple API for authentication and CRUD user

Created By Alvan Dwi Akbar Al Amien

## Table of Contents
- [Requirements](#requirements)
- [Installation](#installation)
- [Database Migration](#database-migration)
- [API Endpoints](#api-endpoints)
    - [1. Login](#1-login)
    - [2. This User Details](#2-this-user-details)
    - [3. Refresh Login](#3-refresh-login)
    - [4. Log Out](#4-log-out)
- [Contributing](#contributing)
- [How to Contribute](#how-to-contribute)

## Requirements
- Java >= 17
- intellij or similar
- JDE & JDK
- MySQL

## Installation
1. **Clone the repository :**
   ```bash
   git clone https://github.com/odp309/springboot-auth.git
   cd springboot-auth
2. **Install dependencies :**
    ```bash
    refresh dependencies
3. **Copy the application.properties.copy then remove .copy and configure your database & secret sign/key settings.**

## Database Migration
import sql.sql into your database
## API Endpoints
### 1. Login
```bash
Method      : POST
Endpoint    : '/api/v1/public/auth/login'
Description : Login and get auth token.
```
> Request :
```json
{
  "email" : "ex@gmail.com",
  "password" : "123456"
}
```
> Response :
```json
{
  "statusCode": "OK",
  "message": "Success",
  "type": "application/json",
  "result": {
    "token": "token"
  }
}
```
### 2. This User Details
```bash
Method      : GET 
Endpoint    : '/api/v1/private/user/profile'
Description : Get login user.
```
> Response :
```json
{
  "statusCode": "OK",
  "message": "Success",
  "type": "application/json",
  "result": {
    "email": "ex@gmail.com",
    "firstName": "Alvan",
    "lastName": "Dwi"
  }
}
```
### 3. Refresh Login
```bash
Method      : POST 
Endpoint    : '/api/v1/public/auth/refreshToken'
Description : Getting a new access token using token refresh.
```
> Request :
```json
{
  "token" : "fb93368e-4940-4e35-8b25-e9af1caf0d1c"
}
```
> Response :
```json
{
  "statusCode": "OK",
  "message": "Success",
  "type": "application/json",
  "result": {
    "token": "fb93368e-4940-4e35-8b25-e9af1caf0d1c",
    "accessToken": "token"
  }
}
```
### 4. Log Out
```bash
Method      : POST 
Endpoint    : '/api/v1/public/auth/logout'
Description : Logged out a user.
```
> Header :
```json
{
  "authorization" : "Bearer ho21hto321%T21t5n32oiot23hoihoihoij0"
}
```
> Response :
```json
{
  "statusCode": "OK",
  "message": "Success",
  "type": "application/json",
  "result": "Logged Out Successfully"
}
```

## Contributing
Welcome and I'm appreciated contributions from the community! To contribute to the Laravel Customer and Address API, please follow these guidelines:
## How to Contribute
1. Fork the repository.
2. Clone your forked repository to your local machine:
   ```bash
   git clone https://github.com/Zigax201/customer.git
   cd customer
3. Create a new branch for your feature or bug fix:
    ```bash
    git checkout -b feature/your-feature-name
4. Make your changes and commit them:
    ```bash
    git add .
    git commit -m "Add your descriptive commit message"
5. Push your changes to your fork on GitHub:
    ```bash
    git push origin feature/your-feature-name
6. Create a pull request (PR) from your forked repository to the main repository.


Provide a clear and descriptive title for your PR. Include information about the changes you made.

Your PR will be reviewed, and if everything looks good, it will be merged.