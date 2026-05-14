# Wehear Backend

Spring Boot API for Wehear project.

## Technologies
- Java 17+
- Spring Boot 3
- MySQL
- Cloudinary (for Media Storage)
- Gemini AI (for Sign Language translation processing)

## Setup
1. Clone the repository.
2. Copy `.env.example` to `.env` and fill in your credentials.
3. Run with Maven: `./mvnw spring-boot:run`

## Database
- Schema is located in `db/schema.sql`.

## Deployment
- Target: Railway.app
- Use the provided `Dockerfile` for deployment.
