# BankApp

## About

**BankApp** is a modern, modular, multi-layered banking application built with Java and inspired by Domain-Driven Design
principles. Initially started as a multitasking and learning experiment, the project has evolved into a comprehensive
showcase of best practices in back-end architecture and clean code.

**Current State:**

- The back-end is largely complete (outstanding tasks: securing endpoints, implementing caching).
- The front-end is functional but slated for a major upgrade (moving from Thymeleaf to a modern JavaScript framework).
- Collaboration and contributions are highly welcome!

---

## Project Details

- **Name:** BankApp
- **Purpose:** To demonstrate advanced software architecture, modular design, and portfolio-ready coding skills in a
  real-world banking application context.
- **Tech Stack:**
    - **Back-end:** Java, Spring, Hibernate, PostgreSQL, Redis, JUnit, Maven
    - **Dev Tools / Infrastructure:** Git, Docker, AWS EC2/RDS, DockerHub, Linux
- **Live Demo:** [bankapp.mackiewicz.info](http://bankapp.mackiewicz.info)
- **Repository:** [github.com/Pawel-Mackiewicz/BankApp](https://github.com/Pawel-Mackiewicz/BankApp.git)

---

## Architecture Overview

BankApp is structured using a **modular, layered architecture** inspired by Domain-Driven Design (DDD):

- **core/** — Business domain modules (e.g., accounts, transactions, users), each with dedicated models, services,
  repositories, exceptions, and validation logic.
- **system/** — Cross-cutting and system-wide functionalities such as error handling, registration and password
  recovery, email notification, transaction processing, and token management.
- **presentation/** — Application UI controllers (currently Thymeleaf-based).
- **shared/** — Common components, such as annotations, configuration, utilities, shared services, and validation
  mechanisms, used across core and system modules.

This structure improves maintainability, testability, and scalability. It also prepares the codebase for potential
future migration to microservices by ensuring strong separation of concerns.

---

```bash
# BankApp Project Structure

BankApp/
├── src/                                          # Source code directory
│   ├── main/                                     # Main application code
│   │   ├── java/                                 # Java source files
│   │   │   └── info/
│   │   │       └── mackiewicz/
│   │   │           └── bankapp/                  
│   │   │               ├── core/                 # Core business domain modules
│   │   │               │   ├── account/          # Account domain module
│   │   │               │   │   ├── exception/    # Account-specific exceptions
│   │   │               │   │   ├── model/        # Account entity models and DTOs
│   │   │               │   │   ├── repository/   # JPA repositories for accounts
│   │   │               │   │   ├── service/      # Account business services
│   │   │               │   │   ├── util/         # Account utility classes
│   │   │               │   │   └── validation/   # Account validation logic
│   │   │               │   │ 
│   │   │               │   ├── transaction/      # Transaction domain module
│   │   │               │   │   ├── exception/    # Transaction-specific exceptions
│   │   │               │   │   ├── model/        # Transaction entity models
│   │   │               │   │   ├── repository/   # JPA repositories for transactions
│   │   │               │   │   ├── service/      # Transaction business services
│   │   │               │   │   └── validation/   # Transaction validation logic
│   │   │               │   │ 
│   │   │               │   └── user/             # User domain module
│   │   │               │       ├── exception/    # User-specific exceptions
│   │   │               │       ├── model/        # User entity models and DTOs
│   │   │               │       ├── repository/   # JPA repositories for users
│   │   │               │       └── service/      # User business services
│   │   │               │   
│   │   │               ├── presentation/         # Presentation layer for user interfaces
│   │   │               │   ├── auth/             # Authentication and authorization features
│   │   │               │   │   ├── recovery/     # Account recovery functionalities
│   │   │               │   │   │   └── password/ # Password reset and recovery
│   │   │               │   │   │       ├── controller/ # REST controllers for password recovery
│   │   │               │   │   │       ├── exception/  # Password recovery specific exceptions
│   │   │               │   │   │       └── service/    # Services implementing password recovery logic
│   │   │               │   │   │
│   │   │               │   │   └── registration/   # User registration functionality
│   │   │               │   │       ├── controller/ # Controllers handling registration requests
│   │   │               │   │       ├── dto/        # Data transfer objects for registration
│   │   │               │   │       ├── exception/  # Registration-specific exceptions
│   │   │               │   │       └── service/    # Services implementing registration logic
│   │   │               │   │
│   │   │               │   ├── dashboard/          # User dashboard functionality
│   │   │               │   │   ├── main/           # Main dashboard views
│   │   │               │   │   │   ├── controller/ # Controllers for main dashboard
│   │   │               │   │   │   └── service/    # Services supporting main dashboard
│   │   │               │   │   │
│   │   │               │   │   └── settings/       # User settings management
│   │   │               │   │       ├── controller/ # REST controllers for settings management
│   │   │               │   │       ├── dto/        # DTOs for user settings (password change, etc.)
│   │   │               │   │       ├── exception/  # Settings-specific exceptions
│   │   │               │   │       └── service/    # Services implementing settings management
│   │   │               │   │
│   │   │               │   └── landing/          # Landing page for non-authenticated users
│   │   │               │   
│   │   │               ├── shared/               # Shared components used across modules
│   │   │               │   ├── annotations/      # Custom annotations
│   │   │               │   ├── config/           # Common configuration classes
│   │   │               │   ├── exception/        # Common exception types
│   │   │               │   ├── service/          # Shared services
│   │   │               │   ├── util/             # Utility classes
│   │   │               │   └── validation/       # Common validation mechanisms
│   │   │               │   
│   │   │               ├── system/               # System-wide functionalities
│   │   │               │   ├── banking/          # Banking system operations
│   │   │               │   │   ├── history/      # Transaction history functionality
│   │   │               │   │   │   ├── controller/   # History REST controllers
│   │   │               │   │   │   ├── exception/    # History-specific exceptions
│   │   │               │   │   │   └── service/      # History services
│   │   │               │   │   │ 
│   │   │               │   │   ├── operations/   # Banking operations (transfers, etc.)
│   │   │               │   │   │   ├── controller/   # Operations REST controllers
│   │   │               │   │   │   └── service/      # Operations services
│   │   │               │   │   │ 
│   │   │               │   │   └── shared/       # Shared code for banking subsystems
│   │   │               │   │
│   │   │               │   ├── error/            # Application error handling
│   │   │               │   │   └── handling/     # Global error handling functionality
│   │   │               │   │       ├── core/     # Core error handling mechanisms
│   │   │               │   │       ├── dto/      # Error response DTOs
│   │   │               │   │       ├── logger/   # Error logging components
│   │   │               │   │       ├── mapping/  # Exception to response mapping
│   │   │               │   │       ├── service/  # Error handling services
│   │   │               │   │       └── util/     # Error handling utilities
│   │   │               │   │
│   │   │               │   ├── notification/     # Notification subsystem
│   │   │               │   │   └── email/        # Email notification service
│   │   │               │   │       ├── exception/    # Email-specific exceptions
│   │   │               │   │       └── template/     # Email templates
│   │   │               │   │
│   │   │               │   ├── shared/           # Shared code for system modules
│   │   │               │   │
│   │   │               │   ├── token/            # Token management system
│   │   │               │   │   ├── model/        # Token entity models
│   │   │               │   │   ├── repository/   # Token repositories
│   │   │               │   │   └── service/      # Token management services
│   │   │               │   │
│   │   │               │   └── transaction/      # Transaction processing system
│   │   │               │       └── processing/   # Transaction execution engine
│   │   │               │           ├── core/     # Core processing components
│   │   │               │           ├── error/    # Error handling for transactions
│   │   │               │           ├── helpers/  # Helper classes for processing
│   │   │               │           └── locking/  # Concurrency control mechanisms
│   │   │               │
│   │   │               └── BankAppApplication.java  # Main Spring Boot application class
│   │   │
│   │   └── resources/                            # Application resources
│   │       ├── db/                               # Database related resources
│   │       │   └── migration/                    # Flyway database migrations
│   │       ├── application.yaml                  # Main application configuration
│   │       └── logback-spring.xml                # Logging configuration
│   │   
│   └── test/                                     # Test code
│       ├── java/                                
│       │   └── info/
│       │       └── mackiewicz/
│       │           └── bankapp/                  # Test packages mirror main structure
│       │               ├── core/                 # Tests for core domain layer
│       │               ├── integration/          # Integration tests between components
│       │               └── presentation/         # Tests for presentation layer 
│       │               ├── system/               # Tests for system functionalities
│       │               ├── testutils/            # Testing utilities and helpers
│       │     
│       └── resources/                            # Test resources
│           └── application-test.yaml             # Test-specific configuration
│  
├── .github/                                      # GitHub configuration
│   └── workflows/                                # GitHub Actions CI/CD workflows
│       ├── ci.yml                                # Continuous Integration workflow
│       └── ec2-deploy.yml                        # AWS EC2 deployment workflow
│   
├── wiki/                                         # Project documentation
├── Dockerfile                                    # Docker configuration for containerization
├── .env.example                                  # Example environment variables template
├── pom.xml                                       # Maven project configuration
├── README.md                                     # Project overview documentation
└── directory-tree.md                             # This file - project structure documentation
```

### How to run locally (for testing purposes)

If you prefer to run the application in a Docker container,
you can use the provided Dockerfile.

Make sure to have Docker installed and running on your machine.

Edit, the `docker-compose.yaml` file to set your own `RESEND_API_KEY`,
then run the following command in the root directory of the project:

```bash
docker compose up
```

This will build the whole setup for you, including the database and application.

**The application will be available at: `http://localhost:8080`**

### How to run locally (For BE Developers)

##### Requirements

- Java 21 or newer
- Maven
- Docker
- Git
- Resend.com account for email delivery

#### Steps

1. **Fork or clone repo**

2. **DB Config**
    - Install docker
    - Pull db image from dockerhub and run it
    ```bash
    # pull image from docker hub
    docker pull bankappproject/postgres_db:clean
    # run image in detached mode and map 5432 port
    docker run -p 5432:5432 -d bankappproject/postgres_db:clean
    ```
    - credentials:
        - user: bankapp_user
        - pass: bankapp_pass


3. **Configure resend.com** (if you skip this, your local app won't be able to send emails.)
    - Fill in the `.env` file with your credentials, use `.env.example`:
   ```dotenv
    # This is an example of a .env file for a Spring Boot application.
    # Application configuration
    PORT=8080   # port at which the application will run                        (default is 8080)
    SPRING_PROFILES_ACTIVE=dev  # active profile for the application (dev/prod) (default is dev)
    BANK_PASSWORD=bank_pass # password for The Bank account
    
    # Database configuration
    DB_URL=localhost    #your_database_url_here     (default is localhost)
    DB_PORT=5432        #your_database_port_here    (default is 5432 for PostgreSQL)
    DB_SCHEMA=bankapp   #your_database_schema_here  (default is bankapp)
    DB_USERNAME=bankapp_user
    DB_PASSWORD=bankapp_pass
    
    # Redis configuration
    REDIS_HOST=localhost
    REDIS_PORT=6379
    
    # Spring Security credentials
    SPRING_SECURITY_USER_NAME=admin
    SPRING_SECURITY_USER_PASSWORD=your_admin_str0nG_password
    
    # Resend email configuration
    RESEND_API_KEY: re_... # generate by your own at https://resend.com/ :)
    APP_BASE_URL=http://localhost:8080 # leave as is for local development, if you deploy it on server - put your domain here

    ```

3. **Build and Run**

   `cd` to your bankapp repo and:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

**The application will be available at: `http://localhost:8080`**

---

### Key Features

#### Transaction Processing System

BankApp's Transaction System provides a robust foundation for secure financial operations between bank accounts. The
system handles deposits, withdrawals, and various types of transfers with strong focus on data consistency and error
handling.

- **Multi-layered Architecture**: Clean separation of controller, service, and execution layers
- **Security-First Design**: Robust account locking mechanism to prevent race conditions
- **Asynchronous Processing**: Asynchronous transaction execution for improved throughput
- **Comprehensive Error Handling**: Centralized error management with appropriate recovery strategies
- **Automatic Batch Processing**: Scheduled tasks for processing new transactions every 10 minutes

For detailed technical documentation, see the [Transaction System](../../wiki/Transaction-System) page in the wiki.

#### Transaction History System

BankApp implements a comprehensive transaction history system that allows users to view, filter, sort, and export
history of financial operations on their bank accounts in a secure and efficient manner.

- **Multi-layered Architecture**: Clean separation between controller, service, and data presentation layers
- **Advanced Filtering**: Robust filtering by date, amount, transaction type, and text search
- **Dynamic Sorting**: Flexible sorting options with toggling direction capabilities
- **Data Export**: Support for exporting transaction history in multiple formats (CSV, PDF)
- **Security-First Design**: Strict account ownership verification and authenticated access

For detailed technical documentation, see the [Transaction History System](../../wiki/Transaction-History-System) page
in the wiki.

#### Banking Operations System

BankApp implements a comprehensive banking operations system that enables secure money transfers between accounts using
different identification methods. The system provides flexible transfer options while maintaining strict security
standards.

- **Multi-layered Architecture**: API layer, specialized services, and security components
- **Multiple Transfer Methods**: Support for IBAN-based and email-based transfers
- **Transaction Building**: Coordinated transaction creation with proper type assignment
- **Security Features**: Strict account ownership verification and secure processing
- **Comprehensive Validation**: Input validation, amount verification, and access control

For detailed technical documentation, see the [Banking Operations System](../../wiki/Banking-Operations-System) page in
the wiki.

#### Registration System

BankApp implements a comprehensive user registration system that ensures secure account creation with automatic bank
account setup and welcome bonus processing.

- **Multi-layered Architecture**: Clean separation of web, service, and validation layers
- **Comprehensive Validation**: Extensive validation of personal data, contact info, and security requirements
- **Automatic Account Setup**: Automated bank account creation and welcome bonus processing
- **Security-First Design**: Built-in protection against common vulnerabilities and data breaches
- **User-Friendly Experience**: Immediate feedback and clear error messaging
-

For detailed technical documentation, see the [Registration System](../../wiki/Registration-System) page in the wiki.

#### Password Reset System

BankApp implements a secure password reset system with the following features:

- **Three-tier architecture**: Web controller, REST API, and service layer
- **Token-based security**: Time-limited tokens for password reset operations
- **Security measures**: Rate limiting, information hiding, and secure error handling
- **Email notifications**: Automatic notifications at each stage of the process
- **Transactional processing**: Ensures data consistency during password changes

For detailed technical documentation, see the [Password Reset System](../../wiki/Password-Reset-System) page in the
wiki.

#### Email Notification System

BankApp includes a flexible email notification system that handles various types of user communication. The system
utilizes a multi-layered architecture and ensures reliable message delivery.

- **Multi-layered Architecture**: Separation of service layers, templates, and delivery
- **Template System**: Consistent formatting and responsive design for all emails
- **Flexible Integration**: Full integration with registration and password reset processes
- **Error Handling**: Comprehensive error handling with appropriate logging
- **Resend API**: Reliable email delivery through Resend API

For detailed technical documentation, see the [Email Notification System](../../wiki/Email-Notification-System) page in
the wiki.

#### Token System

BankApp implements a secure and robust token system primarily used for password reset functionality, ensuring secure
handling of sensitive operations through time-limited, single-use tokens.

- **Multi-layered Architecture**: Service layer for token generation, validation, and lifecycle management
- **Security Features**: SHA-256 hashing, rate limiting, and automatic token expiration
- **Token Management**: Single-use tokens with built-in expiration and cleanup mechanisms
- **Error Handling**: Comprehensive exception handling with proper security measures
- **Database Integration**: Efficient token storage with automated cleanup of expired tokens

For detailed technical documentation, see the [Token System](../../wiki/Token-System) page in the wiki.

#### Error Handling System

BankApp implements a comprehensive error handling system that ensures consistent error management, logging, and
standardized API responses across the entire application.

- **Multi-layered Architecture**: Global exception handler, logging system, and validation processors
- **Standardized Responses**: Unified error response format with consistent HTTP status mapping
- **Validation Framework**: Specialized handling for DTO and parameter validation errors
- **Centralized Logging**: Differentiated logging levels with configurable stack trace handling
- **Error Classification**: Structured error codes system with proper categorization

For detailed technical documentation, see the [Error Handling System](../../wiki/Error-Handling-System) page in the
wiki.

#### API Documentation System

BankApp implements a comprehensive API documentation system based on OpenAPI (Swagger) specification. The system
provides interactive and up-to-date documentation for all API endpoints, supporting development and integration.

- **OpenAPI 3.0 Standard**: Implementation of the industry-standard specification for API documentation
- **Interactive Documentation**: Swagger UI integration for testing and exploring API endpoints
- **Code-First Approach**: Documentation automatically synchronized with codebase
- **Detailed Examples**: Request and response examples for all endpoints
- **Security Integration**: Authentication requirements documented for each endpoint

##### Accessing Documentation

The API documentation is available at the following URLs when the application is running:

- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI Specification**: `/v3/api-docs`
- **YAML format**: `/v3/api-docs.yaml`

For detailed technical documentation, see the [API Documentation System](../../wiki/API-Documentation-System) page in
the wiki.

## Join the Project & Contribute

**Got ideas or want to help out? You're more than welcome here!**  
Whether you code, test, design, or just want to try working on a real project with a team—feel free to join in. Just
open an issue, send a pull request, or drop a message!

Not sure where to start or what you could do? **Check below**—there’s a bunch of areas where you can jump in and make a
difference!

### What Needs to Be Done?

#### Frontend

- **New UI/UX** – the project needs a fresh look and improved optimization (web + mobile).
- **Modern frontend tech** – currently it's Thymeleaf, but I'd love to switch to something new (React, Angular,
  Svelte—whatever you like!).
- **Mobile-first version** – fully responsive, working great on phones.
- **Your ideas** – if you have a cool idea for a new feature, let’s discuss and give it a try!

The actual scope depends on how many people get involved and how fast we progress. Every helping hand and fresh
perspective is more than welcome!

#### Backend

- **Optimizations** – the backend works, but there’s always room for improvements (performance, code cleanliness,
  tests).
- **Caching & rate limiting** – I want to implement effective caching and request limiting on the most important
  endpoints.
- **New features** – open for discussion, if you have ideas!
- **Security** – any advice and support in hardening the application are super valuable.

#### Beyond coding

- **Testing, pentesting** – if you love to hunt bugs or want to practice securing applications, there’s plenty to do.
- **Marketing/promotion** – want to practice promoting an IT project in your portfolio? Jump in!
- **Anyone who wants to learn or check out teamwork in practice** – you are welcome!

---

### Who are We looking for?

- **UI/UX Designers** – if you enjoy crafting modern, user-friendly interfaces, I’m looking for you!
- **Frontend Developers** – if you’re comfortable with React, Angular, Svelte, or any modern framework, and you’re up to
  working with APIs, hop on board!
- **Backend Developers (Java)** – our backend is Java-based, but any help with optimization or adding features is
  appreciated.
- **Testers & Pentesters** – another pair of eyes to catch bugs is always valued.
- **Marketing & Promotion** – if you want to try IT project promotion, help us get it out to the world!
- **Senior/Experienced Java developers** – if you like architecture, patterns, mentoring, or security, your advice would
  be great.
- **All positive enthusiasts** – if you simply want to try something new and build a real project in a friendly team,
  this is the place!

---

### What do We offer?

- 100% remote work—when and as much as you want.
- Experience working on a real app (not just tutorials).
- A friendly, stress-free atmosphere—we learn and help each other.
- A project you can confidently add to your portfolio and describe in your CV or LinkedIn.

---

### Want to join?

Just come and join Our discord community server!
Grab an invite [here](https://discord.gg/9YVuAbPmQc)

Thanks for stopping by—see you soon!

Pawel
