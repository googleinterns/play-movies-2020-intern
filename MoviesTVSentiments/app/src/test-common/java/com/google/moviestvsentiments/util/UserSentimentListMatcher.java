package com.google.moviestvsentiments.util;

import com.google.moviestvsentiments.model.UserSentiment;
import org.mockito.ArgumentMatcher;
import java.util.Arrays;
import java.util.List;

/**
 * An ArgumentMatcher that matches lists of UserSentiments.
 */
public class UserSentimentListMatcher implements ArgumentMatcher<List<UserSentiment>> {

    private List<UserSentiment> lhs;

    private UserSentimentListMatcher(List<UserSentiment> lhs) {
        this.lhs = lhs;
    }

    /**
     * Creates a new UserSentimentListMatcher that matches the given list of UserSentiments.
     * @param sentiments The list of UserSentiments to match.
     * @return A new UserSentimentListMatcher that matches the given list of UserSentiments.
     */
    public static UserSentimentListMatcher containsUserSentiments(UserSentiment ... sentiments) {
        return new UserSentimentListMatcher(Arrays.asList(sentiments));
    }

    @Override
    public boolean matches(List<UserSentiment> rhs) {
        if (lhs.size() != rhs.size()) {
            return false;
        }

        boolean result = true;
        for (int i = 0; i < lhs.size() && result; i++) {
            UserSentiment left = lhs.get(i);
            UserSentiment right = rhs.get(i);
            result = left.assetId.equals(right.assetId) &&
                    left.accountName.equals(right.accountName) &&
                    left.assetType == right.assetType &&
                    left.sentimentType == right.sentimentType &&
                    left.timestamp.equals(right.timestamp) &&
                    left.isPending == right.isPending;
        }
        return result;
    }
}
