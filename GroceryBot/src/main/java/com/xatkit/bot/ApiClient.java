package com.xatkit.bot;

import com.fasterxml.jackson.core.util.BufferRecycler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Class  for making API requests and retrieving JSON data
 * Find better package placement later
 */
public class ApiClient {

    public String fetchDatafromApi(String apiUrl) {
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader((new InputStreamReader(connection.getInputStream()));
                String line;
                while((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            }
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
         return response.toString();
    }



}
