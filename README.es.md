🌍 Idiomas:
[English](README.md) | [Español](README.es.md)

# 🤖 Plataforma de Gestión de Bots

> Aplicación web full-stack para crear, configurar y monitorizar bots de automatización en Discord, Telegram, Reddit, YouTube y GitHub, con notificaciones en tiempo real mediante Server-Sent Events (SSE).

![Java](https://img.shields.io/badge/Java-25-007396?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=flat&logo=springboot&logoColor=white)
![Next.js](https://img.shields.io/badge/Next.js-16.2.0-black?style=flat&logo=next.js&logoColor=white)
![React](https://img.shields.io/badge/React-19.2.4-61DAFB?style=flat&logo=react&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-000000?style=flat&logo=supabase&logoColor=white)

---

## 🔗 Demo en Vivo y Repositorios

| Recurso | Enlace |
|----------|------|
| **API Backend** | [https://backendspringboot-bots.onrender.com](https://backendspringboot-bots.onrender.com) |
| **Frontend** | [https://frontend-bots-one.vercel.app](https://frontend-bots-one.vercel.app) |
| **Repositorio Backend** | [BackendSpringBoot_Bots](https://github.com/Dage10/BackendSpringBoot_Bots) |
| **Repositorio Frontend** | [FrontendBots](https://github.com/Dage10/FrontendBots) |

> Health check: `GET /health` → `{"status":"ok"}`

---

## 🌐 Despliegue en Producción

La aplicación está desplegada en un entorno cloud real utilizando **Vercel**, **Render** y **Supabase**.

| Componente | Proveedor | URL |
|-----------|----------|-----|
| **Frontend (Next.js)** | Vercel | [https://frontend-bots-one.vercel.app](https://frontend-bots-one.vercel.app) |
| **Backend (Spring Boot)** | Render | [https://backendspringboot-bots.onrender.com](https://backendspringboot-bots.onrender.com) |
| **Base de Datos (PostgreSQL)** | Supabase | PostgreSQL gestionado (acceso vía JDBC/JPA) |

### 🏗️ Infraestructura de Producción

- Frontend desplegado en **Vercel**.
- Backend desplegado en **Render**.
- Base de datos **Supabase PostgreSQL** gestionada (con connection pooling).
- CORS configurado entre Vercel ↔ Render.
- Secretos gestionados en Vercel, Render y Supabase.
- Todos los servicios se comunican de forma segura mediante **HTTPS**.

---

## 📸 Capturas de Pantalla

<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/a484736e-13c1-4bda-9d31-638736b2e30e" />
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/c7e2bf5a-1ad4-49ea-90fd-b6a620ecaaac" />
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/2dcb48a4-481a-427d-a31b-64314b7581d6" />
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/6f3bc006-69a9-4029-891a-b02ba4cc1c0b" />
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/fd310fe7-4d4c-46a8-a123-58338d193ed9" />
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/1b2e4fb9-9ced-47b7-b9bf-7c32318c48b7" />
<img width="800" height="400" alt="image" src="https://github.com/user-attachments/assets/fb9eac16-f8fe-4fa2-90be-d0bd84755eac" />

---

## 🚀 ¿Qué es Bots Management?

Bots Management es un panel centralizado donde los usuarios pueden registrarse, iniciar sesión y gestionar bots de diferentes plataformas desde una única interfaz. Cada bot puede **escuchar** actualizaciones entrantes, **enviar** mensajes/publicaciones o realizar **ambas funciones**, dependiendo de la plataforma.

El backend consulta periódicamente APIs externas, procesa eventos y envía actualizaciones al navegador en tiempo real mediante SSE, aisladas por usuario autenticado.

---

## ✨ Características

### 👤 Autenticación y Usuarios
- 🔐 Registro e inicio de sesión mediante JWT almacenado en cookies `HttpOnly`
- 👤 Validación de sesión mediante `/api/users/me`
- 🚪 Cierre de sesión con invalidación de cookies
- 🔒 Bloqueo de cuenta tras múltiples intentos fallidos de inicio de sesión
- ⏱️ Limitación de tasa (rate limiting) en login, registro y endpoints generales

### 🤖 Gestión de Bots
- ➕ Creación de bots para **Discord**, **Telegram**, **Reddit**, **YouTube** y **GitHub Events**
- 📋 Listado, edición, eliminación y activación/desactivación de bots
- 📤 Envío de mensajes/publicaciones desde el panel (plataformas con capacidad de envío)
- 🎯 Configuración específica por plataforma (ID de canal, subreddit, repositorio, claves API, etc.)
- 🔐 Tokens de bots cifrados en reposo mediante AES-GCM en PostgreSQL

### 📡 Actualizaciones en Tiempo Real
- 🔔 Flujo SSE con notificaciones toast y notificaciones del navegador
- 📬 Eventos de plataforma: nuevos mensajes de Discord/Telegram, publicaciones de Reddit, vídeos de YouTube y actividad de GitHub
- 👤 Los eventos se entregan únicamente al propietario del bot (aislamiento SSE por usuario)

### 🛡️ Seguridad (alineada con OWASP)
- Hashing de contraseñas con Argon2
- Validación Jakarta Bean Validation en todos los DTOs de entrada
- Protección contra IDOR: los usuarios solo pueden acceder a sus propios bots
- Exclusión de tokens sensibles en las respuestas de las APIs de listado
- CORS restringido al origen del frontend configurado
- Cabeceras de seguridad (HSTS, X-Content-Type-Options, X-Frame-Options)
- Secretos gestionados mediante variables de entorno en producción (`JWT_SECRET`, `CRYPTO_SECRET`)

---

## 🌐 Plataformas Compatibles

| Plataforma | Escuchar | Enviar | Notas |
|----------|:------:|:----:|-------|
| **Discord** | ✅ | ✅ | Mensajes de canal mediante Bot API v10 |
| **Telegram** | ✅ | ✅ | `getUpdates` / `sendMessage` |
| **Reddit** | ✅ | ✅ | Publicaciones en subreddits; soporte para refresco OAuth |
| **YouTube** | ✅ | ❌ | Nuevos vídeos mediante Data API v3 |
| **GitHub Events** | ✅ | ❌ | Actividad del repositorio (push, issues, PRs, releases...) |

**Tipos de bot:** `LISTENER` · `SENDER` · `BOTH`

**Documentación oficial de APIs:**
- [Discord](https://docs.discord.com/developers/reference)
- [Telegram](https://core.telegram.org/bots/api)
- [Reddit](https://www.reddit.com/dev/api/)
- [YouTube Data API](https://developers.google.com/youtube/v3)
- [GitHub Events](https://docs.github.com/en/rest/activity/events)

---

## 🏗️ Arquitectura

**Frontend** (Next.js 16 · React 19 · Tailwind CSS 4)  
↕ REST + cookies HttpOnly para autenticación, SSE (`bot-event`) para actualizaciones en tiempo real

**Backend** (API Spring Boot 4 · Java 25 · JWT + Security)

El backend se comunica con tres componentes:

- **PostgreSQL** — almacena usuarios y bots
- **APIs Externas** — Discord, Telegram, Reddit, YouTube y GitHub
- **Scheduler** — ejecutor de bots que consulta periódicamente los bots activos

```text
Frontend (Next.js)  <--REST + Cookies-->  Backend (Spring Boot)
Frontend (Next.js)  <--SSE bot-event---   Backend (Spring Boot)

Backend (Spring Boot) ---> PostgreSQL (usuarios, bots)
Backend (Spring Boot) ---> APIs Externas (Discord, Telegram, Reddit, YouTube, GitHub)
Backend (Spring Boot) ---> Scheduler / Ejecutor de bots
```

---

## 🛠️ Stack Tecnológico

| Capa | Tecnología |
|-------|-----------|
| **Backend** | Java 25, Spring Boot 4.0.3, Spring Security, Spring Data JPA |
| **Frontend** | Next.js 16, React 19, TypeScript, Tailwind CSS 4 |
| **Base de Datos** | PostgreSQL |
| **Autenticación** | JWT (JJWT), Argon2, cookies HttpOnly |
| **Cifrado** | AES-GCM para tokens de bots en reposo |
| **Tiempo Real** | Server-Sent Events (SSE) |
| **Cliente HTTP** | Spring RestClient / WebFlux |
| **Despliegue** | Docker, Render |
| **Interfaz de Notificaciones** | react-hot-toast, Web Notifications API |

---

## 📡 Endpoints de la API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/health` | Verificación de estado |
| `POST` | `/api/users/register` | Registrar usuario |
| `POST` | `/api/users/login` | Iniciar sesión (establece cookie JWT) |
| `POST` | `/api/users/logout` | Cerrar sesión |
| `GET` | `/api/users/me` | Usuario actual |
| `GET` | `/api/bots` | Listar bots del usuario (sin secretos) |
| `GET` | `/api/bots/{id}` | Detalle del bot (incluye tokens) |
| `POST` | `/api/bots` | Crear bot |
| `PUT` | `/api/bots/{id}` | Actualizar bot |
| `DELETE` | `/api/bots/{id}` | Eliminar bot |
| `PUT` | `/api/bots/{id}/state` | Activar/desactivar |
| `POST` | `/api/bots/{id}/send` | Encolar mensaje para envío |
| `GET` | `/api/sse/stream` | Flujo SSE (autenticado) |

---

## ⚙️ Variables de Entorno

### Backend (producción)

| Variable | Descripción |
|----------|-------------|
| `DATABASE_URL` | URL JDBC de PostgreSQL |
| `DATABASE_USERNAME` | Usuario de la base de datos |
| `DATABASE_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Clave de firma JWT (mín. 32 caracteres) |
| `CRYPTO_SECRET` | Clave AES para cifrado de tokens (mín. 32 caracteres) |
| `FRONTEND_URL` | Origen del frontend para CORS (ej. `https://frontend-bots-one.vercel.app`) |
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `PORT` | Puerto del servidor (Render lo configura automáticamente) |

### Frontend

| Variable | Descripción |
|----------|-------------|
| `NEXT_PUBLIC_API_URL` | URL del backend (ej. `https://backendspringboot-bots.onrender.com`) |

---

## 🚀 Desarrollo Local

### Requisitos Previos
- Java 25
- Node.js 20+
- PostgreSQL

### Backend

```bash
cd BackendSpringBootBots
./gradlew bootRun
```

Por defecto: `http://localhost:8080`

Configura `src/main/resources/application.properties` para la base de datos local y JWT.

### Frontend

```bash
cd FrontendBots/frontend-bots
npm install
npm run dev
```

Por defecto: `http://localhost:3000`

La URL de la API apunta por defecto a `http://localhost:8080`.

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

## 📁 Estructura del Proyecto

```text
BackendSpringBootBots/
  src/main/java/.../
    config/          # Seguridad, CORS, Contraseñas
    controllers/     # REST + SSE
    services/        # Lógica de negocio + Ejecutor de bots
    security/        # Auth, rate limit, bloqueo de cuentas
    entities/        # User, Bot (JPA)
    exception/       # Gestión global de errores
  Dockerfile

FrontendBots/frontend-bots/
  app/               # Páginas App Router de Next.js
  components/        # Tarjetas de bots, listener SSE
  app/context/       # Contexto de autenticación
```

---

## 👤 Autor

**David Gelmà Corral**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?logo=linkedin)](https://www.linkedin.com/in/david-gelma-corral-92b817114/)
[![GitHub](https://img.shields.io/badge/GitHub-black?logo=github)](https://github.com/Dage10)
