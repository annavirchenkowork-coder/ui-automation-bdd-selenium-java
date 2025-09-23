# UI Automation BDD Framework (Java + Selenium + Cucumber)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Maven](https://img.shields.io/badge/Maven-3.9+-brightgreen.svg)](https://maven.apache.org/)
[![Selenium](https://img.shields.io/badge/Selenium-WebDriver-green.svg)](https://www.selenium.dev/)
[![Cucumber](https://img.shields.io/badge/Cucumber-BDD-orange.svg)](https://cucumber.io/)
[![JUnit](https://img.shields.io/badge/JUnit-5-red.svg)](https://junit.org/junit5/)
[![CI/CD](https://img.shields.io/badge/GitHub-Actions-blue.svg)](https://docs.github.com/en/actions)

🚀 A **Behavior-Driven Development (BDD)** style test automation framework built with **Java, Selenium WebDriver, Cucumber, JUnit 5**, and **GitHub Actions CI/CD**.

This framework demonstrates **Page Object Modeling**, **reusable utilities**, and **feature-driven testing** with rich reporting.

---

## 📂 Project Structure
```
src
├── main
│   └── java
│        └── util
│             ├── Driver.java              # WebDriver manager (singleton)
│             ├── ConfigurationReader.java # Reads config.properties
│             └── BrowserUtil.java         # Common Selenium utilities
│
└── test
├── java
│    ├── pages   # Page Object Models
│    ├── runner  # Test runners
│    └── steps   # Step definitions
│
└── resources
├── features            # Gherkin feature files
│    ├── login_saucedemo.feature
│    ├── sort_products_saucedemo.feature
│    ├── books_cart.feature   # nopCommerce add-to-cart feature
│
├── config.properties
└── junit-platform.properties
```
---

## ⚙️ Tech Stack
- **Language:** Java 17
- **Build Tool:** Maven
- **UI Testing:** Selenium WebDriver
- **BDD:** Cucumber (Gherkin syntax)
- **Assertions:** JUnit 5
- **CI/CD:** GitHub Actions

---

## 📝 Features
**Some of the currently automated flows:**
- 🔐 **SauceDemo Login & Sorting**
- 🛒 **nopCommerce Books – Add to Cart & Cart Validation** 

**Upcoming features (in progress 🚧):**
- 🏥 **CURA Appointment Booking**
- 🏦 **Parabank Login & Money Transfer**


## ▶️ How to Run

Run tests from the terminal:
```bash 
    mvn clean test
```
Run with tags (e.g., smoke, regression):
```bash
    mvn clean test -Psmoke
    mvn clean test -Pregression
```
Run dry-run mode (step binding check only):
```bash
    mvn clean test -Pdry-run
```
Re-run failed scenarios:
```bash
    mvn clean test -Prerun
```
Or run via Cucumber runner inside your IDE.

---
## 📊 Test Reports

After running the tests, reports are generated under the `target/` folder:

- **HTML Report:** `target/cucumber-report.html`  
- **JSON Report (for integrations):** `target/cucumber-report.json`  
- **JUnit XML Report (for CI/CD):** `target/cucumber-report.xml`
  
---

## 📌 Future Improvements

- Add API testing with Rest-Assured
- Add DB validation with JDBC
- Expand CI pipeline (parallel execution, richer reporting)

---

## 👩‍💻 About Me

Hi, I’m **Anna (Netta) Virchenko** – QA Engineer in Test.
I’m passionate about building reliable automation frameworks and continuously improving software quality.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Profile-blue)](https://www.linkedin.com/in/anna-virchenko-work)
[![GitHub](https://img.shields.io/badge/GitHub-Profile-black)](https://github.com/annavirchenkowork-coder)


