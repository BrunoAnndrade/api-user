# Simple social network -API

## Description

This is a social network API developed to manage users, posts, and followers.

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Quarkus](https://img.shields.io/badge/quarkus-%234794EB.svg?style=for-the-badge&logo=quarkus&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/postgresql-4169e1?style=for-the-badge&logo=postgresql&logoColor=white)

### Why FiadoSafe?

The purpose of creating this API was to introduce learning about the Quarkus framework.

---

## Installation

### Prerequisites 

![Java11](https://img.shields.io/badge/Java-11-blue)
![Maven3.9.9](https://img.shields.io/badge/Maven-3.9.9-blue)

1. Clone the repository:
   
```bash
git clone https://github.com/your-username/project-name.git
```

2. Install dependencies with Maven
3. Install [PostgreSQL](https://www.postgresql.org/download/)

### Accessing the API

- Start the application with Maven
- Make sure that PostgreSQL is running locally at jdbc:postgresql://localhost:5432/quarkus-social.

### 1. **User Management**

- **List Users**: `GET /users`
- **Create User**: `POST /users`
- **Update User**: `PUT /users/{id}`
- **Delete User**: `DELETE /users/{id}`

### 2. **Followers Management**

- **List Followers**: `GET /users/{userId}/followers`
- **Follow User**: `PUT /users/{userId}/followers`
- **Unfollow User**: `DELETE /users/{userId}/followers?followerId={followerId}`

### 3. **Posts Management**

- **List Posts**: `GET /users/{userId}/posts`
- **Create Post**: `POST /users/{userId}/posts`


---

![MT LICENCE](https://img.shields.io/badge/license-MIT-blue)

## Contact: [Linkedin](https://www.linkedin.com/in/brunoanndrad/)

