# 🚀 QuantumLeap E-Commerce Test Automation Framework  

![Build Status](https://img.shields.io/badge/build-passing-brightgreen?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-3%2B-red?style=flat-square&logo=apachemaven)
![Selenium](https://img.shields.io/badge/Selenium-WebDriver-43B02A?style=flat-square&logo=selenium)
![TestNG](https://img.shields.io/badge/TestNG-Automation-blueviolet?style=flat-square)
![Cucumber](https://img.shields.io/badge/Cucumber-BDD-23D96C?style=flat-square&logo=cucumber)
![REST Assured](https://img.shields.io/badge/REST%20Assured-API%20Testing-2C8EBB?style=flat-square)
![Extent Reports](https://img.shields.io/badge/Extent%20Reports-Reporting-blue?style=flat-square)

---

### 🎯 Industry-Standard E-Commerce Test Automation Framework  

**QuantumLeap** is a production-grade automation framework built to validate both **UI and API layers** of modern e-commerce platforms.  
Designed following **SDET best practices**, it integrates multiple test types (UI, API, BDD) with **professional reporting, documentation, and CI/CD readiness.**

---

## 📋 Overview  

QuantumLeap demonstrates **enterprise-level automation capabilities** using [SauceDemo](https://www.saucedemo.com) and [ReqRes](https://reqres.in).

| Type | Description |
|------|--------------|
| 🌐 **UI Automation** | Login, Cart, Checkout workflows using Selenium + TestNG |
| 🔗 **API Automation** | CRUD operations on ReqRes API with REST Assured |
| 🧩 **BDD** | Add-to-cart scenario using Cucumber (Gherkin syntax) |
| 📊 **Reporting** | HTML reports with screenshots (Extent Reports) |
| ⚡ **Non-Functional** | Performance & Security test plans |

---

## ✨ What Makes QuantumLeap Special  

🏗️ **End-to-End Coverage** – Full automation of web + API layers  
📊 **Professional Reports** – Extent Reports with screenshots and analytics  
⚙️ **Scalable Architecture** – POM design + modular structure  
🔁 **CI/CD Ready** – GitHub Actions compatible  
📘 **Comprehensive Docs** – Performance & Security plans included  

---

## 🧠 Core Framework Features  

| Feature | Technology | Status |
|----------|-------------|---------|
| 🌐 UI Automation | Selenium WebDriver + Page Object Model | ✅ Complete |
| 📝 BDD Testing | Cucumber (Gherkin Scenarios) | ✅ Complete |
| 🔗 API Automation | REST Assured + JSON Validation | ✅ Complete |
| 📊 Reporting | Extent Reports + Screenshot Integration | ✅ Complete |
| 🧪 Performance Plan | Login Load Test Strategy | ✅ Documented |
| 🔄 CI/CD Pipeline | GitHub Actions | 🧩 Planned |

---

## 🧰 Tech Stack  

| Category | Tools / Frameworks |
|-----------|--------------------|
| **Language** | java |
| **Build Tool** | Maven |
| **UI Testing** | Selenium WebDriver |
| **Test Framework** | TestNG |
| **BDD** | Cucumber |
| **API Testing** | REST Assured |
| **Reporting** | Extent Reports |
| **Design Pattern** | Page Object Model (POM) |
| **Version Control** | Git & GitHub |

---

## ⚙️ Installation & Setup  

### 🧾 Prerequisites  

- Java JDK 11+  
- Maven 3.6+  
- Git (latest)  
- Chrome Browser (WebDriver auto-managed)

### 📦 Setup Commands  

```bash
# Clone Repository
git clone https://github.com/AyanKumarDash01/QuantumLeap.git
cd QuantumLeap-Automation

# Install Dependencies
mvn clean install

# Run All Tests
mvn clean test

# Run Specific TestNG Suite
mvn test -DsuiteXmlFile=testng.xml
