package net.ltxprogrammer.changed.entity.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

public class AnimationAssociation {
    private final AnimationCategory category;
    private final ResourceLocation name;
    private final JsonObject criteria;

    public AnimationAssociation(AnimationCategory category, ResourceLocation name, JsonObject criteria) {
        this.category = category;
        this.name = name;
        this.criteria = criteria;
    }

    public AnimationCategory getCategory() {
        return category;
    }

    public ResourceLocation getName() {
        return name;
    }

    public JsonObject getCriteria() {
        return criteria;
    }

    public enum Match {
        ALLOW,
        DEFAULT,
        DENY;

        public static Match fromBoolean(boolean result) {
            return result ? ALLOW : DENY;
        }

        public Match and(Supplier<Match> condition) {
            if (this == DENY)
                return DENY;
            return condition.get();
        }
    }

    private Optional<JsonElement> getField(ResourceLocation field) {
        return Optional.ofNullable(criteria.get(field.toString()));
    }

    public Match fieldEqualsResourceLocation(ResourceLocation field, ResourceLocation value) {
        return getField(field)
                .map(JsonElement::getAsString)
                .map(ResourceLocation::tryParse)
                .map(value::equals)
                .map(Match::fromBoolean)
                .orElse(Match.DEFAULT);
    }

    public Match fieldEqualsString(ResourceLocation field, String value) {
        return getField(field)
                .map(JsonElement::getAsString)
                .map(value::equals)
                .map(Match::fromBoolean)
                .orElse(Match.DEFAULT);
    }

    public Match fieldEqualsInt(ResourceLocation field, int value) {
        return getField(field)
                .map(JsonElement::getAsInt)
                .map(i -> i == value)
                .map(Match::fromBoolean)
                .orElse(Match.DEFAULT);
    }

    public Match fieldEqualsFloat(ResourceLocation field, float value) {
        return getField(field)
                .map(JsonElement::getAsFloat)
                .map(i -> i == value)
                .map(Match::fromBoolean)
                .orElse(Match.DEFAULT);
    }
}
