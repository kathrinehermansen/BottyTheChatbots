package com.xatkit.bot.library;

import com.xatkit.dsl.entity.EntityDefinitionReferenceProvider;
import com.xatkit.intent.EntityDefinition;
import com.xatkit.intent.IntentDefinition;
import com.xatkit.intent.MappingEntityDefinition;
import com.xatkit.intent.MappingEntityDefinitionEntry;
import lombok.NonNull;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fr.inria.atlanmod.commons.Preconditions.checkArgument;
import static fr.inria.atlanmod.commons.Preconditions.checkNotNull;
import static java.util.Objects.nonNull;

public class Utils {

    private Utils() {

    }

    /*
    Get method for the values of the chatbots entities.
     */

    public static List<String> getEntities(EntityDefinitionReferenceProvider entity){
        EntityDefinition referredEntity = entity.getEntityReference().getReferredEntity();
        if (referredEntity instanceof MappingEntityDefinition) {
            MappingEntityDefinition mapping = (MappingEntityDefinition) referredEntity;
            return mapping.getEntries().stream().map(MappingEntityDefinitionEntry::getReferenceValue)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException(MessageFormat.format("Expected a {0}, found a {1}",
                    MappingEntityDefinition.class.getSimpleName(), referredEntity.getClass().getSimpleName()));
        }
    }

    public static List<String> getTrainingSentences(@NonNull IntentDefinition... intents){
        for(IntentDefinition intent : intents) {
            checkNotNull(intent);
            checkArgument(!intent.getTrainingSentences().isEmpty()
                    && nonNull(intent.getTrainingSentences().get(0))
                    && !intent.getTrainingSentences().get(0).isEmpty(),
                "Intent %s does not contain a non-null, non-empty training sentence", intent.getName());
        }
        return Arrays.stream(intents).map(i -> i.getTrainingSentences().get(0)).collect(Collectors.toList());
    }
}
