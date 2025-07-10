# ARVoice Android Application - Complete Documentation

## Table of Contents
1. [Overview](#overview)
2. [Technical Architecture](#technical-architecture)
3. [Project Structure](#project-structure)
4. [Key Terms and Concepts](#key-terms-and-concepts)
5. [Application Flow](#application-flow)
6. [Core Components](#core-components)
7. [Features and Functionality](#features-and-functionality)
8. [API Documentation](#api-documentation)
9. [Setup and Build Instructions](#setup-and-build-instructions)
10. [Code Examples](#code-examples)

---

## Overview

ARVoice is a comprehensive **CRM (Customer Relationship Management)** and **VoIP (Voice over Internet Protocol)** Android application designed for businesses to manage leads, contacts, tasks, and make phone calls efficiently. The application integrates with a backend API to provide real-time data synchronization and uses Firebase for push notifications.

### Key Features:
- **Lead Management**: Create, edit, and track potential customers
- **Task Management**: Assign and track tasks related to leads
- **Contact Management**: Store and manage contact information
- **Call Management**: Make calls with dual SIM support and call logging
- **Dashboard Analytics**: View business metrics and summaries
- **User Authentication**: Secure login system with JWT tokens
- **Push Notifications**: Real-time notifications via Firebase

---

## Technical Architecture

### Architecture Pattern
The application follows **MVC (Model-View-Controller)** architecture with **Fragment-based navigation**.

### Technology Stack
- **Language**: Java
- **Build System**: Gradle
- **UI Framework**: Android SDK with Material Design
- **Networking**: Retrofit + OkHttp
- **Data Binding**: ViewBinding
- **Local Storage**: SharedPreferences
- **Push Notifications**: Firebase Cloud Messaging (FCM)
- **Analytics**: Firebase Analytics
- **Image Loading**: Default Android ImageView
- **UI Components**: Material Design Components

### Target Specifications
- **Minimum SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Java Version**: 1.8
- **Gradle Version**: 8.5

---

## Project Structure

```
arvoice/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/arvoice/
│   │   │   │   ├── activity/          # Activity classes (14 files)
│   │   │   │   ├── adapter/           # RecyclerView adapters (11 files)
│   │   │   │   ├── base/              # Base classes (Application class)
│   │   │   │   ├── fcm/               # Firebase Cloud Messaging
│   │   │   │   ├── fragment/          # Fragment classes (4 files)
│   │   │   │   ├── model/             # Data models (17 files)
│   │   │   │   ├── networkinh/        # Network layer (API client)
│   │   │   │   ├── profile/           # Profile management (4 files)
│   │   │   │   └── utils/             # Utility classes (4 files)
│   │   │   ├── res/                   # Resources (layouts, drawables, etc.)
│   │   │   └── AndroidManifest.xml    # App configuration
│   │   ├── androidTest/               # Instrumentation tests
│   │   └── test/                      # Unit tests
│   ├── build.gradle                   # App-level build configuration
│   ├── proguard-rules.pro            # ProGuard configuration
│   └── google-services.json          # Firebase configuration
├── build.gradle                       # Project-level build configuration
├── settings.gradle                    # Project settings
└── gradle/                           # Gradle wrapper files
```

---

## Key Terms and Concepts

### 1. **CRM (Customer Relationship Management)**
A system for managing interactions with current and potential customers.

### 2. **Lead**
A potential customer who has shown interest in your product/service.

### 3. **Task**
An action item assigned to a user related to a lead or business process.

### 4. **Disposition**
The outcome or result of a call or interaction with a lead.

### 5. **FCM (Firebase Cloud Messaging)**
Google's service for sending push notifications to mobile devices.

### 6. **JWT (JSON Web Token)**
A secure way to transmit information between parties as a JSON object.

### 7. **Retrofit**
A type-safe HTTP client for Android and Java applications.

### 8. **ViewBinding**
A feature that allows you to more easily write code that interacts with views.

### 9. **SharedPreferences**
A simple storage mechanism for storing small amounts of data locally.

### 10. **Material Design**
Google's design system for creating beautiful, consistent user interfaces.

### 11. **Fragment**
A portion of user interface in an Activity that can be reused.

### 12. **RecyclerView**
A flexible view for providing a limited window into a large data set.

### 13. **Adapter**
A bridge between data source and the UI component (like RecyclerView).

### 14. **API (Application Programming Interface)**
A set of protocols for building and integrating application software.

### 15. **OkHttp**
An HTTP client for Android and Java applications.

### 16. **Gson**
A Java library for converting Java Objects into JSON and vice versa.

### 17. **ProGuard**
A tool for code obfuscation and optimization in Android apps.

### 18. **Manifest**
An XML file that contains essential information about your app.

### 19. **Activity**
A single, focused thing that the user can do in an Android app.

### 20. **Intent**
A messaging object used to request an action from another app component.

---

## Application Flow

### 1. **Application Startup**
```
App Launch → SplashActivity → Check Login Status → Navigate to Login/Dashboard
```

### 2. **Authentication Flow**
```
LoginActivity → Enter Credentials → API Call → Store Token → DashboardActivity
```

### 3. **Main Navigation Flow**
```
DashboardActivity → BottomNavigationView → Fragments:
├── DashboardFragment (Analytics & Summary)
├── LeadFragment (Lead Management)
├── DialFragment (Call Management)
└── SiteVisitingFragment (Site Visits)
```

### 4. **Lead Management Flow**
```
LeadFragment → View Leads → Add/Edit Lead → API Sync → Update UI
```

### 5. **Call Management Flow**
```
DialFragment → View Call Log → Make Call → Log Call → Sync with API
```

### 6. **Task Management Flow**
```
Dashboard → Tasks → Add/Edit Task → Assign to Lead → Track Progress
```

---

## Core Components

### 1. **Application Class (AppController)**
```java
public class AppController extends MultiDexApplication {
    // Singleton pattern for global app state
    // Handles MultiDex initialization
    // Provides global context access
}
```

### 2. **Activities (14 Total)**

#### **SplashActivity**
- Entry point of the application
- Handles notification permissions
- Checks login status
- Redirects to Login or Dashboard

#### **LoginActivity**
- User authentication
- Firebase token generation
- Credential validation
- Animated transitions

#### **DashboardActivity**
- Main navigation hub
- Bottom navigation management
- Fragment container
- Navigation drawer

#### **Lead Management Activities**
- `AddLeadsActivity`: Create new leads
- `EditLeadsActivity`: Modify existing leads
- `UpdateLeadsActivity`: Update lead information
- `SelectLeadActivity`: Choose leads for tasks

#### **Task Management Activities**
- `AddTasksActivity`: Create new tasks
- `EditTasksActivity`: Modify existing tasks
- `TransitionActivity`: Task status transitions

#### **Profile Management Activities**
- `UserDetailActivity`: User profile management
- `BlockListActivity`: Blocked contacts
- `SimCardManagerActivity`: SIM card settings
- `ManualSyncActivity`: Manual data synchronization

#### **Utility Activities**
- `ContactDetailsActivity`: Contact information
- `DispositionActivity`: Call outcomes
- `SelectUserActivity`: User selection

### 3. **Fragments (4 Total)**

#### **DashboardFragment**
- Business analytics dashboard
- Summary cards
- Quick action buttons
- Data visualization

#### **LeadFragment**
- Lead listing with search/filter
- Lead creation and editing
- Lead status management
- Pagination support

#### **DialFragment**
- Call log display
- Dialer interface
- Call history management
- Multi-SIM support

#### **SiteVisitingFragment**
- Site visit scheduling
- Visit tracking
- Location management

### 4. **Adapters (11 Total)**

#### **LeadListAdapter**
- Displays leads in RecyclerView
- Supports search and filtering
- Handle click events
- Data binding for lead items

#### **CallLogAdapter**
- Shows call history
- Date/time formatting
- Call duration display
- Call type icons

#### **TaskListAdapter**
- Task management interface
- Status indicators
- Priority levels
- Assignment information

#### **SummaryAdapter**
- Dashboard summary cards
- Metric display
- Color-coded indicators
- Click handling

#### **Other Adapters**
- `UserListAdapter`: User selection
- `CarListAdapter`: Vehicle management
- `TransitionAdapter`: Task transitions
- `CallDetailsAdapter`: Call information
- `BlockedContactAdapter`: Blocked contacts
- `LeadOptionAdapter`: Lead options
- `RoundButtonAdapter`: Dashboard buttons

### 5. **Models (17 Total)**

#### **Authentication Models**
- `LoginRequest`: Login credentials
- `LoginResponse`: Authentication response
- `CurrentUserModel`: Current user data
- `UserModel`: User information

#### **Lead Models**
- `LeadsListModel`: Lead list data
- `LeadCreate`: Lead creation data
- `LeadsFilter`: Search filters

#### **Task Models**
- `TaskListModel`: Task information
- `TransitionModel`: Task transitions

#### **Call Models**
- `CallLogModel`: Call history
- `CallLogItem`: Individual call data

#### **System Models**
- `CommonResponseModel`: Generic API response
- `SummaryModel`: Dashboard summary
- `SummaryItem`: Summary data items
- `UserSettingsModel`: User preferences
- `CarListModel`: Vehicle data
- `BlockedContact`: Blocked contact data
- `Field`: Dynamic form fields
- `Group`: Data grouping

### 6. **Network Layer**

#### **ApiClient**
- Retrofit configuration
- HTTP client setup
- Request/response interceptors
- Timeout configurations

#### **ApiInterface**
- API endpoint definitions
- HTTP method annotations
- Request/response models
- Authentication headers

#### **WebApi**
- Base URL configuration
- API constants
- Media type definitions

### 7. **Utility Classes**

#### **Constant**
- API endpoint constants
- Configuration values
- Static strings

#### **Utils**
- Common utility methods
- Helper functions
- Toast messages

#### **ProgressUtils**
- Loading dialog management
- Progress indicators

#### **NavigationDrawerHelper**
- Navigation drawer management
- Menu item handling

### 8. **Firebase Integration**

#### **MyFirebaseMessagingService**
- Push notification handling
- Token management
- Notification display
- Background processing

---

## Features and Functionality

### 1. **User Authentication**
- Secure login with email/password
- JWT token-based authentication
- Automatic token refresh
- Session management
- Firebase token integration

### 2. **Lead Management**
- Create, read, update, delete leads
- Lead status tracking
- Search and filter capabilities
- Lead assignment to users
- Lead history tracking

### 3. **Task Management**
- Task creation and assignment
- Task status transitions
- Priority levels
- Due date tracking
- Task completion tracking

### 4. **Call Management**
- Call logging
- Call history display
- Multi-SIM support
- Call duration tracking
- Contact integration

### 5. **Dashboard Analytics**
- Business metrics summary
- Visual data representation
- Quick action buttons
- Performance indicators
- Real-time updates

### 6. **Contact Management**
- Contact storage and retrieval
- Contact blocking functionality
- Contact search
- Contact synchronization

### 7. **User Profile Management**
- User settings
- Profile information
- SIM card management
- Manual synchronization

### 8. **Push Notifications**
- Real-time notifications
- Firebase integration
- Notification channels
- Background processing

### 9. **Data Synchronization**
- API-based data sync
- Offline capability
- Conflict resolution
- Background sync

### 10. **Security Features**
- JWT token authentication
- Secure API communication
- Data encryption
- Permission management

---

## API Documentation

### Base URL
```
http://13.201.184.185/api/
```

### Authentication
All API calls require a JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

### Key Endpoints

#### **Authentication**
```
POST /auth/login
Body: {
  "username": "user@example.com",
  "password": "password",
  "fcmToken": "firebase_token"
}
```

#### **User Management**
```
GET /getCurrentUser
GET /getUsers
```

#### **Lead Management**
```
GET /advanced-search
POST /add
PUT /add
```

#### **Task Management**
```
GET /tasks
POST /tasks
PUT /tasks
GET /tasks/taskTransition
```

#### **Dashboard**
```
GET /leadsSummary/getAll
GET /dashboard/advancedSummary
```

#### **Settings**
```
GET /global-settings/{userId}
GET /mobile-settings
```

#### **Forms**
```
GET /forms/leads
GET /template/all
```

#### **Cars**
```
GET /cars
```

### Response Format
All API responses follow a consistent format:
```json
{
  "success": true,
  "data": {...},
  "message": "Success message",
  "error": null
}
```

---

## Setup and Build Instructions

### Prerequisites
- Android Studio Arctic Fox or newer
- Java 8 or newer
- Android SDK API 34
- Firebase account (for push notifications)

### Setup Steps

1. **Clone the Repository**
```bash
git clone https://github.com/urstrulyDiwakary/arvoice.git
cd arvoice
```

2. **Extract the Source Code**
```bash
unzip arvoice.zip
cd arvoice
```

3. **Configure Firebase**
- Create a new Firebase project
- Add your Android app to the project
- Download `google-services.json`
- Place it in the `app/` directory

4. **Update API Configuration**
- Edit `WebApi.java` to set your API base URL
- Update API endpoints in `Constant.java`

5. **Build the Project**
```bash
./gradlew build
```

6. **Run the Application**
```bash
./gradlew installDebug
```

### Configuration Files

#### **build.gradle (Project)**
```gradle
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.3.15'
    }
}
```

#### **build.gradle (App)**
```gradle
android {
    compileSdk 34
    defaultConfig {
        applicationId "com.arvoice"
        minSdk 24
        targetSdk 34
    }
    dataBinding {
        enabled true
    }
    viewBinding {
        enabled true
    }
}
```

#### **Dependencies**
```gradle
dependencies {
    // Firebase
    implementation 'com.google.firebase:firebase-messaging:23.4.1'
    implementation 'com.google.firebase:firebase-analytics:21.5.0'
    implementation 'com.google.firebase:firebase-crashlytics:18.4.1'
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.9.1'
    
    // UI Components
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
}
```

---

## Code Examples

### 1. **Making an API Call**
```java
public void callLoginAPI(String email, String password) {
    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject.put("username", email);
        jsonObject.put("password", password);
        jsonObject.put("fcmToken", firebaseToken);
    } catch (Exception e) {
        e.printStackTrace();
    }

    RequestBody requestBody = RequestBody.create(
        jsonObject.toString(),
        MediaType.parse("application/json; charset=utf-8")
    );

    ApiInterface apiInterface = ApiClient.getPostService().create(ApiInterface.class);
    Call<LoginResponse> call = apiInterface.login(requestBody);
    
    call.enqueue(new Callback<LoginResponse>() {
        @Override
        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                LoginResponse loginResponse = response.body();
                // Handle successful login
                saveUserData(loginResponse);
                navigateToDashboard();
            }
        }

        @Override
        public void onFailure(Call<LoginResponse> call, Throwable t) {
            // Handle API failure
            Utils.showToast("Login failed: " + t.getMessage());
        }
    });
}
```

### 2. **Fragment Navigation**
```java
private void loadFragment(Fragment fragment) {
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    transaction.replace(R.id.container, fragment);
    transaction.addToBackStack(null);
    transaction.commit();
}
```

### 3. **RecyclerView Setup**
```java
private void setupRecyclerView() {
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    adapter = new LeadListAdapter(this, leadList, this);
    recyclerView.setAdapter(adapter);
    
    // Add item decoration
    DividerItemDecoration divider = new DividerItemDecoration(
        recyclerView.getContext(), 
        DividerItemDecoration.VERTICAL
    );
    recyclerView.addItemDecoration(divider);
}
```

### 4. **SharedPreferences Usage**
```java
private void saveUserData(LoginResponse response) {
    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString("token", response.getToken());
    editor.putString("userId", response.getUserId());
    editor.putString("email", response.getEmail());
    editor.putString("role", response.getRole());
    editor.apply();
}

private boolean isUserLoggedIn() {
    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
    String token = prefs.getString("token", "");
    return !token.isEmpty();
}
```

### 5. **Firebase Messaging**
```java
@Override
public void onMessageReceived(RemoteMessage remoteMessage) {
    if (remoteMessage.getNotification() != null) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        showNotification(title, body);
    }
}

private void showNotification(String title, String message) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true);

    NotificationManagerCompat manager = NotificationManagerCompat.from(this);
    manager.notify(101, builder.build());
}
```

### 6. **Permission Handling**
```java
private void checkPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                NOTIFICATION_PERMISSION_REQUEST_CODE);
        }
    }
}
```

---

## Conclusion

ARVoice is a comprehensive business application that combines CRM functionality with VoIP capabilities. The application follows Android best practices and modern development patterns, making it maintainable and scalable. The modular architecture allows for easy feature additions and modifications.

The application provides a complete solution for businesses to manage their customer relationships, track leads, manage tasks, and maintain effective communication with customers through integrated calling features.

---

*This documentation provides a complete overview of the ARVoice Android application. For specific implementation details or troubleshooting, refer to the source code and inline comments.*