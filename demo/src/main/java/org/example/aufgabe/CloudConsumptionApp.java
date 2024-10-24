package org.example.aufgabe;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudConsumptionApp {
    public static void main(String[] args) {
        Map<String, Object> dataset = getDataset();
        if (dataset != null) {
            List<Map<String, Object>> events = (List<Map<String, Object>>) dataset.get("events");
            Map<String, Object> consumptionData = calculateConsumption(events);
            postData(consumptionData);
        }
    }
    private static Map<String, Object> getDataset() {
        String urlString = "http://container1:8080/v1/dataset";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return parseJson(response.toString());
            } else {
                System.out.println("Error: Unable to fetch dataset. Status code: " + conn.getResponseCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static Map<String, Object> parseJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static Map<String, Object> calculateConsumption(List<Map<String, Object>> events) {
        Map<String, Object> returnMap = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();
        returnMap.put("result", results);
        Map<String, Map<String, Long>> sortedData = new HashMap<>();
        for (Map<String, Object> event : events) {
            String customerId = (String) event.get("customerId");
            String workloadId = (String) event.get("workloadId");
            long timestamp = ((Number) event.get("timestamp")).longValue();
            String eventType = (String) event.get("eventType");
            sortedData.putIfAbsent(customerId, new HashMap<>());
            sortedData.get(customerId).putIfAbsent(workloadId, 0L);
            long currentValue = sortedData.get(customerId).get(workloadId);
            if ("start".equals(eventType)) {
                currentValue -= timestamp;
            } else {
                currentValue += timestamp;
            }
            sortedData.get(customerId).put(workloadId, currentValue);
        }
        for (String customer : sortedData.keySet()) {
            long consumption = 0;
            for (long value : sortedData.get(customer).values()) {
                consumption += value;
            }
            Map<String, Object> result = new HashMap<>();
            result.put("customerId", customer);
            result.put("consumption", consumption);
            results.add(result);
        }
        return returnMap;
    }
    private static void postData(Map<String, Object> consumptionData) {
        String urlString = "http://container1:8080/v1/result";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(consumptionData);
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();
            System.out.println(conn.getResponseCode() + ", " + conn.getResponseMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}