package com.xatkit.example;

import com.mashape.unirest.http.ObjectMapper;
import com.xatkit.core.XatkitBot;
import com.xatkit.core.recognition.IntentRecognitionProviderFactory;
import com.xatkit.core.recognition.dialogflow.DialogFlowConfiguration;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.io.ReactEventProvider;
import com.xatkit.plugins.react.platform.io.ReactIntentProvider;
import lombok.val;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;

import static com.xatkit.dsl.DSL.country;
import static com.xatkit.dsl.DSL.eventIs;
import static com.xatkit.dsl.DSL.fallbackState;
import static com.xatkit.dsl.DSL.intent;
import static com.xatkit.dsl.DSL.intentIs;
import static com.xatkit.dsl.DSL.model;
import static com.xatkit.dsl.DSL.state;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mashape.unirest.http.Unirest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * This is an example greetings bot designed with Xatkit.
 * <p>
 * You can check our <a href="https://github.com/xatkit-bot-platform/xatkit/wiki">wiki</a>
 * to learn more about bot creation, supported platforms, and advanced usage.
 */
public class StortingetBot {

    /*
     * Your bot is a plain Java application: you need to define a main method to make the created jar executable.
     */
    public static void main(String[] args) {

        /*
         * Define the intents our bot will react to.
         * <p>
         * In this example we want our bot to answer greetings inputs and "how are you" questions, so we create an
         * intent for each, and we give a few example training sentences to configure the underlying NLP engine.
         * <p>
         * Note that we recommend the usage of Lombok's val when using the Xatkit DSL: the fluent API defines many
         * interfaces that are not useful for bot designers. If you don't want to use val you can use our own
         * interface IntentVar instead.
         */
        val greetings = intent("Greetings")
                .trainingSentence("Hi")
                .trainingSentence("Hello")
                .trainingSentence("Good morning")
                .trainingSentence("Good afternoon");

        val howAreYou = intent("HowAreYou")
                .trainingSentence("How are you?")
                .trainingSentence("What's up?")
                .trainingSentence("How do you feel?");

        val whatsYourName = intent("WhatsYourName")
                .trainingSentence("Whats your name?")
                .trainingSentence("Do you have a name?")
                .trainingSentence("Your name is?");

        val whatIsTheCapital = intent("WhatIsTheCapital")
                .trainingSentence("What is the capital of COUNTRY?")
                .trainingSentence("Capital of COUNTRY?")
                .parameter("name").fromFragment("COUNTRY").entity(country());

        /*
         * Instantiate the platform we will use in the bot definition.
         * <p>
         * Instantiating the platform before specifying the bot's states creates a usable reference that can be
         * accessed in the states, e.g:
         * <pre>
         * {@code
         * myState
         *   .body(context -> reactPlatform.reply(context, "Hi, nice to meet you!");
         * }
         * </pre>
         */
        /*
         * Similarly, instantiate the intent/event providers we want to use.
         * <p>
         * In our example we want to receive intents (i.e. interpreted user inputs) from our react client, so we
         * create a ReactIntentProvider instance. We also want to receive events from the react client (e.g. when the
         * client's connection is ready), so we create a ReactEventProvider instance.
         * <p>
         * We can instantiate as many providers as we want, including providers from different platforms.
         */
        ReactPlatform reactPlatform = new ReactPlatform();
        ReactEventProvider reactEventProvider = reactPlatform.getReactEventProvider();
        ReactIntentProvider reactIntentProvider = reactPlatform.getReactIntentProvider();

        /*
         * Create the states we want to use in our bot.
         * <p>
         * Similarly to platform/provider creation, we create the state variables first, and we specify their content
         * later. This allows to define circular references between states (e.g. AwaitingQuestion -> HandleWelcome ->
         * AwaitingQuestion).
         * <p>
         * This is not mandatory though, the important point is to have fully specified states when we build the
         * final bot model.
         */
        val init = state("Init");
        val awaitingInput = state("AwaitingInput");
        val handleWelcome = state("HandleWelcome");
        val handleWhatsUp = state("HandleWhatsUp");
        val handleWhatsYourName = state("HandleWhatsYourName");
        val handleWhatIsTheCapital = state("HandleWhatIsTheCapital");

        /*
         * Specify the content of the bot states (i.e. the behavior of the bot).
         * <p>
         * Each state contains:
         * <ul>
         * <li>An optional body executed when entering the state. This body is provided as a lambda expression
         * with a context parameter representing the current state of the bot.</li>
         * <li>A mandatory list of next() transitions that are evaluated when a new event is received. This list
         * must contain at least one transition. Transitions can be guarded with a when(...) clause, or
         * automatically navigated using a direct moveTo(state) clause.</li>
         * <li>An optional fallback executed when there is no navigable transition matching the received event. As
         * for the body the state fallback is provided as a lambda expression with a context parameter representing
         * the current state of the bot. If there is no fallback defined for a state the bot's default fallback state
         * is executed instead.
         * </li>
         * </ul>
         */
        init
                .next()
                /*
                 * We check that the received event matches the ClientReady event defined in the
                 * ReactEventProvider. The list of events defined in a provider is available in the provider's
                 * wiki page.
                 */
                .when(eventIs(ReactEventProvider.ClientReady)).moveTo(awaitingInput);

        awaitingInput
                .next()
                /*
                 * The Xatkit DSL offers dedicated predicates (intentIs(IntentDefinition) and eventIs
                 * (EventDefinition) to check received intents/events.
                 * <p>
                 * You can also check a condition over the underlying bot state using the following syntax:
                 * <pre>
                 * {@code
                 * .when(context -> [condition manipulating the context]).moveTo(state);
                 * }
                 * </pre>
                 */
                .when(intentIs(greetings)).moveTo(handleWelcome)
                .when(intentIs(howAreYou)).moveTo(handleWhatsUp)
                .when(intentIs(whatsYourName)).moveTo(handleWhatsYourName)
                .when(intentIs(whatIsTheCapital)).moveTo(handleWhatIsTheCapital);

        handleWelcome
                .body(context -> reactPlatform.reply(context, "Hi, nice to meet you!"))
                .next()
                /*
                 * A transition that is automatically navigated: in this case once we have answered the user we
                 * want to go back in a state where we wait for the next intent.
                 */
                .moveTo(awaitingInput);

        handleWhatsUp
                .body(context -> reactPlatform.reply(context, "I am fine and you?"))
                .next()
                .moveTo(awaitingInput);

        handleWhatsYourName
                .body(context -> reactPlatform.reply(context, "My name is Botty"))
                .next()
                .moveTo(awaitingInput);

        handleWhatIsTheCapital
                /* .body(context -> reactPlatform.reply(context, "The capital of COUNTRY is CITY"))
                 .next()
                 .moveTo(awaitingInput);
         */
                .body(context -> {
                    String country = (String) context.getIntent().getValue("name");
                    System.out.println("The name of the country is " + country);


                    Map<String, Object> queryParameters = new HashMap<>();
                    queryParameters.put("name", country);

                    try {
                        //HttpResponse<String> response = Unirest.get("https://countries-cities.p.rapidapi.com/location/country/GB")
                        HttpResponse<String> response = Unirest.get("https://countries-cities.p.rapidapi.com/location/country/list?format=json")
                                .header("X-RapidAPI-Key", "dbe83342c2msh8ca91e8296d0001p16c2e3jsn1892d4a3a4e3")
                                .header("X-RapidAPI-Host", "countries-cities.p.rapidapi.com")
                                .asString();

                        //HttpResponse<String> response = Unirest.get( "https://ajayakv-rest-countries-v1.p.rapidapi.com/rest/v1/all").header("name", country).asString();
                        if (response.getStatus() == 200) {

                            final String[] countryCode = new String[1];
                            JSONObject jsonArray = new JSONObject(response.getBody()).getJSONObject("countries");

                            jsonArray.keys().forEachRemaining(key -> {
                                Object value = jsonArray.get(key);
                                if (value.toString().equalsIgnoreCase(country)) {
                                    System.out.println(key + value);
                                    countryCode[0] = key.toString();
                                    System.out.println(countryCode[0]);
                                }
                            });

                            if (countryCode != null) {
                                Thread.sleep(1100);
                                HttpResponse<String> responseCapital = Unirest.get("https://countries-cities.p.rapidapi.com/location/country/" + countryCode[0])
                                        .header("X-RapidAPI-Key", "dbe83342c2msh8ca91e8296d0001p16c2e3jsn1892d4a3a4e3")
                                        .header("X-RapidAPI-Host", "countries-cities.p.rapidapi.com")
                                        .asString();

                                JSONObject jsonObject = new JSONObject(responseCapital.getBody());
                                String capitalName = jsonObject.getJSONObject("capital").getString("name");
                                System.out.println("The capital city of " + country + " is " + capitalName);
                                reactPlatform.reply(context, "The capital of " + country + " is " + capitalName);
                            }

                        } else if (response.getStatus() == 400) {
                            reactPlatform.reply(context, "Oops, I couldn't find this country");
                        } else {
                            reactPlatform.reply(context, "Sorry, an error occurred " +  response.getStatus());
                        }
                    } catch(UnirestException | InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .next()
                .moveTo(awaitingInput);

        /*
         * The state that is executed if the engine doesn't find any navigable transition in a state and the state
         * doesn't contain a fallback.
         * <p>
         * The default fallback state is typically used to answer generic error messages, while state fallback can
         * benefit from contextual information to answer more precisely.
         * <p>
         * Note that every Xatkit bot needs a default fallback state.
         */
        val defaultFallback = fallbackState()
                .body(context -> reactPlatform.reply(context, "Sorry, I didn't, get it"));

        /*
         * Creates the bot model that will be executed by the Xatkit engine.
         * <p>
         * A bot model contains:
         * - A list of platforms used by the bot. Xatkit will take care of starting and initializing the platforms
         * when starting the bot.
         * - A list of providers the bot should listen to for events/intents. As for the platforms Xatkit will take
         * care of initializing the provider when starting the bot.
         * - The entry point of the bot (a.k.a init state). The other states will be automatically collected by analyzing the state machine
         * - The default fallback state: the state that is executed if the engine doesn't find any navigable
         * transition in a state and the state doesn't contain a fallback.
         */
        val botModel = model()
                .usePlatform(reactPlatform)
                .listenTo(reactEventProvider)
                .listenTo(reactIntentProvider)
                .initState(init)
                .defaultFallbackState(defaultFallback);


        Configuration botConfiguration = new BaseConfiguration();

        botConfiguration.addProperty(IntentRecognitionProviderFactory.INTENT_PROVIDER_KEY, DialogFlowConfiguration.DIALOGLFOW_INTENT_PROVIDER);
        botConfiguration.addProperty(DialogFlowConfiguration.PROJECT_ID_KEY, "greetingsbot-kvqs");
        botConfiguration.addProperty(DialogFlowConfiguration.GOOGLE_CREDENTIALS_PATH_KEY, "/Users/KathrineH/greetingsbot-kvqs-13a65a6b939f.json");
        botConfiguration.addProperty(DialogFlowConfiguration.LANGUAGE_CODE_KEY, "en-US");
        botConfiguration.addProperty(DialogFlowConfiguration.CLEAN_AGENT_ON_STARTUP_KEY, true);
        /*
         * Add configuration properties (e.g. authentication tokens, platform tuning, intent provider to use).
         * Check the corresponding platform's wiki page for further information on optional/mandatory parameters and
         * their values.
         */

        XatkitBot xatkitBot = new XatkitBot(botModel, botConfiguration);
        xatkitBot.run();
        /*
         * The bot is now started, you can check http://localhost:5000/admin to test it.
         * The logs of the bot are stored in the logs folder at the root of this project.
         */
    }
}
