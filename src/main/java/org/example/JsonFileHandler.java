package org.example;

import org.apache.commons.io.input.BOMInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collector;

public class JsonFileHandler {
    private String cityDeparture;
    private String cityArriving;
    private JSONArray json;

    public JsonFileHandler() {
        cityArriving = "Тель-Авив";
        cityDeparture = "Владивосток";
    }

    public JsonFileHandler(String cityDeparture, String cityArriving) {
        this.cityDeparture = cityDeparture;
        this.cityArriving = cityArriving;
    }

    public String getCityDeparture() {
        return cityDeparture;
    }

    public void setCityDeparture(String cityDeparture) {
        this.cityDeparture = cityDeparture;
    }

    public String getCityArriving() {
        return cityArriving;
    }

    public void setCityArriving(String cityArriving) {
        this.cityArriving = cityArriving;
    }

    public JSONArray getJson() {
        return json;
    }

    public void setJson(JSONArray json) {
        this.json = json;
    }

    public JSONArray getTicketsFromFile(String fileName) throws IOException, ParseException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        BOMInputStream bomIn = new BOMInputStream(fileInputStream);
        InputStreamReader reader = new InputStreamReader(bomIn, "UTF-8");
        Object o = new JSONParser().parse(reader);
        JSONObject j = (JSONObject) o;
        return (JSONArray) j.get("tickets");
    }

    public JSONArray filterTicketsByCities(JSONArray jsonArray) {
        return (JSONArray) jsonArray.stream().map(x -> (JSONObject) x)
                .filter(x -> ((JSONObject) x).containsValue(cityArriving))
                .filter(x -> ((JSONObject) x).containsValue(cityDeparture))
                .collect(Collector.of(
                        JSONArray::new,
                        JSONArray::add,
                        (ja1, ja2) -> {
                            for (final Object s : ja2) {
                                ja1.add(s);
                            }
                            return ja1;
                        }));
    }


    public void showTimeBetweenCities(JSONArray filteredJson) {
        HashMap<String, Duration> timeForTrip = new HashMap<>();
        for (Object object : filteredJson) {
            JSONObject json = (JSONObject) object;
            String carrier = json.get("carrier").toString();
            LocalDateTime departure = LocalDateTime.parse(json.get("departure_date") +
                    " " + json.get("departure_time"), DateTimeFormatter.ofPattern("dd.MM.yy H:mm"));
            LocalDateTime arrival = LocalDateTime.parse(json.get("arrival_date") +
                    " " + json.get("arrival_time"), DateTimeFormatter.ofPattern("dd.MM.yy H:mm"));
            Duration duration = Duration.between(departure, arrival);
            if (timeForTrip.containsKey(carrier) && timeForTrip.get(carrier).compareTo(duration) > 0) {
                timeForTrip.replace(carrier, duration);
            }
            if (!timeForTrip.containsKey(carrier)) {
                timeForTrip.put(carrier, duration);
            }
        }
        timeForTrip.forEach((k, v) -> System.out.println(k + " " + v));
    }

    public void showDifferentBetweenAvgAndMedian(JSONArray filteredJson) {
        ArrayList<Long> prices = new ArrayList<>();
        for (Object object : filteredJson) {
            JSONObject json = (JSONObject) object;
            prices.add((Long) json.get("price"));
        }
        Collections.sort(prices);
        double median;
        if (prices.size() % 2 == 0) {
            double firstPart = (prices.get(prices.size() / 2));
            double secondPart = prices.get((prices.size() / 2) - 1);
            median = (firstPart + secondPart) / 2;
        } else {
            median = prices.get((prices.size() / 2));
        }
        double avg = prices.stream().mapToLong(Long::longValue).average().orElse(0);
        System.out.println("different between avg and median = " + (avg - median));
    }
}
