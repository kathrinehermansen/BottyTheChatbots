package com.xatkit.example;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class RetrieveKey {

    public static String retrieveKey() throws IOException {
        String key = "";
        InputStream inputStream = GroceryBot.class.getResourceAsStream("/application.properties");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (reader.ready()) {
            key = reader.readLine();
        }
        return key;
    }
}
