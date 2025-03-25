# TechStore - Dockerized Java App
>TechStore is a simple Java CLI application to manage product inventory and sales. This version is containerized using Docker for easy deployment and testing.

## 🔧 Prerequisites

- Java 21 (already compiled into the JAR)
- Docker installed and running
- (Optional) WSL if using on Windows

## 🔧 Build Docker Image
`docker build -t techstore-app .`

## 🚀 Run the App
### Basic Run (Temporary):
`docker run --rm -it techstore-app`

### 💾 To persist `data/`:
**Windows (CMD):**  
`docker run -v %cd%/data:/app/data --rm -it techstore-app`

**WSL/Linux/macOS (Bash):**  
`docker run -v "$PWD/data:/app/data" --rm -it techstore-app`

## 🧪 Running Tests

Unit tests for TechStore are written using **JUnit 5** and are located in the `/test` folder.

### 🛠 How to Run Tests (via IntelliJ):

#### IntelliJ IDEA
- Right-click the `test/` folder or any test class → `Run 'All Tests'`
- You will see test results in the IntelliJ Run tab

