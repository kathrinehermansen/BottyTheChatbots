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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GroceryBot {

    public static void main(String[] args) {

        val greetings = intent("Greetings")
                .trainingSentence("Hi")
                .trainingSentence("Hello")
                .trainingSentence("Good morning")
                .trainingSentence("Good afternoon");

        val howAreYou = intent("HowAreYou")
                .trainingSentence("How are you?")
                .trainingSentence("What's up?")
                .trainingSentence("How do you feel?");

        val doYouHaveProduct = intent("DoYouHaveProduct")
                .trainingSentence("Do you have PRODUCT?")
                .parameter("name").fromFragment("PRODUCT").entity(country());

        ReactPlatform reactPlatform = new ReactPlatform();
        ReactEventProvider reactEventProvider = reactPlatform.getReactEventProvider();
        ReactIntentProvider reactIntentProvider = reactPlatform.getReactIntentProvider();

        val init = state("Init");
        val awaitingInput = state("AwaitingInput");
        val handleWelcome = state("HandleWelcome");
        val handleWhatsUp = state("HandleWhatsUp");
        val handleDoYouHaveProduct = state("HandleDoYouHaveProduct");

        init
                .next()
                .when(eventIs(ReactEventProvider.ClientReady)).moveTo(awaitingInput);

        awaitingInput
                .next()
                .when(intentIs(greetings)).moveTo(handleWelcome)
                .when(intentIs(doYouHaveProduct)).moveTo(handleDoYouHaveProduct)
                .when(intentIs(howAreYou)).moveTo(handleWhatsUp);

        handleWelcome
                .body(context -> reactPlatform.reply(context, "Hi, nice to meet you!"))
                .next()
                .moveTo(awaitingInput);

        handleWhatsUp
                .body(context -> reactPlatform.reply(context, "I am fine and you?"))
                .next()
                .moveTo(awaitingInput);

        handleDoYouHaveProduct
                .body(context -> {
                    String product = (String) context.getIntent().getValue("name");
                    System.out.println("Yes we do have product " + product);


                    Map<String, Object> queryParameters = new HashMap<>();
                    queryParameters.put("name", product);

                    try {
                        HttpResponse<String> response = Unirest.get("https://kassal.app/api/v1/products?search=Grandiosa&page=3&price_max=100&sort=price_desc")
                                .header("Authorization", "Bearer " + "dvBWBtYQ3ZnH9uKDvQ5D6XmavMYSKvvgc1AXLITd")
                                .header("Accept", "application/json")
                                .asString();

                        //HttpResponse<String> response = Unirest.get( "https://ajayakv-rest-countries-v1.p.rapidapi.com/rest/v1/all").header("name", country).asString();
                        if (response.getStatus() == 200) {

                            JSONObject jsonObject = new JSONObject(response.getBody());
                            System.out.println(jsonObject);
                        } else if (response.getStatus() == 400) {
                            reactPlatform.reply(context, "Oops, I couldn't find this country");
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

        XatkitBot xatkitBot = new XatkitBot(botModel, botConfiguration);
        xatkitBot.run();
    }
}
