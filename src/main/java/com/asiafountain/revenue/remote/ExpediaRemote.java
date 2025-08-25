package com.asiafountain.revenue.remote;

import com.asiafountain.revenue.config.ExpConfig;
import com.asiafountain.revenue.model.CurrencyModel;
import com.asiafountain.revenue.utils.Constants;
import com.asiafountain.revenue.utils.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

@Component
@Log4j2
public class ExpediaRemote {

    @Autowired
    private ExpConfig expConfig;

    @Autowired
    private RestTemplate restTemplate;
    
    public CurrencyModel fetchHotelRevenue(String bookingId) {
        CurrencyModel model = new CurrencyModel();
        try {
            JSONArray jsonArrayBooking = fetchBookingDetail(bookingId);
            JSONObject jsonBooking = jsonArrayBooking.getJSONObject(0);

            JSONArray jsonArrayRoom = jsonBooking.getJSONArray("rooms");
            JSONObject jsonRoom = jsonArrayRoom.getJSONObject(0);

            JSONObject jsonRate = jsonRoom.getJSONObject("rate");
            JSONObject jsonPricing = jsonRate.getJSONObject("pricing");
            JSONObject jsonTotal = jsonPricing.getJSONObject("totals");
            JSONObject jsonMarketingFee = jsonTotal.getJSONObject("marketing_fee");
            JSONObject jsonBillable = jsonMarketingFee.getJSONObject("billable_currency");

            String value = jsonBillable.getString("value");
            String currency = jsonBillable.getString("currency");

            double revenue = Double.parseDouble(value);
            model.setAmount(revenue);
            model.setCurrency(currency);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return model;
    }

    private JSONArray fetchBookingDetail(String bookingId) throws Exception {
        HashMap<String, Object> queryParams = new HashMap<>();
        queryParams.put("affiliate_reference_id", bookingId);
        queryParams.put("email", expConfig.getEmail());
        return getDataArray(Constants.URL_GET_ITINERARIES, queryParams);
    }

    private JSONArray getJsonArray(ResponseEntity<byte[]> responseEntity) throws Exception {
        HttpHeaders httpHeaders = responseEntity.getHeaders();
        String responseString = "";
        if (httpHeaders.containsKey("Content-Encoding") && httpHeaders.getFirst("Content-Encoding").equalsIgnoreCase("gzip")) {
            byte[] responseBytes = responseEntity.getBody();
            responseString = StringUtil.unzip(responseBytes);
        } else {
            if (responseEntity.getBody() != null) {
                responseString = new String(responseEntity.getBody());
            }
        }

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return new JSONArray(responseString);
        } else {
            log.error("Error:" + responseString);
            JSONObject jsonObject = new JSONObject(responseString);
            if (jsonObject.has("errorMessage")) {
                String errorMessage = jsonObject.getString("errorMessage");
                throw new Exception(errorMessage);
            }
        }

        throw new Exception("Server Internal Error");
    }

    public JSONArray getDataArray(String urlPath, HashMap<String, Object> queryParam) throws Exception {
        String accessToken = getAccessToken();

        String url = expConfig.getBaseUrl() + urlPath;
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url);
        queryParam.keySet().stream().forEach(key -> {
            Object value = queryParam.get(key);
            if (value instanceof ArrayList<?>) {
                ((ArrayList<?>) value).forEach(data -> {
                    urlBuilder.queryParam(key,  data.toString());
                });
            } else {
                urlBuilder.queryParam(key,  value.toString());
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        headers.add("Customer-Ip", "5.5.5.5");
        headers.add("Accept-Encoding", "gzip");
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> responseEntity = safeExchange(urlBuilder.toUriString(), HttpMethod.GET, requestEntity);
        return getJsonArray(responseEntity);
    }

    private String getAccessToken() {
        long timestamp = Instant.now().getEpochSecond();
        String signatureInput = expConfig.getApiKey() + expConfig.getSharedKey() + timestamp;
        String signature;
        try {
            signature = hashStringSHA512(signatureInput);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating signature", e);
        }

        return "EAN apikey=" + expConfig.getApiKey() + ",signature=" + signature + ",timestamp=" + timestamp;
    }

    private static String hashStringSHA512(String input) throws NoSuchAlgorithmException {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hashBytes = digest.digest(inputBytes);

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    private ResponseEntity<byte[]> safeExchange(String url, HttpMethod method, HttpEntity<?> entity) throws Exception {
        try {
            return restTemplate.exchange(url, method, entity, byte[].class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            byte[] responseBody = ex.getResponseBodyAsByteArray();
            HttpHeaders errorHeaders = ex.getResponseHeaders();

            log.error("HTTP Error Response ({}): {}", ex.getStatusCode(), ex.getMessage());
            log.info("Raw error response bytes (Base64): {}", Base64.getEncoder().encodeToString(responseBody));
            log.info("Error Response Headers: {}", errorHeaders);

            String errorJsonString;
            if (errorHeaders != null && "gzip".equalsIgnoreCase(errorHeaders.getFirst("Content-Encoding"))) {
                errorJsonString = StringUtil.unzip(responseBody);
            } else {
                errorJsonString = new String(responseBody, StandardCharsets.UTF_8);
            }

            log.error("Uncompressed error response: {}", errorJsonString);

            JSONObject errorJson = new JSONObject(errorJsonString);
            if (errorJson.has("errors")) {
                JSONArray errorsArray = errorJson.getJSONArray("errors");
                if (!errorsArray.isEmpty()) {
                    JSONObject errorItem = errorsArray.getJSONObject(0);
                    if (errorItem.has("message")) {
                        throw new Exception(errorItem.getString("message"));
                    }
                }
            }

            if (errorJson.has("message")) {
                throw new Exception(errorJson.getString("message"));
            }

            throw new Exception("Server Internal Error");
        }
    }
    
}
