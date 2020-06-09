package com.google.moviestvsentiments.model;

import androidx.room.TypeConverter;

/**
 * A reaction type of a user sentiment object.
 */
public enum SentimentType {
    UNSPECIFIED(0),
    THUMBS_UP(1),
    THUMBS_DOWN(2);

    private final int type;

    SentimentType(int type) {
        this.type = type;
    }

    /**
     * Converts a SentimentType into an int.
     * @param sentimentType The SentimentType to convert.
     * @return The int value associated with the SentimentType.
     */
    @TypeConverter
    public static int sentimentTypeToInt(SentimentType sentimentType) {
        return sentimentType.type;
    }

    /**
     * Converts an int into a SentimentType.
     * @param type The int to convert.
     * @return The SentimentType associated with the int value.
     */
    @TypeConverter
    public static SentimentType intToSentimentType(int type) {
        return SentimentType.values()[type];
    }
}
