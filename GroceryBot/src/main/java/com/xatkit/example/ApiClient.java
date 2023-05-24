package com.xatkit.example;

import com.fasterxml.jackson.core.util.BufferRecycler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.xatkit.example.RetrieveKey;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mashape.unirest.http.Unirest;
import java.io.IOException;

/**
 * Class for making API requests and retrieving JSON data
 * Find better package placement later
 */
public class ApiClient {

    public static String fetchDataFromApi(String apiUrl, String product, String key) {

        try {
            HttpResponse<String> response = Unirest
                    .get(apiUrl + product + "&size=50")
                    .header("Authorization", "Bearer " + key)
                    .header("Accept", "application/json")
                    .asString();

            if (response.getStatus() == 200) {
                System.out.println("yas response queen");
                JSONObject jsonObject = new JSONObject(response.getBody());
                JSONArray dataArray = jsonObject.getJSONArray("data");
                String productName = dataArray.getJSONObject(0).getString("name");
                double currentPrice = dataArray.getJSONObject(0).getDouble("current_price");

                return "The price of " + productName + " is currently " + currentPrice;
            } else if (response.getStatus() == 400) {
                return "Oops, I couldnt find the price of the product.";
            } else {
                return "Sorry, an error occured " + response.getStatus();
            }

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return "";

    }

}
