# 🩺 Smart Symptom Checker

A full-stack Java (Spring Boot) + HTML/CSS/JS web application that helps users identify possible health conditions based on their symptoms.

## 🚀 Features
- ✅ Select from 30+ symptoms (clickable tags)
- ✅ Add custom symptoms
- ✅ Patient info collection (name, age, gender, duration)
- ✅ Severity assessment (Mild / Moderate / Severe)
- ✅ Smart condition matching engine (15 conditions)
- ✅ Instant health recommendations
- ✅ Check history (last 10 checks)
- ✅ Stats dashboard with bar charts
- ✅ Works in demo mode even without backend
- ✅ H2 in-memory database (no setup needed)
- ✅ CORS enabled for local dev

---

## 📁 Project Structure

```
smart-symptom-checker/
│
├── backend/                          ← Spring Boot Java Backend
│   ├── pom.xml
│   └── src/main/java/com/symptomchecker/
│       ├── SmartSymptomCheckerApplication.java
│       ├── controller/
│       │   └── SymptomController.java
│       ├── model/
│       │   ├── SymptomCheck.java
│       │   └── SymptomRequest.java
│       ├── repository/
│       │   └── SymptomCheckRepository.java
│       └── service/
│           └── SymptomService.java
│
├── frontend/src/main/resources/static/   ← Frontend (HTML/CSS/JS)
│   ├── index.html
│   ├── css/style.css
│   └── js/app.js
│
└── README.md
```

---

## 🛠️ Tools You Need to Install

### 1. Java 17+
- Download from: https://www.oracle.com/java/technologies/downloads/#java17
- Or use OpenJDK: https://adoptium.net/
- **Verify:** `java -version`

### 2. Maven 3.8+
- Download from: https://maven.apache.org/download.cgi
- Extract and add to PATH
- **Verify:** `mvn -version`

### 3. VS Code
- Download from: https://code.visualstudio.com/
- **Extensions to install in VS Code:**
  - Extension Pack for Java (Microsoft)
  - Spring Boot Extension Pack
  - Live Server (for frontend preview)

### 4. Git (for GitHub upload)
- Download from: https://git-scm.com/downloads
- **Verify:** `git --version`

---

## ▶️ How to Run

### Step 1: Open in VS Code
```bash
code smart-symptom-checker
```

### Step 2: Run the Backend
```bash
cd backend
mvn spring-boot:run
```
Backend starts at: **http://localhost:8080**

### Step 3: Open the Frontend
- Open `frontend/src/main/resources/static/index.html` in VS Code
- Right-click → **"Open with Live Server"**
- OR simply open the file directly in your browser

---



| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/check | Analyze symptoms |
| GET | /api/history | Get recent checks |
| GET | /api/symptoms | Get all symptoms list |
| GET | /api/stats | Get statistics |
| GET | /api/health | Health check |



