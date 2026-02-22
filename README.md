<p align="left">
  <img src="https://github.com/user-attachments/assets/00f03808-90e8-4025-970c-dcbe4138ee1c" width="150" title="MemoryWall Logo">
</p>

# 📸 MemoryWall -- Real-Time Collaborative Memory Wall

MemoryWall is a modern Android application that enables users to **create
and share a real-time digital memory wall** for events such as weddings,
birthdays, trips, and conferences.

Participants can join events using a **unique 6-digit code**, **upload photos**
directly from their camera, and **share blessings and memories instantly**
using Firebase-powered real-time updates.

------------------------------------------------------------------------
## App Showcase (Screenshots)📸

<details> 
  <summary><b>1. Authentication & Onboarding</b></summary> 
  <br> 
  <p align="center">
    <img src="https://github.com/user-attachments/assets/0b36f02c-399a-4e98-8a14-0371e9519b05" width="250" />
    <img src="https://github.com/user-attachments/assets/ba12a2ec-8694-4562-9e7e-c032c4655802" width="250"  />
    <img src="https://github.com/user-attachments/assets/97480ce3-ed38-4a07-b5ba-34346437349c" width="250"  />
  </p> 
 <p align="center"><i>Professional login flow using <b>FirebaseUI-Auth</b> for secure authentication, ensuring a high-standard security layer with minimal boilerplate.</i></p>
</details>

<details> 
  <summary><b>2. Main Dashboard & Event Management</b></summary> 
  <br> 
  <p align="center"> 
    <img src="https://github.com/user-attachments/assets/35e0b609-bcd9-414a-8d62-9518144798d4" width="250" /> 
    <img src="https://github.com/user-attachments/assets/609c0a3c-e9ee-4b2f-b818-b09bba18965e" width="250" /> 
    <img src="https://github.com/user-attachments/assets/db2e1786-9876-4d3f-b6f2-bcf5f60016e3" width="250" /> 
  </p> 
  <p align="center"><i>Interactive dashboard for event creation and joining. The <b>6-digit code</b> logic maps directly to Firestore document IDs for O(1) retrieval speed.</i></p>
</details>

<details> 
  <summary><b>3. The Collaborative Live Wall</b></summary> 
  <br> 
  <p align="center"> 
    <img src="https://github.com/user-attachments/assets/39df0c4a-31f2-4d48-bb38-054e677dc0a9" width="250" /> 
    <img src="https://github.com/user-attachments/assets/e0781e7c-adb2-4fe3-b003-58129d9361da" width="250" /> 
  </p> 
  <p align="center"><i>Real-time feed powered by Firestore <b>SnapshotListeners</b>. Images are handled by <b>Glide</b> for optimized memory management and smooth scrolling.</i></p>
</details>

<details> 
  <summary><b>4. User Profile & Personal Stats</b></summary> 
  <br> 
  <p align="center"> 
    <img src="https://github.com/user-attachments/assets/beb71a6b-3a9a-4850-8e7b-7249311518e4" width="250" /> 
    <img src="https://github.com/user-attachments/assets/3ce47bf8-9f4d-429d-bcd9-07e846ffd7d6" width="250" /> 
  </p> 
  <p align="center"><i>Live statistics dashboard using <b>Collection Group Queries</b> to aggregate user contributions across multiple sub-collections in real-time.</i></p>
</details>

## 🎬 Demo

https://vimeo.com/1167180286

------------------------------------------------------------------------
## 🚀 Key Features

### 🔐 Authentication System

-   Secure email/password authentication using Firebase Authentication
-   Persistent user session management
-   Secure logout functionality
-   Restricted access to protected screens

### 🎉 Event Creation & Joining

-   Create new events with name, description, and unique 6-digit code
-   Join existing events via event code
-   Prevent duplicate event registrations
-   User ↔ Event relationship management

### 🖼 Live Memory Wall

-   Real-time updates using Firestore Snapshot Listeners
-   RecyclerView dynamic display
-   Sorted by latest uploads
-   Efficient image loading with Glide

### 📷 Camera & Media Upload

-   Capture photos directly within the app
-   Upload images to Firebase Storage

### 💬 Memory Sharing

-   Add text messages/blessings alongside photos
-   Display uploader name and upload time
-   Store structured Memory objects in Firestore

### 👤 User Profile Dashboard

-   Display number of joined events
-   Count total shared memories

### 🎨 Modern UI/UX

-   Splash Screen implementation
-   Responsive and intuitive layouts

------------------------------------------------------------------------

## 🛠 Tech Stack

-   Language: Java
-   IDE: Android Studio
-   Backend: Firebase

### Firebase Services

-   Firebase Authentication
-   Firebase Firestore (NoSQL Database)
-   Firebase Storage

### Libraries

-   FirebaseUI-Auth
-   Glide (Image Loading & Caching)
-   Material Components for Android

------------------------------------------------------------------------

## 📐 Data Architecture

### Collections

**Events** - eventId - eventName - eventDate - eventCode - creatorUid

**Users** - userId - memoryCount - myEventIds (Reference List)

**Memories (Subcollection under Events)** - memoryId - imageUrl - greeting - userName - userId - timestamp

------------------------------------------------------------------------

## 🧠 Challenges & Solutions

### Real-Time Updates

Implemented Firestore SnapshotListeners to enable instant UI updates
without manual refresh.

### Data Normalization

Used reference-based structure to avoid duplication and maintain a
single source of truth.

### Cross-Collection Statistics

Used Collection Group Queries to efficiently calculate total shared
memories.

### Image Performance Optimization

Integrated Glide for caching and async image loading to prevent memory
issues.

### Security Enforcement

Applied Firebase Security Rules and user validation before allowing
event access.


------------------------------------------------------------------------


------------------------------------------------------------------------

## 🛡 License

This project was developed as part of an academic course submission.
