# DPI Routine - Publication Readiness & Release Plan

Daffodil Polytechnic Institute (DPI) 1st Semester Electronics Technology routine app feature analysis, current additions, and proposed production additions for store release.

---

### User Review & Critical Decisions

#### 📌 Currently Implemented Features (অ্যাপে এখন পর্যন্ত যা যা যুক্ত করা হয়েছে):
1. **DPI Official Curriculum & Schedule**: BTEB 1st Semester Electronics Technology 8 subjects (24 credits, 1200 marks) with complete room numbers and teacher directory.
2. **Real-time Live Class Tracker**:
   - **Classes Done (কতটুক হয়েছে)**: Completed class counter with green checkmarks.
   - **Classes Left (কতটুক বাকি আছে)**: Remaining class counter with active/next class status.
   - **Daily Progress Bar**: Visual progress percentage indicator.
3. **Weekly Holidays Card**: Dedicated Bento card showing 2 weekly off days (**Friday & Saturday**).
4. **Android Home Screen AppWidgets**:
   - **4x2 Today Routine Widget**: Detailed live widget with progress bar, active/next class details, teacher, and room number.
   - **2x2 Quick Stats Widget**: Compact Done/Left counters widget.
   - **In-App Widget Setup Guide**: Card with instructions and widget sync button.
5. **Attendance & Note Logging**: Quick attendance logger (Present/Absent ratio) and subject note saver.

---

#### 🚀 Proposed Additions for Full Release / Publish (অ্যাপটি রিলিজ ও প্রকাশ করার জন্য যা যা যুক্ত করা হবে):
1. **PDF Routine & Class Schedule Export**:
   - Generates a beautifully formatted PDF document of the weekly class schedule that students can save offline or share on WhatsApp/Facebook groups.
2. **Exam Routine & Class Test Tracker**:
   - Dedicated Exam Calendar section for Class Tests (CT), Midterm exams, and BTEB Semester Final exams with countdown timers.
3. **BTEB CGPA & Grade Calculator**:
   - Interactive GPA calculator aligned with official BTEB grading rules (Theory/Practical continuous and final marks).
4. **DPI Campus Notice Board & Lab Guidelines**:
   - Important campus updates, lab rules for R-502 (Physics), R-603 (Electronics), R-604 (Electricity), and Guide Teacher info.

---

### 1. Overview & Core Concept

- **Goal**: Deliver a polished, 100% production-ready mobile application for DPI students with offline capabilities, home screen widgets, printable routines, and academic utilities.
- **Target Audience**: DPI Electronics Technology students, guide teachers, and class representatives.
- **Key Value**: Immediate daily schedule awareness on phone home screens, offline PDF exports, and complete BTEB academic tracking.

---

### 2. User Experience & Visual Design

- **Theme & Identity**:
  - Dark sleek bento cards (`#1E242B`) with Material 3 dynamic pastel accents.
  - Clear visual hierarchy with distinct status badges (`LIVE NOW`, `DONE`, `LEFT`, `WEEKEND`).
- **New Tab / Options**:
  - **Export PDF Button**: Placed in the Timetable/Routine view for 1-tap PDF generation.
  - **Exam & Result Utility Tab**: Access to Exam Countdown and BTEB GPA Calculator.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Native Android PDF Export**:
  - *Chosen Approach*: Use Android's native `android.graphics.pdf.PdfDocument` API.
  - *Why*: Zero external library dependencies, lightweight APK size, and high-quality print output.
- **Decision 2: Off-line BTEB Grading Logic**:
  - *Chosen Approach*: Embed official BTEB credit & marks calculation rules locally in Kotlin.
  - *Why*: Enables instant GPA calculation without needing internet access.

---

### 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                        MainActivity                         │
│   (Home | Schedule | Subjects | Exam/GPA | Profile/Widgets) │
└──────────────────────────────┬──────────────────────────────┘
                               │
             ┌─────────────────┴─────────────────┐
             │         RoutineViewModel          │
             └─────────────────┬─────────────────┘
                               │
     ┌─────────────────────────┼─────────────────────────┐
     ▼                         ▼                         ▼
┌───────────────┐   ┌────────────────────┐    ┌────────────────────┐
│ RoutineData   │   │ RoutinePdfExporter │    │ BtebGpaCalculator  │
│  Repository   │   │ (Android Pdf API)  │    │ (BTEB Grade Rules) │
└───────────────┘   └────────────────────┘    └────────────────────┘
```

---

### Implementation Phases

- **Phase 1**: Add PDF Routine Exporter (`RoutinePdfExporter.kt`) with print/share launcher.
- **Phase 2**: Add BTEB GPA & Marks Calculator UI with continuous & final marks breakdown.
- **Phase 3**: Add Exam Routine & Class Test Calendar tab with live countdowns.
- **Phase 4**: Final compile, test verification, and release preparation.
