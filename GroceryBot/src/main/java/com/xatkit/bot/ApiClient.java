package com.xatkit.bot;

import com.fasterxml.jackson.core.util.BufferRecycler;

import com.xatkit.core.XatkitBot;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.io.ReactEventProvider;
import com.xatkit.plugins.react.platform.io.ReactIntentProvider;
import lombok.val;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;

import static com.xatkit.dsl.DSL.eventIs;
import static com.xatkit.dsl.DSL.fallbackState;
import static com.xatkit.dsl.DSL.intent;
import static com.xatkit.dsl.DSL.intentIs;
import static com.xatkit.dsl.DSL.model;
import static com.xatkit.dsl.DSL.state;
import static com.xatkit.dsl.DSL.country;
import static com.xatkit.dsl.DSL.any;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.io.ReactEventProvider;
import com.xatkit.plugins.react.platform.io.ReactIntentProvider;
import lombok.val;
import org.json.JSONArray;
import org.json.*;

import com.mashape.unirest.http.Unirest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.xatkit.dsl.DSL.intent;
import static com.xatkit.dsl.DSL.state;

public class ApiClient {

    static String retrieveKey() throws IOException {
        String key = "";
        InputStream inputStream = GroceryBot.class.getResourceAsStream("/application.properties");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while(reader.ready()) {
            key = reader.readLine();
        }
        return key;
    }


    public String fetchDataFromApi(String apiUrl, String intentValue, String entityValue, String key) {



        try {
            HttpResponse<String> response = Unirest.get(apiUrl + entityValue)
                    .header("Authorization", "Bearer " + key)
                    .header("Accept", "application/json")
                    .asString();

            if (response.getStatus() == 200){
                JSONObject responseBody = new JSONObject(response.getBody());
                return responseBody.toString();
            } else {
                throw new RuntimeException("API request failed with status code: " + response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("API request failed.", e);
        }
        /**
         * Call on the methods for later...
         * String apiUrl = "https://kassal.app/api/v1/products?search=" + product;
         * String key = retrieveKey(); // Assuming the retrieveKey() method is returning the key value
         * String apiResponse = fetchDataFromApi(apiUrl, key);
         */

    }


}






