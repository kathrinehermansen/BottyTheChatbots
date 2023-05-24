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
import java.util.Map;
import java.lang.Integer;

import com.xatkit.example.ApiClient;

public class GroceryBot {

    private static String retrieveKey() throws IOException {
        String key = "";
        InputStream inputStream = GroceryBot.class.getResourceAsStream("/application.properties");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while (reader.ready()) {
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

        val doYouHavePrice = intent("DoYouHavePrice")
                .trainingSentence("What is the price of PRODUCT?")
                .trainingSentence("What is the price of this PRODUCT?")
                .parameter("name").fromFragment("PRODUCT").entity(country());

        ReactPlatform reactPlatform = new ReactPlatform();
        ReactEventProvider reactEventProvider = reactPlatform.getReactEventProvider();
        ReactIntentProvider reactIntentProvider = reactPlatform.getReactIntentProvider();

        val init = state("Init");
        val greetUser = state("GreetUser");
        val handleWelcome = state("HandleWelcome");
        val awaitingInput = state("AwaitingInput");
        val handleDoYouHavePrice = state("HandleDoYouHavePrice");

        init
                .next()
                .when(eventIs(ReactEventProvider.ClientReady)).moveTo(greetUser);

        greetUser
                .body(context -> reactPlatform.reply(context,
                        "Hi, my name is Botty! You can ask me questions about grocery product information. Currently including: \n "
                                +
                                "What is the price of this of product? \n" +
                                "Which allergens does product have? \n" +
                                "Does product have allergen? \n" +
                                "How many calories does product have? \n" +
                                "What are the ingredients in product? \n" +
                                "List me products with the word PRODUCT in it, with maximum price of MAXPRICE kr \n" +
                                "How much does product from store cost? \n" +
                                "Narrow search to ..."))

                .next()
                .moveTo(awaitingInput);

        awaitingInput
                .next()
                .when(intentIs(greetings)).moveTo(handleWelcome)
                .when(intentIs(doYouHavePrice)).moveTo(handleDoYouHavePrice);

        handleWelcome
                .body(context -> reactPlatform.reply(context, "Hi, nice to meet you!"))
                .next()
                .moveTo(awaitingInput);

        handleDoYouHavePrice
                .body(context -> {
                    String product = (String) context.getIntent().getValue("name");
                    System.out.println("The name of the product is  " + product);

                    Map<String, Object> queryParameters = new HashMap<>();
                    queryParameters.put("name", product);

                    String responseResult = null;

                    responseResult = ApiClient.fetchDataFromApi("https://kassal.app/api/v1/products?search=",
                            product, key);

                    reactPlatform.reply(context, responseResult);
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
