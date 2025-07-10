# ARVoice Setup and Troubleshooting Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Project Setup](#project-setup)
4. [Firebase Configuration](#firebase-configuration)
5. [API Configuration](#api-configuration)
6. [Building the Project](#building-the-project)
7. [Running the Application](#running-the-application)
8. [Testing](#testing)
9. [Deployment](#deployment)
10. [Troubleshooting](#troubleshooting)
11. [Performance Optimization](#performance-optimization)
12. [Security Considerations](#security-considerations)

---

## Prerequisites

### System Requirements
- **Operating System**: Windows 10/11, macOS 10.14+, or Linux Ubuntu 18.04+
- **RAM**: Minimum 8GB (16GB recommended)
- **Storage**: At least 10GB free space
- **Internet**: Stable internet connection for dependencies

### Software Requirements
- **Android Studio**: Arctic Fox (2020.3.1) or newer
- **Java Development Kit (JDK)**: Java 8 or Java 11
- **Android SDK**: API Level 24-34
- **Git**: For version control
- **Firebase Account**: For push notifications and analytics

### Hardware Requirements (for testing)
- **Android Device**: API Level 24+ (Android 7.0+)
- **USB Cable**: For device debugging
- **Emulator**: Android Virtual Device (AVD) if testing on emulator

---

## Development Environment Setup

### 1. Install Android Studio
```bash
# Download from: https://developer.android.com/studio
# Follow installation wizard
# Accept licenses when prompted
```

### 2. Configure Android SDK
```bash
# Open Android Studio
# Go to File → Settings → Appearance & Behavior → System Settings → Android SDK
# Install required SDK platforms:
# - Android 14 (API 34)
# - Android 7.0 (API 24)
# - Android SDK Build-Tools 34.0.0
# - Android SDK Platform-Tools
# - Android SDK Tools
```

### 3. Set up Android Virtual Device (Optional)
```bash
# Go to Tools → AVD Manager
# Create Virtual Device
# Choose device: Pixel 4 or newer
# Select System Image: API 34 (Android 14)
# Configure AVD settings
# Click Finish
```

### 4. Configure JDK
```bash
# In Android Studio: File → Project Structure → SDK Location
# Set JDK location to Java 8 or Java 11
# Common locations:
# Windows: C:\Program Files\Java\jdk-11.0.x
# macOS: /Library/Java/JavaVirtualMachines/jdk-11.0.x.jdk/Contents/Home
# Linux: /usr/lib/jvm/java-11-openjdk
```

---

## Project Setup

### 1. Clone the Repository
```bash
git clone https://github.com/urstrulyDiwakary/arvoice.git
cd arvoice
```

### 2. Extract Source Code
```bash
# Extract the arvoice.zip file
unzip arvoice.zip
cd arvoice
```

### 3. Open Project in Android Studio
```bash
# Open Android Studio
# Select "Open an existing Android Studio project"
# Navigate to the extracted arvoice folder
# Click OK
```

### 4. Sync Project
```bash
# Android Studio will automatically prompt to sync
# Click "Sync Now" in the notification bar
# Wait for sync to complete
```

---

## Firebase Configuration

### 1. Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project"
3. Enter project name: `arvoice-app`
4. Enable Google Analytics (optional)
5. Click "Create project"

### 2. Add Android App
1. In Firebase Console, click "Add app" → Android
2. Enter package name: `com.arvoice`
3. Enter app nickname: `ARVoice Android`
4. Download `google-services.json`
5. Place file in `app/` directory

### 3. Configure Firebase Services

#### Cloud Messaging (FCM)
```bash
# In Firebase Console:
# 1. Go to Project Settings → Cloud Messaging
# 2. Note the Server Key (for backend)
# 3. Configure notification settings
```

#### Analytics
```bash
# In Firebase Console:
# 1. Go to Analytics → Events
# 2. Configure custom events if needed
# 3. Set up conversion tracking
```

#### Crashlytics
```bash
# In Firebase Console:
# 1. Go to Crashlytics
# 2. Enable Crashlytics
# 3. Follow setup instructions
```

### 4. Verify Firebase Setup
```java
// In LoginActivity.java, check Firebase initialization:
FirebaseApp.initializeApp(this);
FirebaseMessaging.getInstance().getToken()
    .addOnCompleteListener(task -> {
        if (!task.isSuccessful()) {
            Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.getException());
            return;
        }
        String token = task.getResult();
        Log.d("FCM_TOKEN", "Token: " + token);
    });
```

---

## API Configuration

### 1. Update Base URL
```java
// In networkinh/WebApi.java
public class WebApi {
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String BASEURL = "YOUR_API_BASE_URL/api/";
}
```

### 2. Configure API Endpoints
```java
// In utils/Constant.java
public class Constant {
    // Update endpoints as needed
    public static final String loginURL = "auth/login";
    public static final String getCurrentUser = "getCurrentUser";
    public static final String getLeads = "advanced-search";
    // ... other endpoints
}
```

### 3. Set Up Network Security
```xml
<!-- In app/src/main/res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">your-api-domain.com</domain>
    </domain-config>
</network-security-config>
```

### 4. Update AndroidManifest.xml
```xml
<!-- Add network security config -->
<application
    android:networkSecurityConfig="@xml/network_security_config"
    android:usesCleartextTraffic="true"
    ... >
```

---

## Building the Project

### 1. Clean Project
```bash
# In Android Studio: Build → Clean Project
# Or via command line:
cd /path/to/arvoice
./gradlew clean
```

### 2. Build Project
```bash
# In Android Studio: Build → Make Project
# Or via command line:
./gradlew build
```

### 3. Generate APK
```bash
# For debug APK:
./gradlew assembleDebug

# For release APK:
./gradlew assembleRelease
```

### 4. Build Variants
```bash
# In Android Studio: Build → Select Build Variant
# Choose between:
# - debug: For development and testing
# - release: For production deployment
```

---

## Running the Application

### 1. Connect Android Device
```bash
# Enable Developer Options on device:
# Settings → About Phone → Tap Build Number 7 times
# Settings → Developer Options → Enable USB Debugging
# Connect device via USB
```

### 2. Run on Device
```bash
# In Android Studio:
# 1. Select your device from device dropdown
# 2. Click Run button (green triangle)
# 3. Grant permissions when prompted
```

### 3. Run on Emulator
```bash
# In Android Studio:
# 1. Start AVD Manager
# 2. Launch created emulator
# 3. Select emulator from device dropdown
# 4. Click Run button
```

### 4. Install via ADB
```bash
# Install debug APK:
adb install app/build/outputs/apk/debug/app-debug.apk

# Install release APK:
adb install app/build/outputs/apk/release/app-release.apk
```

---

## Testing

### 1. Unit Tests
```bash
# Run unit tests:
./gradlew test

# Run specific test class:
./gradlew test --tests com.arvoice.ExampleUnitTest
```

### 2. Instrumentation Tests
```bash
# Run instrumentation tests:
./gradlew connectedAndroidTest

# Run specific test:
./gradlew connectedAndroidTest --tests com.arvoice.ExampleInstrumentedTest
```

### 3. Manual Testing Checklist
- [ ] App launches successfully
- [ ] Login functionality works
- [ ] Dashboard loads data
- [ ] Lead creation/editing works
- [ ] Task management functions
- [ ] Call functionality works
- [ ] Push notifications work
- [ ] Navigation flows smoothly
- [ ] Error handling works
- [ ] Offline capabilities function

---

## Deployment

### 1. Prepare for Release
```bash
# Update version in app/build.gradle:
android {
    defaultConfig {
        versionCode 2
        versionName "1.1.0"
    }
}
```

### 2. Generate Signed APK
```bash
# In Android Studio:
# 1. Build → Generate Signed Bundle / APK
# 2. Choose APK
# 3. Create new keystore or use existing
# 4. Fill keystore details
# 5. Select release build variant
# 6. Click Finish
```

### 3. ProGuard Configuration
```bash
# In app/proguard-rules.pro:
-keep class com.arvoice.model.** { *; }
-keep class com.arvoice.networkinh.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
```

### 4. Upload to Play Store
```bash
# 1. Create Google Play Console account
# 2. Create new app
# 3. Upload APK/AAB
# 4. Fill app details
# 5. Set up pricing and distribution
# 6. Submit for review
```

---

## Troubleshooting

### Common Build Issues

#### 1. Gradle Sync Failed
```bash
# Solution:
# 1. Check internet connection
# 2. File → Invalidate Caches and Restart
# 3. Delete .gradle folder in project root
# 4. Rebuild project
```

#### 2. Dependency Resolution Failed
```bash
# Solution:
# 1. Check build.gradle dependencies
# 2. Update to compatible versions
# 3. Clean and rebuild project
```

#### 3. Firebase Integration Issues
```bash
# Solution:
# 1. Verify google-services.json is in app/ folder
# 2. Check package name matches Firebase project
# 3. Rebuild project
# 4. Check Firebase plugin is applied
```

### Runtime Issues

#### 1. App Crashes on Launch
```bash
# Debug steps:
# 1. Check logcat for error messages
# 2. Verify all permissions are granted
# 3. Check Firebase initialization
# 4. Verify API connectivity

# Common causes:
# - Missing google-services.json
# - Network connectivity issues
# - Incorrect API base URL
# - Missing permissions
```

#### 2. Login Fails
```bash
# Debug steps:
# 1. Check API endpoint URL
# 2. Verify request format
# 3. Check server response
# 4. Validate credentials

# Common causes:
# - Incorrect API URL
# - Server not responding
# - Invalid credentials
# - Network issues
```

#### 3. Push Notifications Not Working
```bash
# Debug steps:
# 1. Check FCM token generation
# 2. Verify server-side setup
# 3. Test on physical device
# 4. Check notification permissions

# Common causes:
# - FCM token not sent to server
# - Notification permissions denied
# - Firebase configuration issues
# - Server-side FCM setup issues
```

### Performance Issues

#### 1. Slow App Performance
```bash
# Solutions:
# 1. Enable R8/ProGuard for release builds
# 2. Optimize images and resources
# 3. Use lazy loading for data
# 4. Implement proper caching
# 5. Profile app with Android Profiler
```

#### 2. Memory Leaks
```bash
# Solutions:
# 1. Use WeakReference for callbacks
# 2. Properly manage Fragment lifecycle
# 3. Unregister listeners in onDestroy()
# 4. Use Android Memory Profiler
```

#### 3. Network Issues
```bash
# Solutions:
# 1. Implement retry logic
# 2. Add network connectivity checks
# 3. Use proper timeout values
# 4. Implement offline caching
```

### Device-Specific Issues

#### 1. Permission Issues
```bash
# Solutions:
# 1. Request runtime permissions properly
# 2. Handle permission denial gracefully
# 3. Guide user to settings if needed
# 4. Test on different API levels
```

#### 2. SIM Card Issues
```bash
# Solutions:
# 1. Check SIM card permissions
# 2. Handle devices without SIM
# 3. Test dual-SIM functionality
# 4. Implement fallback mechanisms
```

---

## Performance Optimization

### 1. Code Optimization
```java
// Use ViewBinding instead of findViewById
private ActivityMainBinding binding;
binding = ActivityMainBinding.inflate(getLayoutInflater());
setContentView(binding.getRoot());

// Use RecyclerView.ViewHolder properly
@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    // Bind data efficiently
    holder.bind(items.get(position));
}

// Implement lazy loading
private void loadMoreData() {
    if (!isLoading && hasMoreData) {
        isLoading = true;
        // Load next page
    }
}
```

### 2. Memory Optimization
```java
// Use Application class for global state
public class AppController extends MultiDexApplication {
    private static AppController instance;
    
    public static AppController getInstance() {
        return instance;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}

// Proper Fragment lifecycle management
@Override
public void onDestroyView() {
    super.onDestroyView();
    binding = null; // Prevent memory leaks
}
```

### 3. Network Optimization
```java
// Use connection pooling
OkHttpClient client = new OkHttpClient.Builder()
    .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
    .addInterceptor(new CacheInterceptor())
    .build();

// Implement request caching
@GET("endpoint")
@Headers("Cache-Control: max-age=3600")
Call<ResponseType> getData();
```

### 4. UI Optimization
```java
// Use ConstraintLayout for complex layouts
// Avoid nested layouts
// Use vector drawables
// Implement proper loading states
```

---

## Security Considerations

### 1. Data Protection
```java
// Encrypt sensitive data
private void saveSecureData(String key, String value) {
    EncryptedSharedPreferences.create(
        "secure_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    ).edit().putString(key, value).apply();
}
```

### 2. Network Security
```java
// Use certificate pinning
CertificatePinner certificatePinner = new CertificatePinner.Builder()
    .add("yourdomain.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .build();

OkHttpClient client = new OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build();
```

### 3. API Security
```java
// Implement proper authentication
public class AuthInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        
        String token = getAuthToken();
        if (token != null) {
            builder.addHeader("Authorization", "Bearer " + token);
        }
        
        return chain.proceed(builder.build());
    }
}
```

### 4. Code Obfuscation
```bash
# Enable ProGuard/R8 in release builds
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

---

## Maintenance and Updates

### 1. Regular Updates
- Update dependencies regularly
- Monitor security vulnerabilities
- Update target SDK annually
- Review and update permissions

### 2. Monitoring
- Set up Firebase Crashlytics
- Monitor app performance
- Track user analytics
- Monitor API usage

### 3. Backup Strategy
- Regular code backups
- Database backups
- Configuration backups
- Version control best practices

---

## Support and Resources

### Official Documentation
- [Android Developer Guide](https://developer.android.com/guide)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Retrofit Documentation](https://square.github.io/retrofit/)

### Community Resources
- [Stack Overflow](https://stackoverflow.com/questions/tagged/android)
- [Android Developers Reddit](https://www.reddit.com/r/androiddev/)
- [Android Arsenal](https://android-arsenal.com/)

### Tools and Utilities
- [Android Debug Bridge (ADB)](https://developer.android.com/studio/command-line/adb)
- [Postman](https://www.postman.com/) for API testing
- [Firebase Console](https://console.firebase.google.com/)

---

This comprehensive setup and troubleshooting guide should help you successfully set up, build, and maintain the ARVoice Android application. For specific issues not covered here, refer to the official documentation or community resources.