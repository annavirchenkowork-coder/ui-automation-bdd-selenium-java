# UI Automation BDD Framework (Java + Selenium + Cucumber)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Maven](https://img.shields.io/badge/Maven-3.9+-brightgreen.svg)](https://maven.apache.org/)
[![Selenium](https://img.shields.io/badge/Selenium-WebDriver-green.svg)](https://www.selenium.dev/)
[![Cucumber](https://img.shields.io/badge/Cucumber-BDD-orange.svg)](https://cucumber.io/)
[![JUnit](https://img.shields.io/badge/JUnit-5-red.svg)](https://junit.org/junit5/)
[![CI/CD](https://img.shields.io/badge/GitHub-Actions-blue.svg)](https://docs.github.com/en/actions)

🚀 A Behavior-Driven Development (BDD) style test automation framework built with **Java, Selenium WebDriver, Cucumber, JUnit**, and **GitHub Actions CI/CD**.  

This framework is designed for practicing and demonstrating professional QA engineering skills, including page object modeling, reusable utilities, and feature-file driven testing.

---

## 📂 Project Structure
```
src
├── main
│    └── java
│         └── util
│              ├── Driver.java              # WebDriver manager (singleton)
│              ├── ConfigurationReader.java # Reads config.properties
│              └── BrowserUtil.java         # Common Selenium utilities
│
└── test
├── java
│    ├── pages   # Page Object Models
│    ├── runner  # Test runners
│    └── steps   # Step definitions
│
└── resources
└── features # Gherkin feature files
```
---

## ⚙️ Tech Stack
- **Language:** Java 17  
- **Build Tool:** Maven  
- **UI Testing:** Selenium WebDriver  
- **BDD:** Cucumber (Gherkin syntax)  
- **Assertions:** JUnit  
- **CI/CD:** GitHub Actions  

---

## 📝 Sample Feature (Login)
```gherkin
Feature: Login

  Scenario: Successful login with valid credentials
    Given the user is on the login page
    When the user enters valid credentials
    Then the user should see the dashboard
```

▶️ How to Run

Run tests from the terminal:

`mvn clean test`

Or run via Cucumber runner inside your IDE.

---
## 📊 Test Reports

After running the tests, reports are generated under the `target/` folder:

- **HTML Report:** `target/cucumber-report.html`  
- **JSON Report (for integrations):** `target/cucumber-report.json`  
- **JUnit XML Report (for CI/CD):** `target/cucumber-report.xml`
  
---

📌 Future Improvements

- Add reporting (Extent / Allure)  
- Add API testing layer with Rest-Assured  
- Add DB validation with JDBC  
- Expand CI pipeline (parallel execution, test reports)  

---

👩‍💻 About Me

Hi, I’m **Anna (Netta) Virchenko** – QA Engineer in Test.
I’m passionate about building reliable automation frameworks and continuously improving software quality.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Profile-blue)](https://www.linkedin.com/in/anna-virchenko-work)
[![GitHub](https://img.shields.io/badge/GitHub-Profile-black)](https://github.com/annavirchenkowork-coder)


