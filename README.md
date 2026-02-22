# 📸 MemoryWall -- Real-Time Collaborative Memory Wall

MemoryWall is a modern Android application that enables users to **create
and share a real-time digital memory wall** for events such as weddings,
birthdays, trips, and conferences.

Participants can join events using a **unique 6-digit code**, **upload photos**
directly from their camera, and **share blessings and memories instantly**
using Firebase-powered real-time updates.

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

## 🎬 Demo

https://vimeo.com/1167180286

------------------------------------------------------------------------

## 🛡 License

This project was developed as part of an academic course submission.
