# Android SmartDoorLock

An Android mobile application for controlling and managing a smart door lock system.

- Package name: `com.smart.smartdoor`
- Launcher activity: `.activity.SplashActivity`

This repository contains the main app module plus several embedded libraries used by the project.

## Repository layout

- `app/` — The main Android application module (source, resources, AndroidManifest).
- `aestheticdialogs/` — UI dialog library included as a module.
- `FastBleLib/` — Bluetooth Low Energy helper library included as a module.
- `kprogresshud/` — Progress HUD library module.
- `kprogresshud/src/main/java/com/kaopiz/kprogresshud/KProgressHUD.java` — example location of a HUD class in that module.
- `kprogresshud/`, `ptr-lib/`, `kprogresshud/` — other utility modules and third-party libs included as modules.
- Gradle wrapper files at the repo root (`gradlew`, `gradlew.bat`, `gradle/`) allow building without installing Gradle globally.

## Features

- BLE (Bluetooth Low Energy) support for communicating with locks (via `FastBleLib`).
- User authentication, registration and management screens.
- Background service support (foreground service and boot receiver registered in the manifest).
- Local history and user management screens.

## Prerequisites

- JDK 8 (or later) installed and `JAVA_HOME` set.
- Android SDK and required platform(s) installed (use Android Studio SDK Manager).
- Recommended: Android Studio to import and run the project.

## Build and run (Windows PowerShell)

Open a PowerShell terminal at the repository root (where `gradlew.bat` is located) and run:

```powershell
# Assemble a debug build
.\gradlew.bat assembleDebug

# Install to a connected device (replace :app:assembleDebug with your variant if needed)
.\gradlew.bat installDebug
```

Or open the project in Android Studio and run the `app` configuration on a device or emulator.

## Where to look for important files

- App manifest: `app/src/main/AndroidManifest.xml` (package and permissions).  
- App entry point: `.activity.SplashActivity` (declared with LAUNCHER intent).  
- Strings: `app/src/main/res/values/strings.xml` (app name and string resources).

## Common tasks

- Clean build:

```powershell
.\gradlew.bat clean
```

- Run unit tests / instrumentation tests (if present):

```powershell
.\gradlew.bat test
.\gradlew.bat connectedAndroidTest
```

## Contributing

- Follow the existing code style.  
- Add small, focused commits and clear PR descriptions.  
- If you add new native or third-party libraries, prefer adding them as Gradle modules or dependencies in `build.gradle` files.

## Troubleshooting

- If Gradle complains about missing SDK/Platform, open Android Studio and install the recommended Android SDK platforms and build-tools.  
- For BLE issues, ensure the device supports BLE and that Bluetooth permissions are granted at runtime (Android 5.1+).  
