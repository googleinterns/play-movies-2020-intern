package com.google.moviestvsentiments.assetSentiment;

import java.util.List;

/**
 * Represents the response type of the update UserSentiments endpoint.
 */
public class UpdateSentimentResponse {

    private List<UserSentiment> sentiments;
    private String error;

    private UpdateSentimentResponse(List<UserSentiment> sentiments, String error) {
        this.sentiments = sentiments;
        this.error = error;
    }

    /**
     * Creates a new UpdateSentimentResponse with the given UserSentiment list and error message.
     * @param sentiments The list of UserSentiments that were successfully updated.
     * @param error The error message that occurred when updating the UserSentiments.
     * @return A new UpdateSentimentResponse with the given UserSentiment list and error message.
     */
    public static UpdateSentimentResponse create(List<UserSentiment> sentiments, String error) {
        return new UpdateSentimentResponse(sentiments, error);
    }

    /**
     * Returns the list of UserSentiments that were updated successfully.
     */
    public List<UserSentiment> getSentiments() {
        return sentiments;
    }

    /**
     * Returns the message of the error the occurred when updating the UserSentiments.
     */
    public String getError() {
        return error;
    }
}
