package com.xatkit.bot;

import com.xatkit.core.XatkitBot;
import com.xatkit.execution.StateContext;
import com.xatkit.plugins.react.platform.ReactPlatform;
import com.xatkit.plugins.react.platform.io.ReactEventProvider;
import com.xatkit.plugins.react.platform.io.ReactIntentProvider;
import lombok.val;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.Configuration;
import com.xatkit.bot.ApiClient;
import org.json.JSONObject;
import java.util.Map;
import java.util.ResourceBundle;


import static com.xatkit.dsl.DSL.eventIs;
import static com.xatkit.dsl.DSL.fallbackState;
import static com.xatkit.dsl.DSL.intentIs;
import static com.xatkit.dsl.DSL.model;
import static com.xatkit.dsl.DSL.state;



import java.io.IOException;


public class BotClass {

    public final XatkitBot xatkitBot;
    public final ResourceBundle messages;

    private ApiClient apiClient;  // Create an instance of the ApiClient class


public BotClass(Configuration botConfig) {


    apiClient = new ApiClient();  // Initialize the ApiClient instance
    messages = ResourceBundle.getBundle("messages"); //add locale later for language
    /*
     * Instantiate the platform and providers we will use in the bot definition.
     */
    ReactPlatform reactPlatform = new ReactPlatform();
    ReactEventProvider reactEventProvider = reactPlatform.getReactEventProvider();
    ReactIntentProvider reactIntentProvider = reactPlatform.getReactIntentProvider();

    /*
     * Create the states we want to use in our bot.
     */
    val init = state("Init");
    val awaitingInput = state("AwaitingInput");
    val startState = state("Start");

    init
            .next()
            .when(eventIs(ReactEventProvider.ClientReady)).moveTo(awaitingInput);
    awaitingInput
            .body(context -> {
                reactPlatform.reply(context, messages.getString("Greetings"));
            })
            .next()
            .moveTo(startState);
    startState
            .body(context -> {
                reactPlatform.reply(context, "hello fix messages and utils ");

            })
            .next();
            //.when()
            //.when();


    val defaultFallback = fallbackState()
            .body(context -> {
                reactPlatform.reply(context, "something went wrong");
            });

    /*
     * Creates the bot model that will be executed by the Xatkit engine.
     */
    val botModel = model()
            .usePlatform(reactPlatform)
            .listenTo(reactEventProvider)
            .listenTo(reactIntentProvider)
            .initState(init)
            .defaultFallbackState(defaultFallback);

    xatkitBot = new XatkitBot(botModel, botConfig);
}


    /*
May change later when intent and entity classes are made
 */
    public void processUserInput(StateContext context) throws IOException {
        String apiUrl = "https://kassal.app/api/v1/products?search=";
        String intentValue = (String) context.getIntent().getValue("name");
       // String entityValue = (String) context.getEntities().get("myEntity").getValue();
        String key = apiClient.retrieveKey();
        //JSONObject entities = context.getEntities();

        //Map<String, Object> intentEntities = context.getIntent().
        //String entityValue = (String) intentEntities.get("name");

      //  String apiResponse = apiClient.fetchDataFromApi(apiUrl + entityValue, key);

       // String apiResponse = apiClient.fetchDataFromApi(apiUrl, intentValue, entityValue, key);


    }
    public void run() {
        this.xatkitBot.run();
    }

}
