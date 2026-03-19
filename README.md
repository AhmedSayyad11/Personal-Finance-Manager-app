# 📱 Personal Finance Manager

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
