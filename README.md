# 📱 Personal Finance Manager app

A mobile Android application developed as part of the ENCS5150 course project at Birzeit University.  
The app helps users manage their personal finances by tracking income, expenses, budgets, and financial summaries.

---

## 🚀 Features

### 🔐 Authentication
- User Signup and Login
- Input validation (email, password rules)
- "Remember Me" functionality
- Session management using SharedPreferences

### 💰 Transactions Management
- Add Income and Expenses
- Edit and Delete transactions
- Categorize transactions
- Store all data locally using SQLite

### 📊 Dashboard (Home)
- View total income, expenses, and balance
- Filter by:
  - Day
  - Week
  - Month
  - Custom range
- Pie Chart (Income vs Expenses)
- Bar Chart (Expenses by Category)

### 📂 Categories
- Default categories (Food, Rent, Salary, etc.)
- User-specific categories

### 🎯 Budgets & Goals
- Set monthly budget per category
- Track spending vs limit
- Prevent overspending

### ⚙️ Settings
- Light/Dark mode
- Default time period selection
- Manage categories

### 👤 Profile
- Update first and last name
- Change password with validation

---

## 🏗️ Project Structure

    edu.birzeit.courseproject
    │
    ├── activities
    │   ├── LoginActivity
    │   ├── SignupActivity
    │   └── MainDrawerActivity
    │
├── fragments
│   ├── HomeFragment
│   ├── IncomeFragment
│   ├── ExpensesFragment
│   ├── BudgetsGoalsFragment
│   ├── SettingsFragment
│   └── ProfileFragment
│
├── data
│   ├── DBHelper
│   ├── UserRepo
│   ├── TransactionRepo
│   ├── CategoryRepo
│   └── BudgetRepo
│
├── utils
│   ├── PrefManager
│   └── validators
---

## 🗄️ Database Design (SQLite)

The app uses a local SQLite database (`pfm.db`) with the following tables:

### users
- email (PK)
- first_name
- last_name
- password

### transactions
- id (PK)
- user_email
- type (INCOME / EXPENSE)
- category
- amount
- note
- date

### categories
- id (PK)
- user_email
- type
- name

### budgets
- id (PK)
- user_email
- category
- monthly_limit

---

## 🧠 Technologies Used

- Java
- Android SDK
- SQLite (Local Database)
- SharedPreferences
- MPAndroidChart (Charts)
- Material Design Components

---

## 🔄 Data Handling

- All financial data is stored locally using SQLite
- User session and settings are managed using SharedPreferences
- Data is filtered and aggregated using SQL queries

---
