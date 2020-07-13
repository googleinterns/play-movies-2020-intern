package com.google.moviestvsentiments.service.database;

import androidx.room.TypeConverter;
import java.time.Instant;

/**
 * Contains methods for converting between types when accessing data in the Room database.
 */
public class SentimentsTypeConverters {

    /**
     * Converts a long timestamp into an Instant timestamp. The long timestamp should be a number
     * of epoch seconds.
     * @param timestamp The long to convert to Instant.
     * @return An Instant with the same timestamp.
     */
    @TypeConverter
    public static Instant longToInstant(long timestamp) {
        return Instant.ofEpochSecond(timestamp);
    }

    /**
     * Converts an Instant timestamp into a long timestamp.
     * @param timestamp The Instant to convert to long.
     * @return The number of seconds that have passed since the epoch.
     */
    @TypeConverter
    public static long instantToLong(Instant timestamp) {
        return timestamp.getEpochSecond();
    }
}
