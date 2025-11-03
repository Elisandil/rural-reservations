# Rural Accommodation Management System

REST API for managing rural accommodations and reservations, built with Spring Boot following a hexagonal 
architecture (ports and adapters).

## Technologies

- **Java** (Spring Boot)
- **Maven** - Dependency management
- **PostgreSQL** - Database
- **H2** - Testing Database
- **Hexagonal Architecture** - Ports and adapters architecture

## Features

### Accommodation Management
- Create and update rural accommodations
- Search and check availability
- Manage accommodation types and capacity

### Booking Management
- Create reservations with availability validation
- Update and cancel reservations
- Handle date conflicts
- Verify capacity

### User Management
- **Clients**: User registration and management
- **Administrators**: System management

## Architecture

The project follows the principles of **Hexagonal Architecture**:

- **Domain**: Modelos, value objects, excepciones
- **Application**: Ports (interfaces) and services
- **Infrastructure**: Controllers, DTOs, configuration
 
## Project Status
Actively under development

## Author
Antonio Ortega

#### ⚠️ NOTE:
This document is subject to change. 
The information contained here may be updated or modified in future versions of the project.