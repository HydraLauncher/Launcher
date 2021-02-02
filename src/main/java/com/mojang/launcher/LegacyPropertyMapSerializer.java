package com.mojang.launcher;

import java.lang.reflect.*;
import com.mojang.authlib.properties.*;
import com.google.gson.*;
import java.util.*;
import com.google.common.collect.*;

public class LegacyPropertyMapSerializer implements JsonSerializer<PropertyMap>
{
    @Override
    public JsonElement serialize(final PropertyMap src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject result = new JsonObject();
        for (final String key : src.keySet()) {
            final JsonArray values = new JsonArray();
            for (final Property property : src.get(key)) {
                values.add(new JsonPrimitive(property.getValue()));
            }
            result.add(key, values);
        }
        return result;
    }
}
