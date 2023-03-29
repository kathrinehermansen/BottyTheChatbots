package com.xatkit.example;

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
import static com.xatkit.dsl.DSL.any;
import static com.xatkit.dsl.DSL.mapping;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mashape.unirest.http.Unirest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.lang.Integer;

public class GroceryBot {

    public static String getProductFromContextIntent(StateContext context) {
        String ord1 = (String) context.getIntent().getValue("ord1");
        String ord2 = (String) context.getIntent().getValue("ord2");
        String ord3 = (String) context.getIntent().getValue("ord3");
        String ord4 = (String) context.getIntent().getValue("ord4");
        String ord5 = (String) context.getIntent().getValue("ord5");
        StringBuilder productBuilder = new StringBuilder();

        if (ord1 != null) {
            productBuilder.append(ord1);
        }
        if (ord2 != null) {
            productBuilder.append("+").append(ord2);
        }
        if (ord3 != null) {
            productBuilder.append("+").append(ord3);
        }
        if (ord4 != null) {
            productBuilder.append("+").append(ord4);
        }
        if (ord5 != null) {
            productBuilder.append("+").append(ord5);
        }
        return productBuilder.toString();
    }

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

        val hasAllergen = intent("HasAllergen")
                .trainingSentence("Does PRODUCT have ALLERGEN?")
                .trainingSentence("Does PRODUCT contain ALLERGEN?")
                .parameter("name").fromFragment("PRODUCT").entity(any())
                .parameter("allergen").fromFragment("ALLERGEN").entity(any());


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

        val productIngredients= intent("ProductIngredients")
                .trainingSentence("What are the ingredients of PRODUCT?")
                .trainingSentence("What are the ingredients in PRODUCT?")
                .trainingSentence("ingredients in PRODUCT?")
                .parameter("name").fromFragment("PRODUCT").entity(country());
                
        val productMaxPrice = intent("ProductMaxPrice")
                .trainingSentence("List me products with the word PRODUCT in it, with maximum price of MAXPRICE kr")
                .trainingSentence("List me PRODUCT with max price of MAXPRICE kr")
                .parameter("name").fromFragment("PRODUCT").entity(any())
                .parameter("maxprice").fromFragment("MAXPRICE").entity(any());

        val priceAndStore = intent("PriceAndStore")
                .trainingSentence("What is the price of PRODUCT from STORE?")
                .trainingSentence("I would like to know the price of PRODUCT from STORE")
                .trainingSentence("How much does PRODUCT from STORE cost?")
                .parameter("name").fromFragment("PRODUCT").entity(any())
                .parameter("store").fromFragment("STORE").entity(any());

        val specifyProduct = intent("SpecifyProduct")
                .trainingSentence("Narrow search to ORD1")
                .trainingSentence("Narrow search to ORD1 ORD2")
                .trainingSentence("Narrow search to ORD1 ORD2 ORD3")
                .trainingSentence("Narrow search to ORD1 ORD2 ORD3 ORD4")
                .trainingSentence("Narrow search to ORD1 ORD2 ORD3 ORD5")
                .parameter("ord1").fromFragment("ORD1").entity(any())
                .parameter("ord2").fromFragment("ORD2").entity(any())
                .parameter("ord3").fromFragment("ORD3").entity(any())
                .parameter("ord4").fromFragment("ORD4").entity(any())
                .parameter("ord5").fromFragment("ORD5").entity(any());

        val narrowMySearch = intent("NarrowMySearch")
                .trainingSentence("I would like to narrow my product search")
                .trainingSentence("Can I narrow my product search");

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
        val handleProductIngredients = state("HandleProductIngredients");
        val handleHasAllergen = state("HandleHasAllergen");
        val handleProductMaxPrice = state("HandleProductMaxPrice");
        val handlePriceAndStore = state("HandlePriceAndStore");
        val handleNarrowMySearch = state("HandleNarrowMySearch");
        val handleSpecifyProduct = state("HandleSpecifyProduct");

        init
                .next()
                .when(eventIs(ReactEventProvider.ClientReady)).moveTo(awaitingInput);

        awaitingInput
                .next()
                .when(intentIs(greetings)).moveTo(handleWelcome)
                .when(intentIs(productAllergens)).moveTo(handleProductAllergens)
                .when(intentIs(doYouHavePrice)).moveTo(handleDoYouHavePrice)
                .when(intentIs(productCalories)).moveTo(handleProductCalories)
                .when(intentIs(productIngredients)).moveTo(handleProductIngredients)
                .when(intentIs(hasAllergen)).moveTo(handleHasAllergen)
                .when(intentIs(howAreYou)).moveTo(handleWhatsUp)
                .when(intentIs(productMaxPrice)).moveTo(handleProductMaxPrice)
                .when(intentIs(priceAndStore)).moveTo(handlePriceAndStore)
                .when(intentIs(narrowMySearch)).moveTo(handleNarrowMySearch)
                .when(intentIs(specifyProduct)).moveTo(handleSpecifyProduct);

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
                            System.out.println(allergensdata);

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

                                } // virker ikke foreløpig

                            } if(allergensdata.length() < 1) {
                                reactPlatform.reply(context, "Allergens are not stated for " + productName + " from " + brandName);

                            } else if (allergens.isEmpty()) {
                            reactPlatform.reply(context, "No allergens found for " + productName + " from " + brandName);

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

        handleHasAllergen
                .body(context -> {
                    String product = (String) context.getIntent().getValue("name");
                    String allergen = (String) context.getIntent().getValue("allergen");

                    Map<String, Object> queryParameters = new HashMap<>();
                    queryParameters.put("name", product);
                    queryParameters.put("allergen", allergen);

                    try {
                        HttpResponse<String> response = Unirest.get("https://kassal.app/api/v1/products?search=" + product)
                                .header("Authorization", "Bearer " + key)
                                .header("Accept", "application/json")
                                .asString();

                        if (response.getStatus() == 200) {

                            JSONObject jsonObject = new JSONObject(response.getBody());
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            JSONArray allergensData = dataArray.getJSONObject(0).getJSONArray("allergens");

                            String allergenDisplayName = null;
                            String containsAllergen = null;
                            String allergens = "";

                            //loop through allergens
                            for (int i = 0; i < allergensData.length(); i++) {
                                JSONObject allergenName = allergensData.getJSONObject(i);
                                allergenDisplayName = allergenName.getString("display_name").toLowerCase();
                                containsAllergen = allergenName.getString("contains");

                                if (containsAllergen.equals("YES") && allergenDisplayName.equals(allergen)) {
                                    allergens = allergenDisplayName;
                                }
                            }

                            if (!allergens.isEmpty()) {
                                reactPlatform.reply(context, "Yes, " + product + " contains " + allergens);

                            } else if(allergensData.length() < 1) {
                                reactPlatform.reply(context, "Allergens are not stated for " + product );
                            } else {
                                reactPlatform.reply(context, "No " + allergen + " is not found in " + product);
                            }



                            } else if (response.getStatus() == 400) {
                            reactPlatform.reply(context, "Oops, I couldn't find the allergen of this product");
                        } else {
                            reactPlatform.reply(context, "Sorry, an error occurred " + response.getStatus());
                        }
                    } catch(UnirestException e) {
                        e.printStackTrace();
                    }
                })
                .next()
                .moveTo(awaitingInput);

        handleProductIngredients
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
                                JSONObject jsonObject = new JSONObject(response.getBody());
                                JSONArray dataArray = jsonObject.getJSONArray("data");
                                String productName = dataArray.getJSONObject(0).getString("name");
                                Object ingredientsObject = dataArray.getJSONObject(0).get("ingredients");
                                String brandName = dataArray.getJSONObject(0).getString("brand");

                                if (ingredientsObject == null) {
                                    reactPlatform.reply(context, "Couldn't find the list of ingredients of " + productName + " from " + brandName);
                                } else if (ingredientsObject instanceof String) {
                                    String ingredients = (String) ingredientsObject;
                                    System.out.println("The ingredients of " + productName + " are " + ingredients);
                                    reactPlatform.reply(context, "The ingredients of " + productName + " from " + brandName + " are: " + ingredients);
                                } else {
                                    reactPlatform.reply(context, "Couldn't find the list of ingredients of " + productName + " from " + brandName);
                                }


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

        handleProductMaxPrice
                .body(context -> {
                    String product = (String) context.getIntent().getValue("name");
                    String maxprice = (String) context.getIntent().getValue("maxprice");

                    Map<String, Object> queryParameters = new HashMap<>();
                    queryParameters.put("name", product);
                    queryParameters.put("maxprice", maxprice);

                    try {
                        HttpResponse<String> response = Unirest.get("https://kassal.app/api/v1/products?search=" + product +"&size=50&price_max=" + maxprice)
                                .header("Authorization", "Bearer " + key)
                                .header("Accept", "application/json")
                                .asString();

                        if (response.getStatus() == 200) {

                            JSONObject jsonObject = new JSONObject(response.getBody());
                            JSONArray dataArray = jsonObject.getJSONArray("data");

                            reactPlatform.reply(context, "We found this information about products with the word " + product + " in it, with a maximum price of " + maxprice + " kr:");

                            for (int i = 0; i<dataArray.length(); i++) {
                                    String storeName = dataArray.getJSONObject(i).getJSONObject("store").getString("name");
                                    String productName = dataArray.getJSONObject(i).getString("name");
                                    double currentPrice = dataArray.getJSONObject(i).getDouble("current_price");
                                    String brandName = dataArray.getJSONObject(i).getString("brand");
                                    reactPlatform.reply(context, "The price of  " + productName + " from " + brandName +" is currently " + currentPrice + " kr, from store " + storeName);
                                }

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

        handlePriceAndStore
                .body(context -> {

                    String product = (String) context.getIntent().getValue("name");
                    System.out.println("The name of the product is " + product);
                    String store = (String) context.getIntent().getValue("store");
                    System.out.println("The name of the store is " + store);

                    Map<String, Object> queryParameters = new HashMap<>();
                    queryParameters.put("name", product);
                    queryParameters.put("store", store);

                    try {
                        HttpResponse<String> response = Unirest.get("https://kassal.app/api/v1/products?search=" + product +"&size=50")
                                .header("Authorization", "Bearer " + key)
                                .header("Accept", "application/json")
                                .asString();

                        if (response.getStatus() == 200) {

                            JSONObject jsonObject = new JSONObject(response.getBody());
                            JSONArray dataArray = jsonObject.getJSONArray("data");
                            int answers = 0;

                            for (int i = 0; i<dataArray.length(); i++) {
                                String storeName = dataArray.getJSONObject(i).getJSONObject("store").getString("name");
                                if (storeName.equalsIgnoreCase(store)) {
                                    answers++;
                                    if (answers == 1) {
                                        reactPlatform.reply(context, "Your search of product " + product + " from store " + store + " gave these results:");
                                    }
                                    String productName = dataArray.getJSONObject(i).getString("name");
                                    double currentPrice = dataArray.getJSONObject(i).getDouble("current_price");
                                    String brandName = dataArray.getJSONObject(i).getString("brand");
                                    reactPlatform.reply(context, "The price of  " + productName + " from " + brandName +" is currently " + currentPrice + " kr, from store " + storeName);
                                }
                            }

                            if (answers == 0) {
                                reactPlatform.reply(context, "Sadly, your search gave no results.");
                            }
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

        handleNarrowMySearch
                .body(context -> reactPlatform.reply(context, "Sure, just write - Narrow search to - and type up to 5 words to narrow down your product search."))
                .next()
                .moveTo(awaitingInput);

        handleSpecifyProduct
                .body(context -> {
                    String product = getProductFromContextIntent(context);

                    try {
                        HttpResponse<String> response = Unirest.get("https://kassal.app/api/v1/products?search=" + product +"&size=50")
                                .header("Authorization", "Bearer " + key)
                                .header("Accept", "application/json")
                                .asString();

                        if (response.getStatus() == 200) {

                            JSONObject jsonObject = new JSONObject(response.getBody());
                            JSONArray dataArray = jsonObject.getJSONArray("data");

                            System.out.println(dataArray);

                            reactPlatform.reply(context, "Below are the hits we got, maximum of 50 products");

                            for (int i = 0; i<dataArray.length(); i++) {
                                String storeName = dataArray.getJSONObject(i).getJSONObject("store").getString("name");
                                String productName = dataArray.getJSONObject(i).getString("name");
                                double currentPrice = dataArray.getJSONObject(i).getDouble("current_price");
                                if (dataArray.getJSONObject(i).getString("brand") != null) {
                                    String brandName = dataArray.getJSONObject(i).getString("brand");
                                    reactPlatform.reply(context, "The price of  " + productName + " from " + brandName +" is currently " + currentPrice + " kr, from store " + storeName);
                                } else {
                                    reactPlatform.reply(context, "The price of  " + productName +  " is currently " + currentPrice + " kr, from store " + storeName);
                                }
                            }

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
