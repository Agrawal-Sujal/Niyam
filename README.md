# Niyam

Niyam is an Android productivity application designed to help users manage tasks efficiently, track progress, and collaborate with friends. The project leverages modern Android development practices, including Jetpack Compose for UI, Hilt for dependency injection, and a clean architecture approach.
## Demo

Watch the demo video: [Niyam-Android Demo](https://youtube.com/shorts/sNqWNokgFv8?si=A0mJHG2Yx-7d7Mq-)

## Features

- **Task Management**
  - Create, update, and delete both flexible and time-bound tasks.
  - Add sub-tasks to main tasks for granular tracking.
  - View daily/weekly overview of tasks.
  - Progress tracking and completion status for every task.

- **Flexible and Time-bound Tasks**
  - Flexible tasks can be scheduled within a date window and allotted hours.
  - Time-bound tasks have strict start/end times and durations.

- **Friend & Social Features**
  - Search and add friends by username.
  - Manage pending and incoming friend requests (accept, reject, withdraw).
  - View and manage your list of friends.
  - Social progress sharing and collaboration.

- **Notifications & Reminders**
  - Automatic notification for running, paused, and completed tasks.
  - Android notification channel integration for timely alerts.

- **Profile & Sync**
  - User profile management.
  - Data sync functionality for cloud backup and restore.

- **Modern Architecture**
  - Jetpack Compose UI.
  - MVVM pattern.
  - Hilt for dependency injection.
  - Room/Datastore for local data persistence.
  - Retrofit for API communication.

## Getting Started

### Installation

1. **Clone the repository:**
   ```sh
   git clone https://github.com/Agrawal-Sujal/Niyam.git
   ```

2. **Open in Android Studio:**
   - Open the project folder in Android Studio.
   - Let Gradle sync and resolve all dependencies.

3. **Configure Backend:**
   - The app expects a backend URL set in `Constants.kt` (`NIYAM_BASE_URL`). Update this as needed.

4. **Run the app:**
   - Choose your device/emulator and run.

## Tech Stack

- **Kotlin** (primary language)
- **Jetpack Compose** (declarative UI)
- **Room/Datastore** (local persistence)
- **Retrofit + OkHttp** (network/API communication)
- **Hilt** (dependency injection)
- **Material Design 3**

## Folder Structure

- `app/src/main/java/com/project/niyam/` - Main app code
  - `data/` - Local data sources, repositories
  - `domain/` - Domain models and mappers
  - `ui/` - Compose screens and navigation
  - `utils/` - Helpers, constants, interceptors
  - `di/` - Dependency injection modules
