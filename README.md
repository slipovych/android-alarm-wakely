# Wakely (Legacy) ⏰

> **🕰️ A Quick Journey Back in Time (Developer's Note):**
> I developed this application when I was **16 years old** as a small school science project. My main research goals at the time were to explore the `Room` database architecture and experiment with the (then newly released) `Material UI` components.
> Looking at this codebase now, I see many naive architectural decisions (like database queries on the main thread 😅). However, I purposefully keep this legacy repository public as a personal time capsule. It documents my early steps in Android development and reminds me of how my skills have evolved. Please view it through the lens of an enthusiastic high school student!

An Android application prototype for scheduling one-off alarms that require completing a "mission" (scanning a specific QR code) to turn off the ringing.

## 🛠 Tech Stack
* **Language:** Java (Android)
* **UI:** Activities, RecyclerView, Material Components
* **Persistence:** Room (SQLite)
* **Scheduling:** `AlarmManager` via a foreground `IntentService`
* **QR Integration:** `code-scanner` (Yuriy Budiyev) and `QRGenerator` (AndroidMads / zxing)

## ⚙️ Core Features
* **Event Management:** Create, view, enable, disable, and delete scheduled alarms from a main list.
* **Smart Alarm Trigger:** Rings at the scheduled time and requires user interaction to dismiss.
* **QR Missions:** Generate, scan, and save QR codes via the device camera.
* **Mission Binding:** Attach a specific saved QR code to an alarm event as a mandatory wake-up task.

## 📱 UI Screens

| Screen / Activity | Description |
| :--- | :--- |
| **MainActivity** | Displays a list of events with time, enable/disable switches, and delete buttons. |
| **AlarmRedactor** | Creation screen with a Material time picker and an option to attach a QR mission. |
| **AlarmActivity** | The active ringing screen triggered when the alarm fires. |
| **QrActivity** | Displays a list of stored QR items to select for a mission. |
| **QrCreatingActivity** | Embeds a camera scanner fragment to scan and save new QR codes. |

## 💾 Data Model (Room Database)

| Entity | Purpose |
| :--- | :--- |
| **Event** | Stores alarm data (`uid`, `time` string, `scheduled` state, `relatedQrId`). |
| **Qr** | Stores scanned code data (`qrId`, `qrName`, raw `qrCode` text). |
| **EventDao** | Drives the main list via `LiveData` and handles CRUD operations for alarms. |
| **QrDao** | Manages saved QR codes. |

## 🔄 App Workflows

**Creating an Event:**
1. User opens `AlarmRedactor` and selects a time via the Material picker.
2. App inserts the `Event` into Room and obtains a unique `uid`.
3. App starts `AlarmService` with the `uid` and selected time to schedule the system alarm.

**Managing Events (List):**
1. User toggles the switch or taps delete on an item.
2. The UI updates the `Event.scheduled` state or removes it from Room.
3. `AlarmScheduler` immediately schedules or cancels the system alarm using the `uid`.

## 🏗️ Build Notes
This project uses **Android Gradle Plugin 7.4.2** and **Gradle 7.5**. Due to this combination, **JDK 11** is required for building. The project also relies on JitPack for some dependencies.

Example CLI build (macOS):
```bash
env JAVA_HOME=/path/to/your/jdk-11 \
    ./gradlew :app:assembleDebug
```

## ⚠️ Known Limitations & Technical Debt
* **String-based Time:** `Event.time` is stored as a string; next occurrence is inferred rather than strictly modeled.
* **Main Thread DB:** Room is configured with `allowMainThreadQueries()` (acceptable for a school prototype, bad for production).
* **Legacy Services:** Alarm scheduling uses a foreground `IntentService` instead of modern `WorkManager` or `ForegroundService` patterns.
* **No Reboot Rescheduling:** A `BOOT_COMPLETED` receiver is not implemented; alarms will not survive a device restart.
* **No Edit Flow:** Existing events cannot be edited (only created or deleted).