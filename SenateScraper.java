package com.scraper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class SenateScraper {

    //this is set up base url
    private static final String BASE_URL = "https://akleg.gov/senate.php";
    private static final int DELAY_MS = 850;

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();

        //here i configured chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        //here in this part i initialized chrome driver
        WebDriver driver = new ChromeDriver(options);
        
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        List<Map<String, String>> senators = new ArrayList<>();

        //i here used try finally block to ensure proper resource management
        try {
            driver.get(BASE_URL);
            pause(DELAY_MS);

            List<WebElement> anchors = driver.findElements(By.cssSelector("a[href]"));
            Set<String> profileLinks = extractProfileLinks(anchors);

            if (profileLinks.isEmpty()) {
                profileLinks.addAll(findFallbackLinks(driver));
            }

            for (String url : profileLinks) {
                try {
                    driver.navigate().to(url);
                    pause(DELAY_MS);

                    Map<String, String> record = new LinkedHashMap<>();
                    record.put("url", url);

                    String name = findElementText(driver, Arrays.asList("h1", "h2", "title"));
                    record.put("name", name);

                    String pageText = driver.findElement(By.tagName("body")).getText();

                    record.put("title", findLabelValue(pageText, "Title"));
                    record.put("party", findLabelValue(pageText, "Party"));
                    record.put("profile", findLabelValue(pageText, "Address"));
                    record.put("dob", "");
                    record.put("type", findLabelValue(pageText, "Position"));
                    record.put("country", "United States");
                    record.put("otherinfo",
                            (findLabelValue(pageText, "Phone") + " " + findEmail(pageText)).trim());

                    senators.add(record);
                } catch (Exception ex) {
                    System.err.println("Skipping " + url + " due to: " + ex.getMessage());
                }
            }

            saveAsJson(senators);

        } finally {
            driver.quit();
        }
    }

    private static void pause(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private static Set<String> extractProfileLinks(List<WebElement> anchors) {
        //Here i used java set interface
        Set<String> links = new LinkedHashSet<>();
        for (WebElement a : anchors) {
            try {
                String href = a.getAttribute("href");
                String text = a.getText();
                if (href == null || text == null) continue;

                String lowerHref = href.toLowerCase();
                String lowerText = text.toLowerCase();

                boolean match = lowerHref.contains("akleg.gov")
                        && (lowerHref.contains("senate") || lowerHref.contains("/member") || lowerText.contains("senator"))
                        && !href.equals(BASE_URL);

                if (match) links.add(href);
            } catch (StaleElementReferenceException ignored) {}
        }
        return links;
    }

    private static Set<String> findFallbackLinks(WebDriver driver) {
        //in this part i used set interface of java
        Set<String> links = new LinkedHashSet<>();
        try {
            List<WebElement> extras = driver.findElements(By.cssSelector("main a, #content a, .list a, ul a"));
            for (WebElement e : extras) {
                String href = e.getAttribute("href");
                if (href != null && href.contains("akleg.gov")) links.add(href);
            }
        } catch (Exception ignored) {}
        return links;
    }

    private static String findElementText(WebDriver driver, List<String> selectors) {
        for (String sel : selectors) {
            try {
                WebElement el = driver.findElement(By.cssSelector(sel));
                if (el != null && !el.getText().trim().isEmpty()) {
                    return el.getText().trim();
                }
            } catch (Exception ignored) {}
        }
        return "";
    }

    private static String findLabelValue(String text, String label) {
        if (text == null) return "";
        String[] lines = text.split("\\r?\\n");
        String lowerLabel = label.toLowerCase();

        for (String line : lines) {
            String l = line.toLowerCase();
            if (l.startsWith(lowerLabel + ":") || l.contains(lowerLabel + ":")) {
                int idx = line.indexOf(":");
                if (idx >= 0 && idx + 1 < line.length()) {
                    return line.substring(idx + 1).trim();
                }
            } else if (l.startsWith(lowerLabel)) {
                return line.substring(lowerLabel.length()).trim();
            }
        }
        return "";
    }

    private static String findEmail(String text) {
        if (text == null) return "";
        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
            java.util.regex.Matcher matcher = pattern.matcher(text);
            if (matcher.find()) return matcher.group();
        } catch (Exception ignored) {}
        return "";
    }

    private static void saveAsJson(List<Map<String, String>> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("output.json")) {
            gson.toJson(data, writer);
            System.out.println("output.json created successfully with " + data.size() + " records.");
        } catch (IOException e) {
            System.err.println("Error writing output.json: " + e.getMessage());
        }
    }
}
