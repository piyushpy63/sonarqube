# 🛡️ Bad Notes App (DevSecOps Lab)

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Docker](https://img.shields.io/badge/Docker-Container-blue?style=for-the-badge&logo=docker)
![SonarCloud](https://img.shields.io/badge/SonarCloud-Quality%20Gate-success?style=for-the-badge&logo=sonarcloud)
![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF?style=for-the-badge&logo=github-actions)

---

## 📖 About The Project

This project simulates a legacy Java application with poor coding practices. It serves as a "Target Practice" environment to implement a robust **DevSecOps Pipeline**.

The goal is not to write the perfect app, but to build the perfect **Defense System** around it using:
* **SAST (Static Application Security Testing):** SonarCloud
* **Container Security:** Trivy Image Scanner
* **CI/CD Automation:** GitHub Actions
* **Quality Gates:** Automated build failure policies

---

## 🏗️ The DevSecOps Pipeline

The pipeline is designed to "Fail Fast." If any security threshold is breached, the build stops immediately.

```mermaid
graph TD;
    A[Push Code] --> B{Build Java};
    B -->|Success| C[SonarCloud Scan];
    C -->|Quality Gate Pass| D[Build Docker Image];
    C -->|Quality Gate Fail| X[❌ Fail Pipeline];
    D -->|Crit Vuln Found| X;
    D -->|Clean| F[Push to DockerHub];
