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

<details>
  <summary><b>1. The "UI Flicker" Challenge (Auth Validation)</b></summary>
  <br>
  <p align="center">
    <img src="https://github.com/user-attachments/assets/f1af93dc-5a72-45ff-b428-9346d4c83958" width="600" />
    <img src="https://github.com/user-attachments/assets/fdf8e713-fe1a-4238-b6f0-42f1f6086917" width="600" />
  </p>
  <p align="center">
  <b>Problem:</b> The app would flash the main dashboard for a second before redirecting to the login screen.<br>
  <b>Solution:</b> I refactored the <code>onCreate</code> logic to check for <code>currentUser</code> <b>before</b> calling <code>setContentView</code>. If no user is found, the app immediately launches the Sign-In intent, ensuring a seamless and secure entry.
  </p>
</details>


<details>
  <summary><b>2. Data Normalization & Integrity</b></summary>
  <br>
  <p align="center">
    <img src="https://github.com/user-attachments/assets/3ff637cd-26fc-475d-b0c4-941f0c22f206" width="600" />
    <img src="https://github.com/user-attachments/assets/462f8d76-d9c3-4991-8e5e-5319ecd058ea" width="600" />
    <img src="https://github.com/user-attachments/assets/89c3b4d0-caea-4b24-94ca-cb89839a2338" width="600" />
  </p>
  <p align="center">
  <b>Problem:</b> Duplicating full event data inside user documents led to sync issues and bloated database storage.<br>
  <b>Solution:</b> I implemented a <b>Reference-based architecture</b>. The user document now stores only an array of <code>myEventIds</code> using <code>FieldValue.arrayUnion</code>. This ensures a "Single Source of Truth" where event updates reflect globally for all participants.
  </p>
</details>


<details>
  <summary><b>3. Real-Time Sync without Manual Refresh</b></summary>
  <br>
  <p align="center">
    <img src="https://github.com/user-attachments/assets/54698de1-658a-435f-a0c1-5d2f01dd6171" width="600" />
  </p>
  <p align="center">
  <b>Problem:</b> Static lists required users to exit and re-enter the screen to see new memories.<br>
  <b>Solution:</b> I implemented <b>Firestore SnapshotListeners</b>. This creates a persistent "handshake" with the database, pushing new data to the <code>RecyclerView</code> instantly as it's uploaded, providing a true live-feed experience.
  </p>
</details>

<details>
  <summary><b>4. Real-time Data Denormalization & Atomic Counters</b></summary>
  <br>
  <p align="center">
    <img src="https://github.com/user-attachments/assets/7e304517-f807-4f97-89c1-d4077313f9e6" width="600" />
    <img src="https://github.com/user-attachments/assets/bf7d97a2-14f7-4862-b95b-e13e7f8cd03a" width="600" />
  </p>
  <p align="center">
  <b>Problem:</b> Counting user memories across thousands of nested sub-collections (Events -> Memories) is traditionally slow and expensive ($O(N)$) in NoSQL databases.<br>
  <b>Solution:</b> I implemented a <b>Denormalized Counter Strategy</b>. Instead of querying the entire database, I utilized <code>FieldValue.increment()</code> for atomic updates and <b>Firebase Write Batches</b> to keep user statistics in sync during memory creation and event deletion. This ensures <b>O(1) read performance</b> for the Profile screen, providing an instant user experience.
  </p>
</details>

<details>
  <summary><b>5. Memory Management & Image Scaling</b></summary>
  <br>
  <p align="center">
    <img src="https://github.com/user-attachments/assets/4012aca5-ee76-4fad-ac6b-f8fb6702ca89" width="600" />
  </p>
  <p align="center">
  <b>Problem:</b> Loading multiple high-resolution photos from Firebase Storage caused <code>OutOfMemory</code> crashes and laggy scrolling.<br>
  <b>Solution:</b> Integrated the <b>Glide Library</b>. Glide handles asynchronous loading, smart disk caching, and automatic image downsampling, ensuring the app remains fast and stable even with heavy media content.
  </p>
</details>

------------------------------------------------------------------------

## 🛡 License

This project was developed as part of an academic course submission.
