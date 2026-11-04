# FBI API

[![Open Issues](https://img.shields.io/github/issues-raw/UdL-EPS-SoftArch/fbi-API?logo=github)](https://github.com/UdL-EPS-SoftArch/fbi-API/issues)
[![CI/CD](https://github.com/UdL-EPS-SoftArch/fbi-API/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/UdL-EPS-SoftArch/fbi-API/actions/workflows/ci-cd.yml)
[![Cucumber Reports](https://img.shields.io/badge/cucumber-reports-brightgreen)](https://udl-eps-softarch.github.io/fbi-API/reports/cucumber-report.html)
[![Deployments](https://img.shields.io/badge/github-deployments-blue?logo=github)](https://github.com/UdL-EPS-SoftArch/fbi-API/deployments)

The FBI API is the backend being developed for FBI, a platform that helps students, primarily at the University of Lleida, prepare for their subjects through tests. It supports practice and self-assessment, progress tracking, and friendly competition through reliable scores and leaderboards.

The capabilities below describe the intended platform and are not necessarily implemented yet. For the role model, domain rules and detailed requirements, see [Platform Roles, Features and User Stories](docs/Context-FBI.md) and [User Stories](docs/User-Stories-FBI.md).

## Vision

FBI aims to give students an accessible way to explore subject material, practice with official and custom tests, review their results and measure their progress. The platform also aims to maintain fair competition and trustworthy content through automatic grading, privacy protections and administrative moderation.

Official test attempts can contribute to subject and global leaderboards using each student's personal best per eligible test. Custom tests are for practice and do not contribute to competitive rankings. Recorded results remain fixed when scoring criteria change.

## Features per Stakeholder

The roles are hierarchical: students can use public visitor features, and administrators can also use student features, subject to competitive restrictions.

| Visitor | Student | Administrator |
| --- | --- | --- |
| Browse subjects and published public tests; search or filter the catalog and view public test details | Take official and custom tests; review scores, correct answers, mistakes and test history | Manage subjects and create, edit, publish, withdraw and validate official tests, questions and answer options |
| View featured tests, public profiles, and subject and global leaderboards | Participate in subject and global leaderboards through eligible official test attempts | Moderate student-created content, review reports and resolve issues |
| Register, log in and recover a forgotten password | Create, publish and manage custom practice tests | Monitor statistics and manage leaderboards for justified administrative reasons |
| | Earn badges, manage profile information, and report content or platform problems | Use student features for validation and practice; administrator attempts do not count toward leaderboards and administrators cannot earn student badges |

## Entities Model

The following diagram describes the project domain model; it is not a statement that all entities and features are implemented. See the project documentation for detailed rules and proposed implementation conventions.

```mermaid
classDiagram
    class USER {
        +int user_id PK
        +string email UK
        +string password_hash
        +string username UK
        +string full_name
        +string role
        +datetime created_at
        +datetime updated_at
    }

    class SUBJECT {
        +int subject_id PK
        +string name
        +string description
        +datetime created_at
        +datetime updated_at
    }

    class TEST {
        +int test_id PK
        +int subject_id FK
        +int author_id FK
        +string title
        +string description
        +string test_type
        +string status
        +int year
        +string difficulty
        +int max_score
        +string tags
        +boolean is_public
        +datetime published_at
        +datetime created_at
        +datetime updated_at
    }

    class QUESTION {
        +int question_id PK
        +int test_id FK
        +text question_text
        +string question_type
        +int order_index
        +int score
        +datetime created_at
        +datetime updated_at
    }

    class ANSWER_OPTION {
        +int option_id PK
        +int question_id FK
        +text option_text
        +boolean is_correct
        +int order_index
    }

    class TEST_ATTEMPT {
        +int attempt_id PK
        +int test_id FK
        +int user_id FK
        +string attempt_type
        +string status
        +int score
        +int correct_count
        +int incorrect_count
        +int unanswered_count
        +boolean counts_for_leaderboard
        +datetime started_at
        +datetime submitted_at
    }

    class ANSWER_ATTEMPT {
        +int ANSWER_ATTEMPT_id PK
        +int attempt_id FK
        +int question_id FK
        +int selected_option_id FK
        +boolean is_correct
        +decimal awarded_score
        +datetime answered_at
    }

    class LEADERBOARD {
        +int leaderboard_id PK
        +int user_id FK
        +int test_id FK
        +int subject_id FK
        +string leaderboard_type
        +int score
        +int rank
        +datetime calculated_at
    }

    class BADGE {
        +int badge_id PK
        +string name UK
        +string description
        +string icon_url
        +string requirement_type
        +int requirement_value
        +boolean is_active
        +datetime created_at
    }

    class USER_BADGE {
        +int user_badge_id PK
        +int user_id FK
        +int badge_id FK
        +datetime earned_at
        +boolean is_visible
    }

    class REPORT {
        +int report_id PK
        +int reporter_id FK
        +int test_id FK
        +int question_id FK
        +string reason
        +text description
        +string status
        +int resolved_by FK
        +datetime created_at
        +datetime resolved_at
    }

    USER "1" -- "0..*" TEST : authors
    USER "1" -- "0..*" TEST_ATTEMPT : takes
    USER "1" -- "0..*" ANSWER_ATTEMPT : answers
    USER "1" -- "0..*" LEADERBOARD : ranks_in
    USER "1" -- "0..*" USER_BADGE : earns
    USER "1" -- "0..*" REPORT : submits
    USER "1" -- "0..*" REPORT : resolves

    SUBJECT "1" -- "0..*" TEST : contains
    SUBJECT "1" -- "1" LEADERBOARD : groups

    TEST "1" --> "0..*" QUESTION : contains
    TEST "1" -- "0..*" TEST_ATTEMPT : is_taken_in
    TEST "1" -- "0..1" LEADERBOARD : ranks
    TEST "1" -- "0..*" REPORT : is_reported

    QUESTION "1" --> "0..*" ANSWER_OPTION : has
    QUESTION "1" -- "0..*" ANSWER_ATTEMPT : is_answered_in
    QUESTION "1" -- "0..*" REPORT : is_reported

    ANSWER_OPTION "1" -- "0..*" ANSWER_ATTEMPT : selected_as

    TEST_ATTEMPT "1" -- "0..*" ANSWER_ATTEMPT : contains

    BADGE "1" -- "0..*" USER_BADGE : awarded_as
```

## AI-Assisted Spec-Driven Development (SDD)

The API is developed using **AI-Assisted Spec-Driven Development (SDD)** with **Spring Data REST** and **Cucumber BDD**.

It is pre-configured with agent skills (`skills/`), repository instructions (`AGENTS.md`, `CONVENTIONS.md`), and Aider configuration (`.aider.conf.yml`) to support both:
1. **Full-Capability AI Agents** (OpenCode, Claude Code, Cursor, GitHub Copilot, Windsurf).
2. **Token-Constrained APIs** (Aider paired with Groq free tier - 8K token limits).

For complete stage-by-stage instructions and command workflows, see **[AI_DEVELOPMENT_GUIDE.md](AI_DEVELOPMENT_GUIDE.md)**.

### SDD Agent Skills (`skills/`)
- **`skills/sdd-feature-designer/SKILL.md`**: Draft Cucumber `.feature` specs for Spring Data REST endpoints (<50 lines).
- **`skills/sdd-cucumber-steps/SKILL.md`**: Implement MockMvc step definition classes extending `StepDefs.java`.
- **`skills/spring-datarest-entity/SKILL.md`**: Create JPA `@Entity` classes extending `UriEntity` and `@RepositoryRestResource` interfaces with SpEL row-level security queries.
- **`skills/spring-datarest-handler/SKILL.md`**: Create lifecycle event handlers based on the `@RepositoryEventHandler`.
- **`skills/sdd-bdd-verifier/SKILL.md`**: Run `mvn test -Dtest=CucumberTest` and analyze test failure summaries.
