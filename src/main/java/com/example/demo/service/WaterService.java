package com.example.demo.service;

import com.example.demo.dto.CatchResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WaterService {

    @Cacheable(value = "waterCache", key = "#city")
    public List<Element> getWaterConditions(String city) throws IOException {
        String url = switch (city) {
            case "Sremska Mitrovica" -> "https://www.hidmet.gov.rs/latin/hidrologija/izvestajne/prognoza.php?hm_id=45090";
            case "Belgrade" -> "https://www.hidmet.gov.rs/latin/hidrologija/izvestajne/prognoza.php?hm_id=45099";
            case "Novi Sad" -> "https://www.hidmet.gov.rs/latin/hidrologija/izvestajne/prognoza.php?hm_id=42035";
            default -> throw new IllegalArgumentException("Unsupported city");
        };

        Document doc = Jsoup.connect(url).get();
        List<Element> elements = new ArrayList<>();
        elements.add(doc.select("td:has(img[src*='nivo.gif'])").first());    // Index 0: Level
        elements.add(doc.select("td:has(img[src*='promena.gif'])").first()); // Index 1: Change
        elements.add(doc.select("td:has(img[src*='protok.gif'])").first());  // Index 2: Flow
        elements.add(doc.select("td:has(img[src*='temp.gif'])").first());    // Index 3: Temp

        // Add future levels (Indices 4, 5, 6)
        Element futureRoot = doc.select("td.siva75.levo:matchesOwn(\\s*Vodostaj\\s*\\(cm\\):)").first();
        if (futureRoot != null) {
            Element f1 = futureRoot.nextElementSibling();
            elements.add(f1);
            elements.add(f1.nextElementSibling());
            elements.add(f1.nextElementSibling().nextElementSibling());
        }
        return elements;
    }

    public CatchResponse.WaterDto mapToDto(List<Element> elements) {
        return CatchResponse.WaterDto.builder()
                .height(elements.get(0).text())
                .flow(elements.get(2).text())
                .temperature(elements.get(3).text())
                .build();
    }
}