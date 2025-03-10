package com.example.demo.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class WaterService {

    @Cacheable(value = "waterCache", key = "#city", unless = "#result == null")
    public List<Element> getWaterConditions(String city) throws IOException {
        return getWaterData(city);
    }

    public List<Element> getWaterData(String city) throws IOException {
        System.out.println("Scraping Water data for city: " + city);

        List<Element> waterConditions = new ArrayList<>();

        Document doc;

        switch (city) {
            case "Sremska Mitrovica" : doc = Jsoup.connect("https://www.hidmet.gov.rs/latin/hidrologija/izvestajne/prognoza.php?hm_id=45090").get();
                break;
            case "Belgrade" : doc = Jsoup.connect("https://www.hidmet.gov.rs/latin/hidrologija/izvestajne/prognoza.php?hm_id=45099").get();
                break;
            case "Novi Sad" : doc = Jsoup.connect("https://www.hidmet.gov.rs/latin/hidrologija/izvestajne/prognoza.php?hm_id=42035").get();
                break;
            default: return null;
        }

        Element level = doc.select("td:has(img[src='../../../repository/ikonice/interf/nivo.gif'])").first();
        waterConditions.add(level);

        Element levelChange = doc.select("td:has(img[src='../../../repository/ikonice/interf/promena.gif'])").first();
        waterConditions.add(levelChange);

        Element flow = doc.select("td:has(img[src='../../../repository/ikonice/interf/protok.gif'])").first();
        waterConditions.add(flow);

        Element temp = doc.select("td:has(img[src='../../../repository/ikonice/interf/temp.gif'])").first();
        waterConditions.add(temp);

        Element futureLevel1 = Objects.requireNonNull(doc.select("td.siva75.levo:matchesOwn(\\s*Vodostaj\\s*\\(cm\\):)").first()).nextElementSibling();
        waterConditions.add(futureLevel1);

        assert futureLevel1 != null;
        Element futureLevel2 = futureLevel1.nextElementSibling();
        waterConditions.add(futureLevel2);

        assert futureLevel2 != null;
        Element futureLevel3 = futureLevel2.nextElementSibling();
        waterConditions.add(futureLevel3);

        return waterConditions;
    }

    @CacheEvict(value = "waterCache", key = "#city")
    public void evictCache(String city) {
        System.out.println("Evicting water cache at " + LocalDateTime.now());
    }
}
