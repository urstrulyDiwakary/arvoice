# ARVoice Technical Architecture & Flow Diagrams

## Application Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           ARVoice Android App                            │
├─────────────────────────────────────────────────────────────────────────┤
│                          Presentation Layer                             │
├─────────────────────────────────────────────────────────────────────────┤
│  Activities (14)           │  Fragments (4)        │  Adapters (11)     │
│  ├─ SplashActivity         │  ├─ DashboardFragment  │  ├─ LeadListAdapter │
│  ├─ LoginActivity          │  ├─ LeadFragment       │  ├─ CallLogAdapter  │
│  ├─ DashboardActivity      │  ├─ DialFragment       │  ├─ TaskListAdapter │
│  ├─ AddLeadsActivity       │  └─ SiteVisitFragment  │  └─ SummaryAdapter  │
│  ├─ EditLeadsActivity      │                       │                    │
│  ├─ AddTasksActivity       │                       │                    │
│  ├─ EditTasksActivity      │                       │                    │
│  └─ ... (7 more)          │                       │                    │
├─────────────────────────────────────────────────────────────────────────┤
│                           Business Logic Layer                          │
├─────────────────────────────────────────────────────────────────────────┤
│  Utils (4)                 │  Models (17)          │  Profile (4)       │
│  ├─ Constant               │  ├─ LoginResponse      │  ├─ UserDetailAct. │
│  ├─ Utils                  │  ├─ LeadsListModel     │  ├─ BlockListAct.  │
│  ├─ ProgressUtils          │  ├─ TaskListModel      │  ├─ ManualSyncAct. │
│  └─ NavigationDrawerHelper │  └─ CallLogModel       │  └─ SimCardMgrAct. │
├─────────────────────────────────────────────────────────────────────────┤
│                            Data Layer                                   │
├─────────────────────────────────────────────────────────────────────────┤
│  Network Layer             │  Local Storage        │  Firebase Services │
│  ├─ ApiClient              │  ├─ SharedPreferences  │  ├─ FCM Service     │
│  ├─ ApiInterface           │  ├─ App Settings       │  ├─ Analytics       │
│  └─ WebApi                 │  └─ User Preferences   │  └─ Crashlytics     │
├─────────────────────────────────────────────────────────────────────────┤
│                          External Services                              │
├─────────────────────────────────────────────────────────────────────────┤
│  Backend API               │  Firebase Platform    │  Android System    │
│  ├─ Authentication         │  ├─ Cloud Messaging    │  ├─ Phone System   │
│  ├─ Lead Management        │  ├─ Analytics          │  ├─ Contacts        │
│  ├─ Task Management        │  └─ Crashlytics        │  └─ Call Logs       │
│  └─ User Management        │                       │                    │
└─────────────────────────────────────────────────────────────────────────┘
```

## Application Flow Diagrams

### 1. App Startup Flow
```
┌─────────────┐
│  App Launch │
└─────────────┘
      │
      ▼
┌─────────────┐
│SplashActivity│
└─────────────┘
      │
      ▼
┌──────────────────────┐
│Check Notifications   │
│Permission (API 33+)  │
└──────────────────────┘
      │
      ▼
┌──────────────────────┐
│Check Login Status    │
│(SharedPreferences)   │
└──────────────────────┘
      │
      ▼
┌─────────────────────────────────────┐
│              Decision               │
│        User Logged In?              │
└─────────────────────────────────────┘
      │                    │
      ▼ Yes                ▼ No
┌─────────────┐    ┌─────────────┐
│Dashboard    │    │Login        │
│Activity     │    │Activity     │
└─────────────┘    └─────────────┘
```

### 2. Authentication Flow
```
┌─────────────┐
│LoginActivity│
└─────────────┘
      │
      ▼
┌─────────────┐
│User Input   │
│Validation   │
└─────────────┘
      │
      ▼
┌─────────────┐
│Firebase     │
│Token        │
│Generation   │
└─────────────┘
      │
      ▼
┌─────────────┐
│API Login    │
│Call         │
└─────────────┘
      │
      ▼
┌─────────────────────────────────────┐
│              Decision               │
│        Login Successful?            │
└─────────────────────────────────────┘
      │                    │
      ▼ Yes                ▼ No
┌─────────────┐    ┌─────────────┐
│Save User    │    │Show Error   │
│Data         │    │Message      │
└─────────────┘    └─────────────┘
      │
      ▼
┌─────────────┐
│Car Animation│
│& Navigate   │
└─────────────┘
      │
      ▼
┌─────────────┐
│Dashboard    │
│Activity     │
└─────────────┘
```

### 3. Dashboard Navigation Flow
```
┌─────────────┐
│Dashboard    │
│Activity     │
└─────────────┘
      │
      ▼
┌─────────────┐
│Bottom       │
│Navigation   │
│Setup        │
└─────────────┘
      │
      ▼
┌─────────────────────────────────────┐
│           Fragment Loading          │
├─────────────────────────────────────┤
│ Dashboard │ Lead │ Dial │ SiteVisit │
│ Fragment  │ Frag │ Frag │ Fragment  │
└─────────────────────────────────────┘
      │
      ▼
┌─────────────┐
│Load         │
│Fragment     │
│Content      │
└─────────────┘
      │
      ▼
┌─────────────┐
│API Calls    │
│for Data     │
└─────────────┘
      │
      ▼
┌─────────────┐
│Update UI    │
│with Data    │
└─────────────┘
```

### 4. Lead Management Flow
```
┌─────────────┐
│Lead         │
│Fragment     │
└─────────────┘
      │
      ▼
┌─────────────┐
│Load Leads   │
│from API     │
└─────────────┘
      │
      ▼
┌─────────────┐
│Display in   │
│RecyclerView │
└─────────────┘
      │
      ▼
┌─────────────────────────────────────┐
│           User Actions              │
├─────────────────────────────────────┤
│ Add Lead │ Edit │ Search │ Filter  │
└─────────────────────────────────────┘
      │
      ▼
┌─────────────┐
│Perform      │
│Action       │
└─────────────┘
      │
      ▼
┌─────────────┐
│API Call     │
│(POST/PUT)   │
└─────────────┘
      │
      ▼
┌─────────────┐
│Update UI    │
│& List       │
└─────────────┘
```

### 5. Call Management Flow
```
┌─────────────┐
│Dial         │
│Fragment     │
└─────────────┘
      │
      ▼
┌─────────────┐
│Load Call    │
│Log from     │
│Device       │
└─────────────┘
      │
      ▼
┌─────────────┐
│Display Call │
│History      │
└─────────────┘
      │
      ▼
┌─────────────────────────────────────┐
│           User Actions              │
├─────────────────────────────────────┤
│ Make Call │ View Details │ Block   │
└─────────────────────────────────────┘
      │
      ▼
┌─────────────┐
│Handle Call  │
│Action       │
└─────────────┘
      │
      ▼
┌─────────────┐
│Update Call  │
│Log & Sync   │
└─────────────┘
```

### 6. Task Management Flow
```
┌─────────────┐
│Task         │
│Management   │
└─────────────┘
      │
      ▼
┌─────────────┐
│Load Tasks   │
│from API     │
└─────────────┘
      │
      ▼
┌─────────────┐
│Display      │
│Tasks        │
└─────────────┘
      │
      ▼
┌─────────────────────────────────────┐
│           User Actions              │
├─────────────────────────────────────┤
│ Add Task │ Edit │ Status │ Assign │
└─────────────────────────────────────┘
      │
      ▼
┌─────────────┐
│Task         │
│Transition   │
│API Call     │
└─────────────┘
      │
      ▼
┌─────────────┐
│Update Task  │
│Status & UI  │
└─────────────┘
```

## Data Flow Architecture

### 1. API Request Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Activity  │───▶│   Model     │───▶│ ApiClient   │
│   Fragment  │    │   Request   │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
                                            │
                                            ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Update    │◀───│   Model     │◀───│ HTTP        │
│   UI        │    │   Response  │    │ Request     │
└─────────────┘    └─────────────┘    └─────────────┘
```

### 2. Local Data Storage Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   User      │───▶│ SharedPrefs │───▶│ Local       │
│   Action    │    │ Manager     │    │ Storage     │
└─────────────┘    └─────────────┘    └─────────────┘
                                            │
                                            ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   App       │◀───│   Data      │◀───│ Retrieve    │
│   State     │    │   Retrieved │    │ Data        │
└─────────────┘    └─────────────┘    └─────────────┘
```

### 3. Firebase Notification Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   FCM       │───▶│   Token     │───▶│ Server      │
│   Service   │    │   Generated │    │ Registration│
└─────────────┘    └─────────────┘    └─────────────┘
                                            │
                                            ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Show      │◀───│   Message   │◀───│ Receive     │
│   Notification   │   Processing│    │ Push        │
└─────────────┘    └─────────────┘    └─────────────┘
```

## Component Interaction Diagram

### 1. Activity-Fragment Communication
```
┌─────────────────────────────────────────────────────────────────────────┐
│                        DashboardActivity                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │ Dashboard   │  │ Lead        │  │ Dial        │  │ SiteVisit   │   │
│  │ Fragment    │  │ Fragment    │  │ Fragment    │  │ Fragment    │   │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘   │
│       │                │                │                │             │
│       ▼                ▼                ▼                ▼             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │ Summary     │  │ Lead List   │  │ Call Log    │  │ Visit List  │   │
│  │ Adapter     │  │ Adapter     │  │ Adapter     │  │ Adapter     │   │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2. Network Layer Communication
```
┌─────────────────────────────────────────────────────────────────────────┐
│                           Network Layer                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────┐           ┌─────────────┐           ┌─────────────┐   │
│  │ ApiClient   │◀────────▶│ ApiInterface│◀────────▶│ WebApi      │   │
│  │             │           │             │           │             │   │
│  │ - Setup     │           │ - Endpoints │           │ - Base URL  │   │
│  │ - Config    │           │ - Methods   │           │ - Constants │   │
│  │ - Timeouts  │           │ - Headers   │           │ - MediaType │   │
│  └─────────────┘           └─────────────┘           └─────────────┘   │
│       │                            │                            │       │
│       ▼                            ▼                            ▼       │
│  ┌─────────────┐           ┌─────────────┐           ┌─────────────┐   │
│  │ OkHttp      │           │ Retrofit    │           │ Gson        │   │
│  │ Client      │           │ Instance    │           │ Converter   │   │
│  └─────────────┘           └─────────────┘           └─────────────┘   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3. Data Model Relationships
```
┌─────────────────────────────────────────────────────────────────────────┐
│                          Data Models                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│  │ User        │────│ Lead        │────│ Task        │                │
│  │ Models      │    │ Models      │    │ Models      │                │
│  │             │    │             │    │             │                │
│  │ - Login     │    │ - LeadsList │    │ - TaskList  │                │
│  │ - Current   │    │ - LeadCreate│    │ - Transition│                │
│  │ - Settings  │    │ - LeadFilter│    │             │                │
│  └─────────────┘    └─────────────┘    └─────────────┘                │
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│  │ Call        │    │ System      │    │ UI          │                │
│  │ Models      │    │ Models      │    │ Models      │                │
│  │             │    │             │    │             │                │
│  │ - CallLog   │    │ - Common    │    │ - Summary   │                │
│  │ - CallItem  │    │ - Response  │    │ - Group     │                │
│  │             │    │ - Field     │    │ - CarList   │                │
│  └─────────────┘    └─────────────┘    └─────────────┘                │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

## Security Architecture

### 1. Authentication Security
```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Security Layers                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│  │ User Input  │───▶│ Validation  │───▶│ Encryption  │                │
│  │ Validation  │    │ Rules       │    │ (HTTPS)     │                │
│  └─────────────┘    └─────────────┘    └─────────────┘                │
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│  │ JWT Token   │───▶│ Secure      │───▶│ API         │                │
│  │ Storage     │    │ Headers     │    │ Authorization│                │
│  └─────────────┘    └─────────────┘    └─────────────┘                │
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│  │ Permission  │───▶│ Data        │───▶│ Secure      │                │
│  │ Management  │    │ Encryption  │    │ Communication│                │
│  └─────────────┘    └─────────────┘    └─────────────┘                │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2. Data Protection Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Sensitive   │───▶│ Local       │───▶│ Encrypted   │
│ Data        │    │ Storage     │    │ Storage     │
└─────────────┘    └─────────────┘    └─────────────┘
      │                                      │
      ▼                                      ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ API         │───▶│ HTTPS       │───▶│ Secure      │
│ Transmission│    │ Protocol    │    │ Transport   │
└─────────────┘    └─────────────┘    └─────────────┘
```

## Performance Optimization

### 1. Memory Management
```
┌─────────────────────────────────────────────────────────────────────────┐
│                      Memory Optimization                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│  │ ViewBinding │    │ Fragment    │    │ Adapter     │                │
│  │ Lifecycle   │    │ Lifecycle   │    │ ViewHolder  │                │
│  │ Management  │    │ Management  │    │ Recycling   │                │
│  └─────────────┘    └─────────────┘    └─────────────┘                │
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│  │ Image       │    │ Background  │    │ Memory      │                │
│  │ Loading     │    │ Tasks       │    │ Leak        │                │
│  │ Optimization│    │ Management  │    │ Prevention  │                │
│  └─────────────┘    └─────────────┘    └─────────────┘                │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2. Network Optimization
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Request     │───▶│ Caching     │───▶│ Offline     │
│ Batching    │    │ Strategy    │    │ Support     │
└─────────────┘    └─────────────┘    └─────────────┘
      │                                      │
      ▼                                      ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Connection  │───▶│ Retry       │───▶│ Background  │
│ Pooling     │    │ Logic       │    │ Sync        │
└─────────────┘    └─────────────┘    └─────────────┘
```

## Testing Strategy

### 1. Testing Pyramid
```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Testing Layers                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│                        ┌─────────────┐                                 │
│                        │     UI      │                                 │
│                        │   Tests     │                                 │
│                        │  (Espresso) │                                 │
│                        └─────────────┘                                 │
│                                                                         │
│                ┌─────────────┐   ┌─────────────┐                       │
│                │ Integration │   │ Component   │                       │
│                │   Tests     │   │   Tests     │                       │
│                │  (MockWebS) │   │  (Robolect) │                       │
│                └─────────────┘   └─────────────┘                       │
│                                                                         │
│        ┌─────────────┐   ┌─────────────┐   ┌─────────────┐             │
│        │    Unit     │   │   Model     │   │  Utility    │             │
│        │   Tests     │   │   Tests     │   │   Tests     │             │
│        │   (JUnit)   │   │   (JUnit)   │   │   (JUnit)   │             │
│        └─────────────┘   └─────────────┘   └─────────────┘             │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2. Test Coverage Areas
```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Test Coverage                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│  │ Business    │    │ Network     │    │ UI          │                │
│  │ Logic       │    │ Layer       │    │ Components  │                │
│  │ Testing     │    │ Testing     │    │ Testing     │                │
│  └─────────────┘    └─────────────┘    └─────────────┘                │
│                                                                         │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                │
│  │ Data        │    │ Error       │    │ Performance │                │
│  │ Validation  │    │ Handling    │    │ Testing     │                │
│  │ Testing     │    │ Testing     │    │             │                │
│  └─────────────┘    └─────────────┘    └─────────────┘                │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

This technical architecture document provides a comprehensive overview of the ARVoice application's structure, data flow, and component interactions. It serves as a reference for developers to understand the system's design and implementation details.