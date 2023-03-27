package com.xatkit.example;

import com.xatkit.core.XatkitBot;
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

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mashape.unirest.http.Unirest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.lang.Integer;

public class GroceryBot {

    private static String retrieveKey () throws IOException {
        String key = "";
        InputStream inputStream = GroceryBot.class.getResourceAsStream("/application.properties");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while(reader.ready()) {
            key = reader.readLine();
        }
        return key;
    }

    public static void main(String[] args) throws IOException {

        String key = retrieveKey();

        val greetings = intent("Greetings")
                .trainingSentence("Hi")
                .trainingSentence("Hello")
                .trainingSentence("Good morning")
                .trainingSentence("Good afternoon");

        val howAreYou = intent("HowAreYou")
                .trainingSentence("How are you?")
                .trainingSentence("What's up?")
                .trainingSentence("How do you feel?");

        val productAllergens = intent("ProductAllergens")
                .trainingSentence("What are the allergens of PRODUCT?")
                .trainingSentence("allergens of PRODUCT?")
                .trainingSentence("Which allergens does PRODUCT have?")
                .parameter("name").fromFragment("PRODUCT").entity(country());

        val doYouHavePrice = intent("DoYouHavePrice")
                .trainingSentence("What is the price of PRODUCT?")
                .trainingSentence("What is the price of this PRODUCT?")
                .parameter("name").fromFragment("PRODUCT").entity(country());

        val productCalories = intent("ProductCalories")
                .trainingSentence("What are the calories of PRODUCT?")
                .trainingSentence("What are the calories in PRODUCT?")
                .trainingSentence("calories in PRODUCT?")
                .trainingSentence("How many calories does PRODUCT have?")
                .parameter("name").fromFragment("PRODUCT").entity(country());

        ReactPlatform reactPlatform = new ReactPlatform();
        ReactEventProvider reactEventProvider = reactPlatform.getReactEventProvider();
        ReactIntentProvider reactIntentProvider = reactPlatform.getReactIntentProvider();

        val init = state("Init");
        val awaitingInput = state("AwaitingInput");
        val handleWelcome = state("HandleWelcome");
        val handleWhatsUp = state("HandleWhatsUp");
        val handleProductAllergens = state("HandleProductAllergens");
        val handleDoYouHavePrice = state("HandleDoYouHavePrice");
        val handleProductCalories = state("HandleProductCalories");

        init
                .next()
                .when(eventIs(ReactEventProvider.ClientReady)).moveTo(awaitingInput);

        awaitingInput
                .next()
                .when(intentIs(greetings)).moveTo(handleWelcome)
                .when(intentIs(productAllergens)).moveTo(handleProductAllergens)
                .when(intentIs(doYouHavePrice)).moveTo(handleDoYouHavePrice)
                .when(intentIs(productCalories)).moveTo(handleProductCalories)
                .when(intentIs(howAreYou)).moveTo(handleWhatsUp);

        handleWelcome
                .body(context -> reactPlatform.reply(context, "Hi, nice to meet you!"))
                .next()
                .moveTo(awaitingInput);

        handleWhatsUp
                .body(context -> reactPlatform.reply(context, "I am fine and you?"))
                .next()
                .moveTo(awaitingInput);

        handleProductAllergens
                .body(context -> {
                    String product = (String) context.getIntent().getValue("name");
                    System.out.println("Yes we do have product " + product);


                    Map<String, Object> queryParameters = new HashMap<>();
                    queryParameters.put("name", product);

                    try {
                        HttpResponse<String> response = Unirest.get("https://kassal.app/api/v1/products?search=" + product)
                                .header("Authorization", "Bearer " + key)
                                .header("Accept", "application/json")
                                .asString();


                        if (response.getStatus() == 200) {

                            JSONObject jsonObject = new JSONObject(response.getBody());
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            String productName = dataArray.getJSONObject(0).getString("name");
                            String brandName = dataArray.getJSONObject(0).getString("brand");

                            JSONArray allergensdata = dataArray.getJSONObject(0).getJSONArray("allergens");

                            String allergenDisplayName = null;
                            String containsAllergen = null;
                            String allergens = "";

                            //loop through allergens and add names to the list if contains is YES
                            for (int i = 0; i < allergensdata.length(); i++) {
                                JSONObject allergen = allergensdata.getJSONObject(i);
                                allergenDisplayName = allergen.getString("display_name");
                                containsAllergen = allergen.getString("contains");

                                if (containsAllergen.equals("YES")){
                                    allergens += allergenDisplayName + ", ";
                                    System.out.println(allergenDisplayName);

                                }
                            }
                            if (allergens.isEmpty()) {
                                reactPlatform.reply(context, "No allergens found for " + productName + " from " + brandName);

                            } else if (containsAllergen.equals("UNKNOWN")){
                                reactPlatform.reply(context, "Allergens are not stated for " + productName + " from " + brandName);

                            }
                            else {
                                allergens = allergens.substring(0, allergens.length() - 2);
                                System.out.println("The allergens of the " + product + " is  " + allergens );
                                reactPlatform.reply(context, "The allergens of  " + productName + " from " + brandName + " are: " + allergens);
                            }


                        } else if (response.getStatus() == 400) {
                            reactPlatform.reply(context, "Oops, I couldn't find this product");
                        } else {
                            reactPlatform.reply(context, "Sorry, an error occurred " +  response.getStatus());
                        }
                    } catch(UnirestException e) {
                        e.printStackTrace();
                    }
                })
                .next()
                .moveTo(awaitingInput);

        handleDoYouHavePrice
                .body(context -> {
                    String product = (String) context.getIntent().getValue("name");
                    System.out.println("The name of the product is  " + product);


                    Map<String, Object> queryParameters = new HashMap<>();
                    queryParameters.put("name", product);

                    try {
                        HttpResponse<String> response = Unirest.get("https://kassal.app/api/v1/products?search=" + product)
                                .header("Authorization", "Bearer " + key)
                                .header("Accept", "application/json")
                                .asString();


                        if (response.getStatus() == 200) {

                            // System.out.println(response.getBody());
                            JSONObject jsonObject = new JSONObject(response.getBody());
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            String productName = dataArray.getJSONObject(0).getString("name");
                            double currentPrice = dataArray.getJSONObject(0).getDouble("current_price");
                            String brandName = dataArray.getJSONObject(0).getString("brand");
                            String storeName = dataArray.getJSONObject(0).getJSONObject("store").getString("name");




                            System.out.println("The price of " + product + " is currently " + currentPrice +  "kr");
                            reactPlatform.reply(context, "The price of  " + productName + " from " + brandName +" is currently " + currentPrice + " kr, from store " + storeName);




                        } else if (response.getStatus() == 400) {
                            reactPlatform.reply(context, "Oops, I couldn't find the price of this product");
                        } else {
                            reactPlatform.reply(context, "Sorry, an error occurred " +  response.getStatus());
                        }
                    } catch(UnirestException e) {
                        e.printStackTrace();

                    }
                })
                .next()
                .moveTo(awaitingInput);

        handleProductCalories
                .body(context -> {
                    String product = (String) context.getIntent().getValue("name");
                    System.out.println("Yes we do have product " + product);


                    Map<String, Object> queryParameters = new HashMap<>();
                    queryParameters.put("name", product);

                    try {
                        HttpResponse<String> response = Unirest.get("https://kassal.app/api/v1/products?search=" + product)
                                .header("Authorization", "Bearer " + key)
                                .header("Accept", "application/json")
                                .asString();


                        if (response.getStatus() == 200) {

                            JSONObject jsonObject = new JSONObject(response.getBody());
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            String productName = dataArray.getJSONObject(0).getString("name");
                            String brandName = dataArray.getJSONObject(0).getString("brand");

                            JSONArray nutritionData = dataArray.getJSONObject(0).getJSONArray("nutrition");
                            System.out.println(nutritionData);

                            String calories = "";

                            int kcal = nutritionData.getJSONObject(0).getInt("amount");
                            calories = Integer.toString(kcal) + " kcal per 100g ";



                            System.out.println("The calories of  " + product + " is  " + calories);
                            reactPlatform.reply(context, "The number of calories in  " + productName + " from " + brandName + " is: " + calories);



                        } else if (response.getStatus() == 400) {
                            reactPlatform.reply(context, "Oops, I couldn't find this product");
                        } else {
                            reactPlatform.reply(context, "Sorry, an error occurred " +  response.getStatus());
                        }
                    } catch(UnirestException e) {
                        e.printStackTrace();
                    }
                })
                .next()
                .moveTo(awaitingInput);

        val defaultFallback = fallbackState()
                .body(context -> reactPlatform.reply(context, "Sorry, I didn't, get it"));

        val botModel = model()
                .usePlatform(reactPlatform)
                .listenTo(reactEventProvider)
                .listenTo(reactIntentProvider)
                .initState(init)
                .defaultFallbackState(defaultFallback);

        Configuration botConfiguration = new BaseConfiguration();

        botConfiguration.getProperty("apiKey");

        XatkitBot xatkitBot = new XatkitBot(botModel, botConfiguration);
        xatkitBot.run();
    }
}
