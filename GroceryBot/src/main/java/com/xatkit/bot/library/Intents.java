package com.xatkit.bot.library;

import com.xatkit.i18n.XatkitI18nHelper;
import com.xatkit.intent.IntentDefinition;

import java.util.Locale;

import static com.xatkit.dsl.DSL.any;
import static com.xatkit.dsl.DSL.dateTime;
import static com.xatkit.dsl.DSL.intent;
import static com.xatkit.dsl.DSL.number;

/**
 * A set of intents the chatbot can recognize
 * PUT ON BREAK
 */
public class Intents {

    /**
     * A container of the training sentences for each intent in a specific language
     */
    private XatkitI18nHelper BUNDLE;

    /**
     * The intent productAllergens
     */
    public final IntentDefinition productAllergens;

    /**
     * The intent hasAllergen
     */
    public final IntentDefinition hasAllergen;

    /**
     * The intent DoYouHavePrice
     */
    public final IntentDefinition doYouHavePrice;

    /**
     * The intent ProductCalories
     */
    public final IntentDefinition productCalories;

    /**
     * The intent ProductIngredients
     */
    public final IntentDefinition productIngredients;

    /**
     * The intent ProductMaxPrice

    public final IntentDefinition productMaxPrice;

    /**
     * The Intent PriceAndStore
     */
    public final IntentDefinition priceAndStore;

    /**
     * The intent NarrowMySearch
     */
    public final IntentDefinition narrowMySearch;


    public Intents(Entities entities, Locale locale) {
        BUNDLE = new XatkitI18nHelper("intents", locale);

        productAllergens = intent("ProductAllergens")
                .trainingSentences(BUNDLE.getStringArray("ProductAllergens"))
               // .parameter(ContextKeys.PRODUCT).fromFragment("PRODUCT").entity(entities.entities)
                .getIntentDefinition();
        hasAllergen = intent("HasAllergen")
                .trainingSentences(BUNDLE.getStringArray("HasAllergen"))
                .getIntentDefinition();
        doYouHavePrice = intent("DoYouHavePrice")
                .trainingSentences(BUNDLE.getStringArray("DoYouHavePrice"))
                .getIntentDefinition();
        productCalories = intent("ProductCalories")
                .trainingSentences(BUNDLE.getStringArray("ProductCalories"))
                .getIntentDefinition();
        productIngredients = intent("ProductIngredients")
                .trainingSentences(BUNDLE.getStringArray("ProductAllergens"))
                .getIntentDefinition();
        priceAndStore = intent("PriceAndStore")
                .trainingSentences(BUNDLE.getStringArray("PriceAndStore"))
                .getIntentDefinition();
        narrowMySearch = intent("NarrowMySearch")
                .trainingSentences(BUNDLE.getStringArray("narrowMySearch"))
                .getIntentDefinition();


    }

