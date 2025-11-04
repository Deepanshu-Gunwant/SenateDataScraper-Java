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
This will generate an output.json file in the project root directory.

---

## 🧩 Detailed Notes — Approach, Assumptions, and Limitations

### 🧠 Approach
- Used **Selenium WebDriver (Java)** because it reliably renders JavaScript-driven pages and supports robust navigation to detail pages.  
- **WebDriverManager** is used to auto-download and manage ChromeDriver, so no manual setup is required.  
- **Gson** is used to serialize the list of senator objects into a pretty-printed JSON array.  
- The scraper first fetches the main listing, extracts each senator's link (or element), then navigates to detail pages where available to obtain full contact details.  
- Implemented defensive coding practices: whenever a field is missing, the scraper inserts an empty string and continues execution gracefully.

---

### ⚙️ Assumptions
- The target website (`https://akleg.gov/senate.php`) is primarily HTML-driven, with links or cards for each senator and properly labeled contact fields.  
- **Phone**, **Email**, and **Address** information may appear on separate detail pages — the scraper handles both inline and linked cases.  
- The program assumes Chrome is installed and accessible through the system PATH (handled automatically by WebDriverManager).  

---

### ⚠️ Limitations
- If the site uses heavy **anti-bot protections** (e.g., CAPTCHA or rate limits), the scraper may be temporarily blocked. Polite delays are included to minimize such issues.  
- Field extraction relies on common label patterns (e.g., text containing "Phone" or "Email"). If the site structure changes drastically, minor selector adjustments may be required.  
- The scraper does not perform advanced validation (e.g., phone format normalization); data is stored as scraped for authenticity.  

---

### 🚀 Potential Improvements
- Add configurable rate-limiting, proxy rotation, and retry/backoff logic.  
- Use **Playwright (Java)** as an alternative for faster headless scraping and enhanced automation control.  
- Integrate **unit and integration tests** to ensure future maintainability.  
- Provide a CLI interface to allow user-defined parameters (e.g., output filename, headless toggle, timeout duration).

