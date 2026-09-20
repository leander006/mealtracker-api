# Deploying backend to Render

## 1. Push this folder as its own git repo
```bash
cd backend
git init && git add . && git commit -m "Initial commit"
git remote add origin <your-backend-repo-url>
git push -u origin main
```

## 2. Create the Render service
- Render dashboard → **New → Web Service** → connect this repo.
- **Runtime**: Docker (uses the `Dockerfile` here — Maven build stage +
  slim JRE runtime stage, so Render doesn't need Java/Maven pre-installed).
- **Instance type**: Free tier is fine — Spring Boot on the JVM is a heavier
  cold start than ml-service, but doesn't need the RAM headroom torch does.
- Render sets `PORT` automatically - `application.yml` already reads it
  (`server.port: ${PORT:8080}`), nothing to configure.

## 3. Environment variables
Set these in the Render dashboard's **Environment** tab:

| Variable | Value |
|---|---|
| `DATABASE_URL` | `jdbc:postgresql://<neon-host>/<db>?sslmode=require` — note the `jdbc:` prefix, this is NOT the same string format ml-service uses |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | from the same Neon connection string, split out |
| `JWT_SECRET` | a long random string (`openssl rand -base64 32`) |
| `ML_SERVICE_URL` | your ml-service Render URL, e.g. `https://mealtracker-ml.onrender.com` |
| `CORS_ORIGINS` | your Vercel URL, e.g. `https://mealtracker.vercel.app` — **set this once you know it**, or the UI's login/scan/search calls through the backend will be blocked by CORS |

## 4. Deploy and verify
```bash
curl https://<your-backend>.onrender.com/api/food/search?q=rice
```
Should return JSON (empty results array is fine if ml-service isn't seeded yet;
what you're checking is that the backend reached ml-service successfully,
not a 502/timeout).

## Service-to-service call note
The backend calls ml-service over the public internet (Render doesn't give
you private networking between two services on the free tier the way
docker-compose does locally) - so `ML_SERVICE_URL` must be ml-service's
public HTTPS Render URL, not `localhost` or an internal hostname. Both
services being on Render's free tier means BOTH can cold-start-delay a
request if both have been idle — worth knowing if a request from the UI
times out after a period of no traffic.
