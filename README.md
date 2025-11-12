# 🚀 QuantumLeap E-Commerce Test Automation

> **End-to-End Test Automation Framework** for Web + API layers of a modern e-commerce system.  
> Built using **Selenium, TestNG, Cucumber, REST Assured**, and **Extent Reports**,  
> showcasing a complete **SDET workflow** on [SauceDemo](https://www.saucedemo.com) & [ReqRes](https://reqres.in).

---

## 🌐 Project Overview

**QuantumLeap** validates the complete e-commerce journey — from UI flows to API integration.

| Area | Description |
|------|--------------|
| 🧩 **UI Functional Testing** | Automates Login, Add to Cart, Checkout, and Purchase Flow on SauceDemo |
| 🔗 **API Testing** | Validates CRUD operations on ReqRes API |
| 🧠 **BDD Integration** | Implements “Add to Cart” scenario using Cucumber (Gherkin syntax) |
| 📊 **Reporting** | HTML Reports + Screenshots (Extent Reports) |
| ⚙️ **Non-Functional** | Performance & Security Testing documentation |

---

## 🧰 Tech Stack

| Category | Tools / Frameworks |
|-----------|--------------------|
| **Language** | Java |
| **Build Tool** | Maven |
| **UI Automation** | Selenium WebDriver |
| **Test Framework** | TestNG |
| **BDD Framework** | Cucumber |
| **API Testing** | REST Assured |
| **Reporting** | Extent Reports |
| **Design Pattern** | Page Object Model (POM) |
| **Version Control** | Git & GitHub |

---

## 🌟 Key Features

### 🔹 UI Tests (SauceDemo)
- Data-driven login using TestNG `@DataProvider`
- End-to-End Purchase Flow  
  → *Login → Add Item → Verify Cart → Checkout → Confirm Order*  
- Explicit waits, Screenshot Capture, and JS Executor usage  
- Modular POM structure for reusability

### 🔹 BDD (Cucumber + TestNG)
- “Add to Cart” feature written in **Gherkin**
- Step Definitions reuse POM methods
- TestRunner integrated with TestNG XML

### 🔹 API Tests (ReqRes)
- **GET** → Fetch User List  
- **POST** → Create User  
- **PUT** → Update User  
- Validates Status Codes, Response Schema, and Body Assertions

### 🔹 Reports
- Auto-generated HTML Report (Extent Reports)
- Embedded Screenshots for failed UI tests

### 🔹 Non-Functional Docs
- Performance Testing Plan for Login Flow  
- Security Scenarios: *Authentication Bypass, XSS Validation*

---

## ⚙️ How to Run Tests

### 🔧 Prerequisites
- Java **JDK 11+**
- Maven **3+**
- Chrome Browser (WebDriver auto-managed)
- IDE with TestNG Plugin *(optional)*

### ▶️ Steps to Run

```bash
# Clone the Repository
git clone https://github.com/AyanKumarDash01/QuantumLeap.git
cd QuantumLeap-Automation

# Run All Tests
mvn clean test

# Or Run Specific TestNG Suite
mvn test -DsuiteXmlFile=testng.xml
