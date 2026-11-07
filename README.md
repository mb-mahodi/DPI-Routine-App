# DPI Routine — Daffodil Polytechnic Institute (DPI) 🎓📱

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)
![Material 3](https://img.shields.io/badge/Design-Material%203-amber.svg)
![Sync](https://img.shields.io/badge/Data-Google%20Sheet%20Live%20Sync-teal.svg)

An official, modern Android application for **Daffodil Polytechnic Institute (DPI)** 1st Semester **Electronics Technology (ET)** students. The app provides live class tracking, class routine schedules, subject mark distributions, teacher directories, Google Sheet live synchronization, and student profile management.

---

## 🌟 Key Features

- ⏱️ **Live Class & Progress Tracker**:
  - Real-time indicator for ongoing classes (`Live Now`).
  - **Classes Done & Classes Left** live counters (`ক্লাস কতটুক হয়েছে আর কতটুক বাকি আছে`).
  - Visual daily class progress bar with remaining minutes countdown.
  - Automatic institute Prayer & Tiffin Break banner (01:15 PM – 01:40 PM).

- 📅 **Interactive Timetable & Weekly Routine**:
  - Rolling 7-day date selector strip.
  - Full week archive drawer for Sunday through Thursday routines + Friday/Saturday weekend off-day status.
  - Period span markers, room numbers (e.g., R-904, R-603 Electronics Lab, R-502 Physics Lab), and teacher labels.

- 📚 **BTEB Curriculum & Subjects (8 Subjects / 1200 Marks / 24 Credits)**:
  - Complete syllabus coverage for 1st Semester Electronics Technology:
    1. **Bangla-I** (25711) • 100 Marks • 2 Credits
    2. **Physics-I** (25912) • 200 Marks • 4 Credits
    3. **English-I** (25712) • 100 Marks • 2 Credits
    4. **Mathematics-I** (25911) • 200 Marks • 4 Credits
    5. **Engineering Drawing** (21011) • 100 Marks • 2 Credits
    6. **Basic Electricity** (26711) • 200 Marks • 4 Credits
    7. **Basic Electronics** (26811) • 150 Marks • 3 Credits
    8. **Programming in C** (28567) • 150 Marks • 3 Credits
  - Built-in **Attendance Logger** (Present/Absent tracker with percentage calculation).
  - Personal **Subject Notes** storage.

- 👨‍🏫 **Teacher Directory & Guide Teacher Spotlight**:
  - Dedicated spotlight for Official Guide Teacher: **Eng. Abdullah Al Nisat** (Instructor, Electronics Technology).
  - One-touch direct phone dialer and SMS messaging actions.
  - Complete list of course instructors and department designations.

- 📊 **Google Sheet Live Sync & Campus Navigation**:
  - Synchronizes live routine changes from official institute Google Sheet (GID: `222252652`).
  - Direct campus location mapping to Daffodil Polytechnic Institute (Sobhanbag, Dhanmondi, Dhaka) via Google Maps.
  - APK download link and GitHub source code sharing integration.

- 🪪 **Student ID Smart Card & Profile**:
  - Customizable student profile (Name, Roll No, Registration No, Shift, Group).
  - One-tap copyable Roll & Registration chips.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture Layering
- **State Management**: Kotlin `StateFlow`, `MutableStateFlow`, `collectAsStateWithLifecycle`
- **Concurrency**: Kotlin Coroutines
- **Networking & Data**: Google Sheet CSV Parser / OkHttp / SharedPreferences local persistence
- **Testing**: Robolectric local JVM unit testing framework

---

## 📁 Project Structure

```text
com.example/
├── data/
│   ├── datasource/
│   │   └── DefaultRoutineData.kt       # Official BTEB curriculum & routine defaults
│   ├── local/
│   │   └── RoutineDatabase.kt          # SharedPreferences & local state persistence
│   ├── model/
│   │   ├── RoutineModels.kt            # ClassSession, Subject, Teacher, Homework models
│   │   └── StudentProfile.kt           # Student ID profile data class
│   ├── parser/
│   │   └── GoogleSheetParser.kt        # Live CSV parser for Google Sheet routine sync
│   └── repository/
│       └── RoutineRepository.kt        # Centralized data repository
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt               # Bento grid, live progress tracker & today's schedule
│   │   ├── TimetableScreen.kt          # Timeline view & 7-day rolling selector
│   │   ├── TodayScreen.kt              # Detailed today's class cards & attendance logger
│   │   ├── SubjectsScreen.kt           # BTEB 8 subjects, marks breakdown & notes
│   │   ├── TeachersScreen.kt           # Faculty directory & guide teacher card
│   │   ├── CampusSyncScreen.kt         # Google Sheet sync, maps & APK/GitHub links
│   │   └── ProfileScreen.kt            # Student ID smart card & edit dialog
│   ├── theme/
│   │   ├── Color.kt                    # M3 Pastel color palette
│   │   ├── Theme.kt                    # Custom DPI M3 Compose Theme
│   │   └── Type.kt                     # Typography definitions
│   └── RoutineViewModel.kt             # Application ViewModel & live status ticker
└── MainActivity.kt                     # Single-activity container with custom bottom navigation
```

---

## 🚀 Building & Running the Project

### Prerequisites

- **Android Studio**: Ladybug (2024.2.1) or higher
- **JDK**: Java 17 or higher
- **Compile SDK**: 34
- **Min SDK**: 24 (Android 7.0 Nougat)

### Gradle Build Commands

1. **Build Debug APK**:
   ```bash
   gradle assembleDebug
   ```

2. **Run Unit & Robolectric Tests**:
   ```bash
   gradle :app:testDebugUnitTest
   ```

---

## 🏫 Institute Information

- **Institute**: Daffodil Polytechnic Institute (DPI)
- **Institute Code**: 50238
- **Department**: Electronics Technology (ET)
- **Campus Location**: Sobhanbag, Dhanmondi, Dhaka-1207, Bangladesh
- **Acting Principal**: S. M. Jahirul Islam Farhad
- **Executive Director**: Rathindra Nath Das
- **Routine Committee Team Leader**: Md. Emon Khan (Jr. Instructor, Civil)

---

## 📄 License & Credits

Developed for the students of **Daffodil Polytechnic Institute (DPI)** under the Bangladesh Technical Education Board (BTEB) curriculum framework.
