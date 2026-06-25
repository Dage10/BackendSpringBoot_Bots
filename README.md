🌍 Languages:
[English](README.md) | [Español](README.es.md)

# 🤖 Bots Management Platform

> Full-stack web application to create, configure and monitor automation bots across Discord, Telegram, Reddit, YouTube and GitHub — with real-time notifications via Server-Sent Events (SSE).

![Java](https://img.shields.io/badge/Java-25-007396?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=flat&logo=springboot&logoColor=white)
![Next.js](https://img.shields.io/badge/Next.js-16.2.0-black?style=flat&logo=next.js&logoColor=white)
![React](https://img.shields.io/badge/React-19.2.4-61DAFB?style=flat&logo=react&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Neon](https://img.shields.io/badge/Neon-PostgreSQL-00E599?style=flat&logo=neon&logoColor=white)


---

## 🔗 Live Demo & Repositories

| Resource | Link |
|----------|------|
| **Backend API** | [https://backendspringboot-bots.onrender.com](https://backendspringboot-bots.onrender.com) |
| **Frontend** |  [https://frontend-bots-one.vercel.app](https://frontend-bots-one.vercel.app)  |
| **Backend repo** | [BackendSpringBoot_Bots](https://github.com/Dage10/BackendSpringBoot_Bots) |
| **Frontend repo** | [FrontendBots](https://github.com/Dage10/FrontendBots) |

> Health check: `GET /health` → `{"status":"ok"}`

---

## 🌐 Production Deployment

The application is deployed in a real cloud environment using **Vercel**, **Render**, and **Neon**.

| Component | Provider | URL |
|-----------|----------|-----|
| **Frontend (Next.js)** | Vercel | [https://frontend-bots-one.vercel.app](https://frontend-bots-one.vercel.app) |
| **Backend (Spring Boot)** | Render | [https://backendspringboot-bots.onrender.com](https://backendspringboot-bots.onrender.com) |
| **Database (PostgreSQL)** | Neon | Serverless cluster on `neon.tech` |

### 🏗️ Production Infrastructure

- Frontend deployed on **Vercel**.
- Backend deployed on **Render**.
- **Neon PostgreSQL** serverless database.
- CORS configured between Vercel ↔ Render  
- Secrets managed in Vercel, Render, and Neon. 
- All services communicate securely over **HTTPS**
  
---

## 📸 Screenshots

<!-- Replace with your own screenshots -->
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/a484736e-13c1-4bda-9d31-638736b2e30e" />
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/c7e2bf5a-1ad4-49ea-90fd-b6a620ecaaac" />
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/2dcb48a4-481a-427d-a31b-64314b7581d6" />


<img width="800" alt="Bot management dashboard" src="YOUR_SCREENSHOT_DASHBOARD" />
<img width="800" alt="Create bot form" src="YOUR_SCREENSHOT_CREATE" />
<img width="800" alt="Real-time SSE notification" src="YOUR_SCREENSHOT_SSE" />

---

## 🚀 What is Bots Management?

Bots Management is a centralized dashboard where users register, log in, and manage platform bots from a single UI. Each bot can **listen** for incoming updates, **send** messages/posts, or do **both**, depending on the platform.

The backend polls external APIs on a scheduled interval, processes events, and pushes updates to the browser in real time through SSE — scoped per authenticated user.

---

## ✨ Features

### 👤 Authentication & Users
- 🔐 User registration and login with JWT stored in `HttpOnly` cookies
- 👤 Session validation via `/api/users/me`
- 🚪 Logout with cookie invalidation
- 🔒 Account lockout after failed login attempts
- ⏱️ API rate limiting on login, register and general endpoints

### 🤖 Bot Management
- ➕ Create bots for **Discord**, **Telegram**, **Reddit**, **YouTube** and **GitHub Events**
- 📋 List, edit, delete and activate/deactivate bots
- 📤 Send messages/posts from the dashboard (sender-capable platforms)
- 🎯 Per-platform configuration (channel ID, subreddit, repo, API keys, etc.)
- 🔐 Bot tokens encrypted at rest (AES-GCM) in PostgreSQL

### 📡 Real-Time Updates
- 🔔 SSE stream with toast and browser notifications
- 📬 Platform events: new Discord/Telegram messages, Reddit posts, YouTube videos, GitHub activity
- 👤 Events delivered only to the bot owner (per-user SSE isolation)

### 🛡️ Security (OWASP-aligned)
- Argon2 password hashing
- Jakarta Bean Validation on all request DTOs
- IDOR protection — users can only access their own bots
- Sensitive tokens excluded from list API responses
- CORS restricted to configured frontend origin
- Security headers (HSTS, X-Content-Type-Options, X-Frame-Options)
- Environment-based secrets in production (`JWT_SECRET`, `CRYPTO_SECRET`)

---

## 🌐 Supported Platforms

| Platform | Listen | Send | Notes |
|----------|:------:|:----:|-------|
| **Discord** | ✅ | ✅ | Channel messages via Bot API v10 |
| **Telegram** | ✅ | ✅ | `getUpdates` / `sendMessage` |
| **Reddit** | ✅ | ✅ | Subreddit posts; OAuth refresh support |
| **YouTube** | ✅ | ❌ | New videos via Data API v3 |
| **GitHub Events** | ✅ | ❌ | Repo activity (push, issues, PRs, releases…) |

**Bot types:** `LISTENER` · `SENDER` · `BOTH`

**Official API docs:**
- [Discord](https://docs.discord.com/developers/reference)
- [Telegram](https://core.telegram.org/bots/api)
- [Reddit](https://www.reddit.com/dev/api/)
- [YouTube Data API](https://developers.google.com/youtube/v3)
- [GitHub Events](https://docs.github.com/en/rest/activity/events)

---

## 🏗️ Architecture

**Frontend** (Next.js 16 · React 19 · Tailwind CSS 4)
↕ REST + HttpOnly cookies for auth, SSE (`bot-event`) for real-time updates
**Backend** (Spring Boot 4 API · Java 25 · JWT + Security)

The backend talks to three things:
- **PostgreSQL** — stores users and bots
- **External APIs** — Discord, Telegram, Reddit, YouTube, GitHub
- **Scheduler** — a bot executor that polls active bots periodically

```
Frontend (Next.js)  <--REST + Cookies-->  Backend (Spring Boot)
Frontend (Next.js)  <--SSE bot-event---   Backend (Spring Boot)

Backend (Spring Boot) ---> PostgreSQL (users, bots)
Backend (Spring Boot) ---> External APIs (Discord, Telegram, Reddit, YouTube, GitHub)
Backend (Spring Boot) ---> Scheduler / Bot executor
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 25, Spring Boot 4.0.3, Spring Security, Spring Data JPA |
| **Frontend** | Next.js 16, React 19, TypeScript, Tailwind CSS 4 |
| **Database** | PostgreSQL |
| **Auth** | JWT (JJWT), Argon2, HttpOnly cookies |
| **Encryption** | AES-GCM for bot tokens at rest |
| **Real-time** | Server-Sent Events (SSE) |
| **HTTP client** | Spring RestClient / WebFlux |
| **Deployment** | Docker, Render |
| **Notifications UI** | react-hot-toast, Web Notifications API |

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/health` | Health check |
| `POST` | `/api/users/register` | Register user |
| `POST` | `/api/users/login` | Login (sets JWT cookie) |
| `POST` | `/api/users/logout` | Logout |
| `GET` | `/api/users/me` | Current user |
| `GET` | `/api/bots` | List user's bots (no secrets) |
| `GET` | `/api/bots/{id}` | Bot detail (includes tokens) |
| `POST` | `/api/bots` | Create bot |
| `PUT` | `/api/bots/{id}` | Update bot |
| `DELETE` | `/api/bots/{id}` | Delete bot |
| `PUT` | `/api/bots/{id}/state` | Toggle active/inactive |
| `POST` | `/api/bots/{id}/send` | Queue message to send |
| `GET` | `/api/sse/stream` | SSE stream (authenticated) |

---

## ⚙️ Environment Variables

### Backend (production)

| Variable | Description |
|----------|-------------|
| `DATABASE_URL` | PostgreSQL JDBC URL |
| `DATABASE_USERNAME` | Database user |
| `DATABASE_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing key (min. 32 chars) |
| `CRYPTO_SECRET` | AES key for bot token encryption (min. 32 chars) |
| `FRONTEND_URL` | Frontend origin for CORS (e.g. `https://frontend-bots-one.vercel.app`) |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `PORT` | Server port (Render sets automatically) |

### Frontend

| Variable | Description |
|----------|-------------|
| `NEXT_PUBLIC_API_URL` | Backend URL (e.g. `https://backendspringboot-bots.onrender.com`) |

---

## 🚀 Local Development

### Prerequisites
- Java 25
- Node.js 20+
- PostgreSQL

### Backend

```bash
cd BackendSpringBootBots
./gradlew bootRun
```

Default: `http://localhost:8080`
Configure `src/main/resources/application.properties` for local DB and JWT.

### Frontend

```bash
cd FrontendBots/frontend-bots
npm install
npm run dev
```

Default: `http://localhost:3000`
API URL defaults to `http://localhost:8080`.

### Docker (backend)

```bash
docker build -t bots-backend .
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=... \
  -e JWT_SECRET=... \
  -e CRYPTO_SECRET=... \
  -e FRONTEND_URL=http://localhost:3000 \
  bots-backend
```

---

## 📁 Project Structure

```
BackendSpringBootBots/
  src/main/java/.../
    config/          # Security, CORS, Password
    controllers/     # REST + SSE
    services/        # Business logic + Bot executor
    security/        # Auth, rate limit, lockout
    entities/        # User, Bot (JPA)
    exception/       # Global error handling
  Dockerfile

FrontendBots/frontend-bots/
  app/               # Next.js App Router pages
  components/        # Bot card, SSE listener
  app/context/       # Auth context
```

---

## 👤 Author

**David Gelmà Corral**
[![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?logo=linkedin)](https://www.linkedin.com/in/david-gelma-corral-92b817114/)
[![GitHub](https://img.shields.io/badge/GitHub-black?logo=github)](https://github.com/Dage10)
