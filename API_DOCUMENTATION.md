# ARVoice API Documentation

## Overview
This document provides comprehensive API documentation for the ARVoice application backend. The API follows RESTful principles and uses JSON for data exchange.

## Base Configuration

### Base URL
```
http://13.201.184.185/api/
```

### Authentication
All API endpoints (except login) require JWT authentication:
```
Authorization: Bearer <jwt_token>
```

### Content Type
```
Content-Type: application/json
```

### Response Format
All API responses follow this structure:
```json
{
  "success": true,
  "data": {},
  "message": "Success message",
  "error": null
}
```

## API Endpoints

### 1. Authentication

#### Login
**Endpoint:** `POST /auth/login`

**Description:** Authenticate user and receive JWT token

**Request Body:**
```json
{
  "username": "user@example.com",
  "password": "password123",
  "fcmToken": "firebase_messaging_token"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userName": "John Doe",
    "role": "sales_executive",
    "userId": "12345",
    "email": "user@example.com"
  },
  "message": "Login successful",
  "error": null
}
```

**Error Response:**
```json
{
  "success": false,
  "data": null,
  "message": "Authentication failed",
  "error": "Invalid credentials"
}
```

### 2. User Management

#### Get Current User
**Endpoint:** `GET /getCurrentUser`

**Description:** Get current logged-in user information

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "12345",
    "userName": "John Doe",
    "email": "user@example.com",
    "role": "sales_executive",
    "department": "Sales",
    "phoneNumber": "+1234567890",
    "isActive": true,
    "lastLogin": "2024-01-15T10:30:00Z"
  },
  "message": "User data retrieved successfully",
  "error": null
}
```

#### Get All Users
**Endpoint:** `GET /getUsers`

**Description:** Get list of all users in the system

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "userId": "12345",
      "userName": "John Doe",
      "email": "john@example.com",
      "role": "sales_executive",
      "department": "Sales",
      "isActive": true
    },
    {
      "userId": "12346",
      "userName": "Jane Smith",
      "email": "jane@example.com",
      "role": "manager",
      "department": "Sales",
      "isActive": true
    }
  ],
  "message": "Users retrieved successfully",
  "error": null
}
```

### 3. Lead Management

#### Get Leads (Advanced Search)
**Endpoint:** `GET /advanced-search`

**Description:** Search and filter leads with advanced criteria

**Query Parameters:**
- `page`: Page number (default: 1)
- `limit`: Number of results per page (default: 20)
- `status`: Lead status filter
- `assignedTo`: User ID filter
- `dateFrom`: Start date filter (YYYY-MM-DD)
- `dateTo`: End date filter (YYYY-MM-DD)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "leadId": "lead_001",
        "contactName": "Alice Johnson",
        "email": "alice@example.com",
        "phoneNumber": "+1234567890",
        "company": "ABC Corp",
        "status": "new",
        "assignedTo": "12345",
        "assignedToName": "John Doe",
        "source": "website",
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z",
        "customFields": {
          "industry": "Technology",
          "budget": "$50,000",
          "timeline": "Q2 2024"
        }
      }
    ],
    "totalElements": 150,
    "totalPages": 8,
    "currentPage": 1,
    "pageSize": 20
  },
  "message": "Leads retrieved successfully",
  "error": null
}
```

#### Add Lead
**Endpoint:** `POST /add`

**Description:** Create a new lead

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "contactName": "Alice Johnson",
  "email": "alice@example.com",
  "phoneNumber": "+1234567890",
  "company": "ABC Corp",
  "status": "new",
  "source": "website",
  "assignedTo": "12345",
  "customFields": {
    "industry": "Technology",
    "budget": "$50,000",
    "timeline": "Q2 2024"
  },
  "notes": "Initial contact from website inquiry"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "leadId": "lead_001",
    "contactName": "Alice Johnson",
    "email": "alice@example.com",
    "phoneNumber": "+1234567890",
    "company": "ABC Corp",
    "status": "new",
    "assignedTo": "12345",
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "message": "Lead created successfully",
  "error": null
}
```

#### Update Lead
**Endpoint:** `PUT /add`

**Description:** Update an existing lead

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "leadId": "lead_001",
  "contactName": "Alice Johnson",
  "email": "alice.johnson@example.com",
  "phoneNumber": "+1234567890",
  "company": "ABC Corp",
  "status": "contacted",
  "assignedTo": "12345",
  "customFields": {
    "industry": "Technology",
    "budget": "$75,000",
    "timeline": "Q1 2024"
  },
  "notes": "Updated budget after second meeting"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "leadId": "lead_001",
    "contactName": "Alice Johnson",
    "email": "alice.johnson@example.com",
    "status": "contacted",
    "updatedAt": "2024-01-15T14:30:00Z"
  },
  "message": "Lead updated successfully",
  "error": null
}
```

#### Get Lead Forms
**Endpoint:** `GET /forms/leads`

**Description:** Get dynamic form configuration for lead creation/editing

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "formFields": [
      {
        "fieldName": "contactName",
        "fieldType": "text",
        "label": "Contact Name",
        "required": true,
        "placeholder": "Enter contact name"
      },
      {
        "fieldName": "email",
        "fieldType": "email",
        "label": "Email Address",
        "required": true,
        "placeholder": "Enter email address"
      },
      {
        "fieldName": "phoneNumber",
        "fieldType": "tel",
        "label": "Phone Number",
        "required": true,
        "placeholder": "Enter phone number"
      },
      {
        "fieldName": "company",
        "fieldType": "text",
        "label": "Company",
        "required": false,
        "placeholder": "Enter company name"
      },
      {
        "fieldName": "status",
        "fieldType": "select",
        "label": "Status",
        "required": true,
        "options": [
          {"value": "new", "label": "New"},
          {"value": "contacted", "label": "Contacted"},
          {"value": "qualified", "label": "Qualified"},
          {"value": "proposal", "label": "Proposal Sent"},
          {"value": "won", "label": "Won"},
          {"value": "lost", "label": "Lost"}
        ]
      }
    ],
    "statusOptions": [
      {"value": "new", "label": "New", "color": "#007bff"},
      {"value": "contacted", "label": "Contacted", "color": "#17a2b8"},
      {"value": "qualified", "label": "Qualified", "color": "#ffc107"},
      {"value": "proposal", "label": "Proposal Sent", "color": "#fd7e14"},
      {"value": "won", "label": "Won", "color": "#28a745"},
      {"value": "lost", "label": "Lost", "color": "#dc3545"}
    ]
  },
  "message": "Lead form configuration retrieved successfully",
  "error": null
}
```

### 4. Task Management

#### Get Tasks
**Endpoint:** `GET /tasks`

**Description:** Get list of tasks with filtering options

**Query Parameters:**
- `page`: Page number (default: 1)
- `limit`: Number of results per page (default: 20)
- `status`: Task status filter
- `assignedTo`: User ID filter
- `leadId`: Lead ID filter
- `priority`: Priority filter (low, medium, high)
- `dueDate`: Due date filter (YYYY-MM-DD)

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "taskId": "task_001",
        "title": "Follow up call with Alice Johnson",
        "description": "Call to discuss proposal details",
        "status": "pending",
        "priority": "high",
        "assignedTo": "12345",
        "assignedToName": "John Doe",
        "leadId": "lead_001",
        "leadName": "Alice Johnson",
        "dueDate": "2024-01-20",
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z",
        "estimatedHours": 1,
        "actualHours": null,
        "notes": "Discuss pricing and timeline"
      }
    ],
    "totalElements": 45,
    "totalPages": 3,
    "currentPage": 1,
    "pageSize": 20
  },
  "message": "Tasks retrieved successfully",
  "error": null
}
```

#### Add Task
**Endpoint:** `POST /tasks`

**Description:** Create a new task

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Follow up call with Alice Johnson",
  "description": "Call to discuss proposal details",
  "status": "pending",
  "priority": "high",
  "assignedTo": "12345",
  "leadId": "lead_001",
  "dueDate": "2024-01-20",
  "estimatedHours": 1,
  "notes": "Discuss pricing and timeline"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "taskId": "task_001",
    "title": "Follow up call with Alice Johnson",
    "status": "pending",
    "priority": "high",
    "assignedTo": "12345",
    "leadId": "lead_001",
    "dueDate": "2024-01-20",
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "message": "Task created successfully",
  "error": null
}
```

#### Update Task
**Endpoint:** `PUT /tasks`

**Description:** Update an existing task

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "taskId": "task_001",
  "title": "Follow up call with Alice Johnson",
  "description": "Call completed - discussed proposal details",
  "status": "completed",
  "priority": "high",
  "assignedTo": "12345",
  "leadId": "lead_001",
  "dueDate": "2024-01-20",
  "estimatedHours": 1,
  "actualHours": 0.5,
  "notes": "Call completed successfully. Customer interested in Q1 timeline."
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "taskId": "task_001",
    "title": "Follow up call with Alice Johnson",
    "status": "completed",
    "actualHours": 0.5,
    "updatedAt": "2024-01-15T14:30:00Z"
  },
  "message": "Task updated successfully",
  "error": null
}
```

#### Get Task Transitions
**Endpoint:** `GET /tasks/taskTransition`

**Description:** Get available task status transitions

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "transitions": [
      {
        "from": "pending",
        "to": "in_progress",
        "label": "Start Task",
        "color": "#007bff"
      },
      {
        "from": "in_progress",
        "to": "completed",
        "label": "Complete Task",
        "color": "#28a745"
      },
      {
        "from": "pending",
        "to": "cancelled",
        "label": "Cancel Task",
        "color": "#dc3545"
      }
    ],
    "statuses": [
      {"value": "pending", "label": "Pending", "color": "#ffc107"},
      {"value": "in_progress", "label": "In Progress", "color": "#007bff"},
      {"value": "completed", "label": "Completed", "color": "#28a745"},
      {"value": "cancelled", "label": "Cancelled", "color": "#dc3545"}
    ]
  },
  "message": "Task transitions retrieved successfully",
  "error": null
}
```

### 5. Dashboard and Analytics

#### Get Leads Summary
**Endpoint:** `GET /leadsSummary/getAll`

**Description:** Get summary statistics for leads

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "totalLeads": 150,
    "newLeads": 25,
    "contactedLeads": 45,
    "qualifiedLeads": 30,
    "proposalsSent": 20,
    "wonLeads": 15,
    "lostLeads": 15,
    "conversionRate": 10.0,
    "averageDealValue": 45000,
    "monthlyGrowth": 15.5,
    "leadsThisMonth": 35,
    "leadsLastMonth": 30,
    "statusDistribution": [
      {"status": "new", "count": 25, "percentage": 16.7},
      {"status": "contacted", "count": 45, "percentage": 30.0},
      {"status": "qualified", "count": 30, "percentage": 20.0},
      {"status": "proposal", "count": 20, "percentage": 13.3},
      {"status": "won", "count": 15, "percentage": 10.0},
      {"status": "lost", "count": 15, "percentage": 10.0}
    ]
  },
  "message": "Leads summary retrieved successfully",
  "error": null
}
```

#### Get Advanced Dashboard Summary
**Endpoint:** `GET /dashboard/advancedSummary`

**Description:** Get comprehensive dashboard analytics

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "leadsMetrics": {
      "totalLeads": 150,
      "newLeads": 25,
      "conversionRate": 10.0,
      "averageDealValue": 45000
    },
    "tasksMetrics": {
      "totalTasks": 45,
      "pendingTasks": 20,
      "completedTasks": 20,
      "overdueTasks": 5,
      "completionRate": 44.4
    },
    "callsMetrics": {
      "totalCalls": 120,
      "outboundCalls": 80,
      "inboundCalls": 40,
      "averageCallDuration": 300,
      "callsToday": 8
    },
    "performanceMetrics": {
      "targetAchievement": 85.5,
      "monthlyRevenue": 125000,
      "quarterlyRevenue": 350000,
      "topPerformers": [
        {"userId": "12345", "userName": "John Doe", "leadsConverted": 8},
        {"userId": "12346", "userName": "Jane Smith", "leadsConverted": 6}
      ]
    },
    "recentActivity": [
      {
        "type": "lead_created",
        "description": "New lead created: Alice Johnson",
        "timestamp": "2024-01-15T10:30:00Z",
        "userId": "12345"
      },
      {
        "type": "task_completed",
        "description": "Task completed: Follow up call",
        "timestamp": "2024-01-15T09:45:00Z",
        "userId": "12346"
      }
    ]
  },
  "message": "Advanced dashboard summary retrieved successfully",
  "error": null
}
```

### 6. Settings and Configuration

#### Get Global Settings
**Endpoint:** `GET /global-settings/{userId}`

**Description:** Get global application settings for a user

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "12345",
    "settings": {
      "notifications": {
        "email": true,
        "push": true,
        "sms": false
      },
      "dashboard": {
        "defaultView": "summary",
        "refreshInterval": 300,
        "showMetrics": true
      },
      "leads": {
        "autoAssign": true,
        "defaultStatus": "new",
        "requireApproval": false
      },
      "tasks": {
        "autoReminder": true,
        "reminderInterval": 1440,
        "defaultPriority": "medium"
      },
      "calls": {
        "autoLog": true,
        "recordCalls": false,
        "defaultSim": 1
      }
    }
  },
  "message": "Settings retrieved successfully",
  "error": null
}
```

#### Get Mobile Settings
**Endpoint:** `GET /mobile-settings`

**Description:** Get mobile-specific application settings

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "appSettings": {
      "version": "1.0.0",
      "forceUpdate": false,
      "maintenanceMode": false,
      "features": {
        "callRecording": true,
        "voiceNotes": true,
        "offlineMode": true,
        "darkMode": true
      }
    },
    "syncSettings": {
      "syncInterval": 3600,
      "autoSync": true,
      "wifiOnly": false
    },
    "securitySettings": {
      "sessionTimeout": 7200,
      "biometricAuth": true,
      "pinAuth": true
    }
  },
  "message": "Mobile settings retrieved successfully",
  "error": null
}
```

### 7. Templates and Forms

#### Get Templates
**Endpoint:** `GET /template/all`

**Description:** Get all available templates

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "templates": [
      {
        "templateId": "template_001",
        "name": "Lead Follow-up Email",
        "type": "email",
        "category": "lead_management",
        "subject": "Thank you for your interest in our services",
        "body": "Dear {{contactName}},\n\nThank you for your interest in our services. We would like to schedule a call to discuss your requirements.\n\nBest regards,\n{{userName}}",
        "variables": ["contactName", "userName", "companyName"],
        "isActive": true
      },
      {
        "templateId": "template_002",
        "name": "Task Reminder",
        "type": "notification",
        "category": "task_management",
        "subject": "Task Reminder: {{taskTitle}}",
        "body": "This is a reminder that your task '{{taskTitle}}' is due on {{dueDate}}.",
        "variables": ["taskTitle", "dueDate", "assignedTo"],
        "isActive": true
      }
    ]
  },
  "message": "Templates retrieved successfully",
  "error": null
}
```

### 8. Cars/Vehicles (If applicable)

#### Get Cars
**Endpoint:** `GET /cars`

**Description:** Get list of vehicles/cars

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "cars": [
      {
        "carId": "car_001",
        "make": "Toyota",
        "model": "Camry",
        "year": 2023,
        "licensePlate": "ABC123",
        "assignedTo": "12345",
        "assignedToName": "John Doe",
        "isActive": true,
        "mileage": 15000,
        "lastServiceDate": "2024-01-01"
      },
      {
        "carId": "car_002",
        "make": "Honda",
        "model": "Civic",
        "year": 2022,
        "licensePlate": "XYZ789",
        "assignedTo": "12346",
        "assignedToName": "Jane Smith",
        "isActive": true,
        "mileage": 22000,
        "lastServiceDate": "2023-12-15"
      }
    ]
  },
  "message": "Cars retrieved successfully",
  "error": null
}
```

## Error Handling

### Common Error Codes

#### 400 - Bad Request
```json
{
  "success": false,
  "data": null,
  "message": "Bad request",
  "error": "Invalid request format or missing required fields"
}
```

#### 401 - Unauthorized
```json
{
  "success": false,
  "data": null,
  "message": "Unauthorized access",
  "error": "Invalid or expired token"
}
```

#### 403 - Forbidden
```json
{
  "success": false,
  "data": null,
  "message": "Access denied",
  "error": "Insufficient permissions"
}
```

#### 404 - Not Found
```json
{
  "success": false,
  "data": null,
  "message": "Resource not found",
  "error": "The requested resource does not exist"
}
```

#### 500 - Internal Server Error
```json
{
  "success": false,
  "data": null,
  "message": "Internal server error",
  "error": "An unexpected error occurred on the server"
}
```

## Rate Limiting

The API implements rate limiting to prevent abuse:

- **Standard endpoints**: 100 requests per minute
- **Authentication endpoints**: 10 requests per minute
- **Bulk operations**: 20 requests per minute

Rate limit headers are included in responses:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1642617600
```

## SDK Integration Examples

### Android (Java) Example
```java
public class ApiClient {
    private static final String BASE_URL = "http://13.201.184.185/api/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor())
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit;
    }
}

// Usage
ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
Call<LoginResponse> call = apiInterface.login(loginRequest);
call.enqueue(new Callback<LoginResponse>() {
    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        if (response.isSuccessful()) {
            // Handle success
        }
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        // Handle failure
    }
});
```

### Authentication Interceptor
```java
public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        
        String token = getStoredToken(); // Get from SharedPreferences
        
        if (token != null) {
            Request authorized = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
            return chain.proceed(authorized);
        }
        
        return chain.proceed(original);
    }
}
```

## API Testing

### Postman Collection
A Postman collection is available for testing all API endpoints. Import the collection using the following structure:

```json
{
  "info": {
    "name": "ARVoice API Collection",
    "version": "1.0.0"
  },
  "auth": {
    "type": "bearer",
    "bearer": {
      "token": "{{jwt_token}}"
    }
  },
  "variable": [
    {
      "key": "base_url",
      "value": "http://13.201.184.185/api"
    },
    {
      "key": "jwt_token",
      "value": "your_jwt_token_here"
    }
  ]
}
```

### cURL Examples

#### Login
```bash
curl -X POST \
  http://13.201.184.185/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "user@example.com",
    "password": "password123",
    "fcmToken": "firebase_token"
  }'
```

#### Get Leads
```bash
curl -X GET \
  http://13.201.184.185/api/advanced-search?page=1&limit=20 \
  -H 'Authorization: Bearer your_jwt_token_here'
```

#### Create Lead
```bash
curl -X POST \
  http://13.201.184.185/api/add \
  -H 'Authorization: Bearer your_jwt_token_here' \
  -H 'Content-Type: application/json' \
  -d '{
    "contactName": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "+1234567890",
    "company": "Example Corp",
    "status": "new"
  }'
```

This comprehensive API documentation provides all the information needed to integrate with the ARVoice backend API, including request/response formats, authentication, error handling, and practical examples.