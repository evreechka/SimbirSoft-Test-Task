package ru.mashka.services;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class PageService {
    private String  stringURL;
    public void setStringURL(String stringURL) {
        this.stringURL = stringURL;
    }

    public boolean isValidURL() {
        try {
            URL url = new URL(stringURL);
            url.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
           return false;
        }
        return true;
    }
    private String getWebsiteName() {
        String name = "";
        int start_index;
        if (stringURL.startsWith("https")) {
            start_index = 8;
        } else  {
            start_index = 7;
        }
        if (stringURL.contains("www.")) {
            start_index += 4;
        }
        for (int i = start_index; i < stringURL.length(); i++) {
            if (stringURL.charAt(i) != '.') {
                name += stringURL.charAt(i);
            } else
                break;
        }
        return name + ".html";
    }
    public String createFile() throws IOException {
        String fileContent = downloadHtmlPage();
        if (fileContent == null)
            return "Troubles with downloading html of the web-page! Try another link or check Wi-Fi connection";
        String fileName = getWebsiteName();
        String filePath = System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop" + System.getProperty("file.separator") + "savedPages" + System.getProperty("file.separator") + fileName;
        File newFile = new File(filePath);
        newFile.getParentFile().mkdirs();
        newFile.createNewFile();
        try(FileWriter fileWriter = new FileWriter(newFile)) {
            fileWriter.write(fileContent);
        } catch (IOException e) {
            return "Troubles with saving html page on computer. Check permission!";
        }
        return "OK";
    }
    private String downloadHtmlPage() {
        String pageHtml;
        try {
            pageHtml = Jsoup.connect(stringURL).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/33.0.1750.152 Safari/537.36").ignoreHttpErrors(true)
                    .get().html();
        } catch (IOException e) {
            return null;
        }
        return pageHtml;
    }
    private String downloadTextPage() {
        String pageText;
        try {
            pageText = Jsoup.connect(stringURL)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/33.0.1750.152 Safari/537.36").ignoreHttpErrors(true)
                    .get().text();
        } catch (IOException e) {
            return null;
        }
        return pageText;
    }
    public String printTextInfo() {
        String text = downloadTextPage();
        if (text == null)
            return "Troubles with downloading text from the web-page! Sorry :(";
        List<String> words = Arrays.stream(text.toUpperCase().split("([\\s\\d',?!\\\"/;:\\[\\]\\(\\)\\{\\}«»$%#&`\\|—\\-]*\\.*[\\s\\d',?!\\\"/;:\\[\\]\\(\\)\\{\\}«»$%#&`\\|—\\-]+)")).collect(Collectors.toList());
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).length() <= 1) {
                words.remove(i);
                i--;
            }
        }
        Map<String, Long> countWords = words.stream().collect(Collectors.toMap(Function.identity(), v -> 1L, Long::sum));
        for (String key : countWords.keySet()) {
            System.out.println(key + " - " + countWords.get(key));
        }
        return "OK";
    }
}
