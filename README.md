# DSA Revision Tracker

A complete CSV-backed Spring Boot web application for tracking learned topics, solved coding problems, spaced-repetition revisions, weak areas, streaks, analytics, and interview readiness.

## Tech Stack

- Java 17
- Spring Boot 3
- HTML, CSS, JavaScript
- CSV file storage only

## Run

```powershell
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

On first startup, sample CSV files are copied from `src/main/resources/data` into a local `data` folder. Runtime changes are written to:

- `data/topics.csv`
- `data/problems.csv`
- `data/revisions.csv`
- `data/revision_history.csv`
- `data/streaks.csv`

## Features

- Add topics with category, learned date, confidence level, and notes.
- Add solved problems with platform, number, difficulty, tags, time taken, hints, independence, and notes.
- Automatically schedules new topics and problems for Day 1, Day 3, Day 7, Day 15, Day 30, Day 60, and Day 120.
- Shows today's revisions and overdue revisions separately.
- Re-solve mode hides notes until after recall is marked.
- Adaptive revision scheduling:
  - 1: next revision in 1 day
  - 2: next revision in 3 days
  - 3: next revision in 7 days
  - 4: next revision in 15 days
  - 5: next revision in 30 days
- Forgotten topics detector for 30, 60, and 90 day risk bands.
- Weak area analysis and smart recommendations based on solved tags.
- Pattern tracking by tags.
- Interview readiness score by core category and overall readiness.
- Daily dashboard with streaks, totals, revision time, and completion pressure.
- Search and filters for topics and problems.
- Analytics charts, tag distribution, completion rate, and activity heatmap.
- Export as JSON or individual CSV files.

## Architecture

The app is separated into:

- `model`: domain objects such as `Topic`, `Problem`, `Revision`, and `StreakStats`
- `storage`: CSV table parsing and repository classes
- `service`: business logic, spaced repetition, analytics, streaks, recommendations
- `controller`: REST API endpoints
- `static`: browser UI

The service layer depends on repository-style storage classes, so CSV persistence can later be replaced by MySQL or PostgreSQL repositories without changing the UI or core application flows.

## API Overview

- `GET /api/dashboard`
- `GET /api/topics`
- `POST /api/topics`
- `GET /api/problems`
- `POST /api/problems`
- `GET /api/revisions/today`
- `POST /api/revisions/{revisionId}/complete`
- `GET /api/analytics`
- `GET /api/progress`
- `GET /api/export/json`
- `GET /api/export/csv/{file}`

## Notes

This project intentionally avoids a database. CSV parsing supports quoted values, commas, and escaped quotes, which keeps notes safe while preserving simple inspectable files.
