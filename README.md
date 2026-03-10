# Inventory Tracker

A full-stack Spring Boot application that helps internal teams manage stock levels, audit inventory movements, and surface reorder alerts through a responsive web interface.

**Live Demo:**  [View on Render](https://inventorytracker-jc.onrender.com/)  
*The demo uses seeded sample data to simulate real-world operations.*  
*Initial load may be delayed due to Render cold starts.*  

**Demo Credentials:**

The application requires authentication and role-based access to use the system.  
The following accounts are provided for demonstration purposes.

| Role  | Username | Password |
|------|------|------|
| User | user | user123 |
| Admin | admin | admin123 |

User access allows viewing the dashboard of active items and adding stock movements.  
Admin access allows adding and editing items along with viewing inactive items. 

## Screenshots

### Main Dashboard
![Main Dashboard](screenshots/main-dashboard.png)

### Item Details & Movement History
![Item Details](screenshots/item-details.png)

### Edit Inventory Item
![Item Form](screenshots/edit-item-form.png)

### Mobile Dashboard
<img src="screenshots/mobile-dashboard.png" width="300">

## Problem

Teams often rely on spreadsheets or informal processes to track inventory, making it difficult to maintain accurate stock counts and understand how inventory changes over time.  

Inventory Tracker centralizes inventory management, movement auditing, and reorder visibility into a single system designed to simplify everyday stock monitoring and operational decision-making.

## Key Features

- Inventory item management with stock tracking
- Movement history auditing to trace how inventory changes over time
- Low-stock alerts surfaced across dashboards, inventory lists, and item views
- Role-based access control with administrative permissions
- Paginated inventory views for efficient browsing of large datasets
- Responsive layouts optimized for both desktop and mobile use

## Architecture & Design Highlights

- Layered Spring Boot architecture separating controllers, services, and repositories
- DTO-based request and view models to isolate domain entities from web input and output
- Inventory quantities derived from movement history using JPQL aggregation queries
- Soft-delete strategy for inventory items to preserve historical movement data
- Business rule enforcement in the service layer using custom domain exceptions
- Server-side pagination for scalable inventory and movement history views
- Role-based access control for administrative routes using Spring Security matchers

## Tech Stack

- **Backend:** Java, Spring Boot  
- **Frontend:** Thymeleaf, Bootstrap  
- **Database:** PostgreSQL  
- **Deployment:** Docker, Render

## Running Locally

### Prerequisites
Ensure the following are installed:
- Java 17+
- Git

### 1. Clone the Repository
```bash
git clone https://github.com/jakeclara/inventory-tracker.git  
cd inventory-tracker
```

### 2. Run the Application
Run the application with the development profile (uses local H2 database):  
*The development profile automatically loads sample data on startup.*  

**Windows (PowerShell):**  
```bash
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=development"
```

**macOS / Linux:**  
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=development
```

### 3. Access the Application

Open your browser and navigate to:

http://localhost:8080

**Local Development Credentials:**

Credentials are created automatically by the development seed data.

| Role  | Username | Password |
|------|------|------|
| User | regular | regularpassword |
| Admin | admin | adminpassword |

## Testing

The project includes a comprehensive automated test suite covering service, repository, and controller layers.

- Unit tests validate service-layer business rules and domain logic
- Repository tests verify JPQL queries and data access behavior
- Controller tests exercise web-layer interactions
- ~100% service-layer coverage and 95%+ overall test coverage
- Tests run using a dedicated test configuration with an isolated database environment

### Run Tests

```bash
./mvnw test
```

## Future Improvements

- Search and filtering for inventory items to improve navigation in larger datasets
- Reporting features such as exportable inventory summaries and count sheets
- Bulk inventory adjustments for faster reconciliation during stock counts
- Audit logs for administrative actions and inventory changes
- Automated reorder notifications when items fall below threshold levels

## Author

**Jake Clara**  
Software Engineer  
- [LinkedIn](https://www.linkedin.com/in/jacobclara/)
- [GitHub](https://github.com/jakeclara)
