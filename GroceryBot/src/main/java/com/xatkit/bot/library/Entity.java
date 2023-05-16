package com.xatkit.bot.library;

import com.xatkit.dsl.entity.EntityDefinitionReferenceProvider;
import com.xatkit.dsl.entity.MappingEntryStep;
import com.xatkit.dsl.entity.MappingSynonymStep;
import com.xatkit.intent.EntityDefinition;
import com.xatkit.intent.MappingEntityDefinition;
import com.xatkit.intent.MappingEntityDefinitionEntry;
import fr.inria.atlanmod.commons.log.Log;
import lombok.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xatkit.dsl.DSL.mapping;
import static org.apache.commons.lang3.StringUtils.isEmpty;



/**
 * A set of entities the chatbot can recognize
 * put on BREAK
 */

public class Entity {
    /**
     * The language of the entities
     **
    private final String language;

    /**
     * The name of the file containing the chatbot field entities
     */
    private static String entitiesJsonFile = "entities.json";

    /**
     * The name og the file containing the chatbot operator entities
     */
    private static String fieldOperatorsJson = "fieldOperators.json";

    /**
     * Contains the chatbot entities in a JSON format
     */
    private static final JSONObject entitiesJson;

    /**
     *  more to be instantiated but now sure what is needed yet
     */
/*
    static {
        InputStream is1 = Thread.currentThread().getContextClassLoader().getResourceAsStream(entitiesJsonFile);
        if (is1 == null) {
            throw new NullPointerException("Cannot find the json file \"" + entitiesJsonFile + "\"");
        }
        InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(fieldOperatorsJson);
        if (is2 == null) {
            throw new NullPointerException("Cannot find the json file \"" + fieldOperatorsJson + "\"");
        }
        JSONObject fields = new JSONObject(new JSONTokener(is1));
        JSONObject fieldOperators = new JSONObject(new JSONTokener(is2));
        entitiesJson = new JSONObject();
        for (String key : fields.keySet()) {
            entitiesJson.put(key, fields.getJSONObject(key));
        }
        for (String key : fieldOperators.keySet()) {
            entitiesJson.put(key, fieldOperators.getJSONObject(key));
        }
    }

    public Entities(String language){
        this.language = language;

    }

    private EntityDefinitionReferenceProvider generateFieldEntity(String entity) {
        JSONObject entityJson = entitiesJson.getJSONObject(entityName);
        MappingEntryStep entity = mapping(entityName);
        for (String entry : entityJson.keySet()) {
            MappingSynonymStep synonymStep = entity.entry().value(entry);
            try{
                String readableName = entityJson.getJSONObject((entry).getJSONObject(language).getString("readableName"));
                if(!isEmpty(readableName)) {
                    this.readableNames.put(entry, readableName);
                    if(!entry.equals(readableName)){
                        // add readable name as an entity synonym
                        synonymStep.synonym(readableName);
                    }
                } else {
                this.readableNames.put(entry,entry);
            }
        } catch(Exception ignored) {}
        for(Object synonym :entityJson.getJSONObject(entry).getJSONObject(language).getJSONArray("synonyms"));
            synonymStep.synonym((String) synonym);
        }
        try {
            boolean key =entityJson.getJSONObject(entry).getBoolean("key");
            if(key) {
                this.keyFields.add(entry);
            }
        } catch (Exception ignored) {}
        // Check there are no duplicated readable names
        Set<String> readableNamesSet = new HashSet<>(this.readableNames.values());
        List<String> readableNamesList = new ArrayList<>(this.readableNames.values());
        if (readableNamesSet.size() < readableNamesList.size()) {
            throw new IllegalArgumentException("duplicated readable names were found in field entities");


        return (EntityDefinitionReferenceProvider) entity;
    }




}
*/