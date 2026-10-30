# Calculator REST API — CI/CD Pipeline

A simple Calculator REST API built with **Spring Boot**, wrapped in a fully automated CI/CD pipeline using **Git/GitHub, Jenkins, Docker and Docker Compose**.

A push to GitHub automatically builds, tests, containerizes, pushes and deploys the application.

- **GitHub:** https://github.com/kodercrish/Calculator-App-CICD
- **Docker Hub:** https://hub.docker.com/r/kodercrish/calculator

---

## Tech Stack

| Tool | Purpose |
|---|---|
| Java 21 + Spring Boot | REST API |
| Maven | Build and dependency management |
| JUnit 5 + MockMvc | Unit and integration tests |
| Docker | Containerization (multi-stage build) |
| Docker Compose | Deployment (app + Nginx reverse proxy) |
| Jenkins | CI/CD pipeline |
| GitHub | Version control |

---

## Project Structure

```
calculator-devops/
├── src/main/java/com/example/calculator/
│   ├── CalculatorApplication.java     # Spring Boot entry point
│   ├── ApiEndpoints.java              # Endpoint path constants
│   ├── CalculatorController.java      # REST endpoints
│   ├── CalculatorService.java         # Arithmetic logic
│   ├── CalculationResponse.java       # Success response (JSON)
│   ├── ErrorResponse.java             # Error response (JSON)
│   └── GlobalExceptionHandler.java    # Converts errors to HTTP 400 JSON
├── src/main/resources/
│   └── application.properties
├── src/test/java/com/example/calculator/
│   ├── CalculatorServiceTest.java     # JUnit 5 unit tests
│   └── CalculatorControllerTest.java  # MockMvc integration tests
├── nginx/nginx.conf                   # Nginx reverse proxy config
├── pom.xml                            # Maven build
├── Dockerfile                         # Multi-stage image build
├── .dockerignore
├── docker-compose.yml                 # App + Nginx
├── Jenkinsfile                        # CI/CD pipeline
├── .gitignore
└── README.md
```

---

## API Endpoints

| Operation | Endpoint | Description |
|---|---|---|
| Add | `GET /api/v1/add?a=&b=` | Returns a + b |
| Subtract | `GET /api/v1/subtract?a=&b=` | Returns a - b |
| Multiply | `GET /api/v1/multiply?a=&b=` | Returns a * b |
| Divide | `GET /api/v1/divide?a=&b=` | Returns a / b (b = 0 → HTTP 400) |
| Health | `GET /actuator/health` | Returns `{"status":"UP"}` |

**Success response:**
```json
{ "operation": "add", "a": 2.0, "b": 3.0, "result": 5.0 }
```

**Error response (HTTP 400):**
```json
{ "status": 400, "error": "Bad Request", "message": "Division by zero is not allowed" }
```

Errors returned with HTTP 400:
- Division by zero
- Missing parameter (e.g. `/add?a=5`)
- Non-numeric parameter (e.g. `/add?a=abc&b=2`)

---

## Run Locally (without Docker)

**Requirements:** Java 21, Maven

```bash
mvn clean package        # build the JAR (also runs tests)
mvn test                 # run tests only
java -jar target/calculator.jar
```

The app runs on **http://localhost:8080**:
```bash
curl "localhost:8080/api/v1/add?a=2&b=3"
```

---

## Run with Docker

```bash
docker build -t kodercrish/calculator:latest .
docker run -d --name calculator -p 8080:8080 kodercrish/calculator:latest
curl "localhost:8080/api/v1/add?a=2&b=3"
```

Stop and remove:
```bash
docker stop calculator && docker rm calculator
```

---

## Run with Docker Compose

Starts the calculator app and an Nginx reverse proxy in front of it.

```bash
docker compose up -d       # start
docker compose ps          # check status (calculator should be "healthy")
docker compose down        # stop
```

Access the API through Nginx on port **80**:
```bash
curl "localhost/api/v1/add?a=2&b=3"
curl "localhost/api/v1/divide?a=5&b=0"
curl localhost/actuator/health
```

```
Client ──► Nginx (port 80) ──► Calculator (port 8080, internal only)
```

The Compose setup includes:
- A custom network (`calc-net`) shared by both containers
- A health check on `/actuator/health`
- Restart policy `unless-stopped`
- Environment variables for the app
- Nginx starts only after the calculator is healthy

---

## CI/CD Pipeline (Jenkins)

The `Jenkinsfile` defines a declarative pipeline that runs automatically on every push to `main`.

```
Push to GitHub ─► Checkout ─► Build ─► Test ─► Docker Build ─► Push ─► Deploy ─► Email
```

| Stage | What it does |
|---|---|
| Checkout | Pulls the latest code from GitHub |
| Build | `mvn clean package -DskipTests` |
| Test | `mvn test`, publishes JUnit results, fails the build on test failure |
| Docker Build | Builds the image tagged with the build number and `latest` |
| Push | Pushes the image to Docker Hub using Jenkins credentials |
| Deploy | `docker compose up -d`, then verifies the health endpoint |

- **Trigger:** Poll SCM — Jenkins checks GitHub every minute and builds only when there are new commits.
- **Notifications:** an email is sent on success and on failure (with the build log attached).
- **Re-runnable:** re-running the pipeline only replaces containers whose image changed.

### Jenkins Setup

1. Install plugins: Git, Pipeline, JUnit, Email Extension, Credentials Binding.
2. Give Jenkins access to Docker: `sudo usermod -aG docker jenkins` and restart Jenkins.
3. Add credentials in **Manage Jenkins → Credentials**:
   - `dockerhub-creds` — Docker Hub username + access token
   - `gmail-creds` — Gmail address + App Password (for email notifications)
4. Configure email in **Manage Jenkins → System → Extended E-mail Notification**: SMTP `smtp.gmail.com`, port `465`, SSL, credentials `gmail-creds`.
5. Create a **Pipeline** job → *Pipeline script from SCM* → Git → this repository, branch `*/main`, script path `Jenkinsfile`.
6. Run the job once manually so Jenkins picks up the Poll SCM trigger.

No credentials are stored in this repository — all secrets live in the Jenkins Credentials store.
