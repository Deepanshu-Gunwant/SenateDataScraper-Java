# Senate Data Scraper (Java / Selenium)

**Assignment:** Scrape data from `https://akleg.gov/senate.php` and produce a JSON file with fields:  
`Name, Title, Position, Party, Address, Phone, Email, URL`

---

## 🛠️ Technologies Used
- Java 17  
- Maven  
- Selenium WebDriver  
- WebDriverManager (automates ChromeDriver)  
- Gson (for JSON serialization)

---

## 📂 Files Included
- `pom.xml` — Maven dependencies  
- `src/main/java/com/scraper/SenateScraper.java` — main scraper program  
- `sample_output.json` — example output format  
- `README.md` — this file  
- `TIME_LOG.txt` — time spent on the task  

---

## ⚙️ How It Works (High Level)
1. Launches a headless Chrome browser using Selenium.  
2. Navigates to `https://akleg.gov/senate.php`.  
3. Finds the list of senator entries (links/cards).  
4. For each entry, visits its detail page (or extracts inline data) and collects:  
   - Name  
   - Title  
   - Position  
   - Party  
   - Address  
   - Phone  
   - Email  
   - URL  
5. Stores all collected entries into `output.json`.

---

## 🚀 Run Instructions (Linux / Windows)
1. Ensure **Java 17** and **Maven** are installed.  
2. Clone or unzip this project.  
3. From the project root, run:
   ```bash
   mvn clean package
   java -jar target/senate-scraper.jar
