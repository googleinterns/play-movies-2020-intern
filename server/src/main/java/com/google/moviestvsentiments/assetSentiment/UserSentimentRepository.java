package com.google.moviestvsentiments.assetSentiment;

import org.springframework.data.repository.CrudRepository;

/**
 * A Repository that provides functions for accessing and modifying UserSentiment database records.
 */
public interface UserSentimentRepository extends CrudRepository<UserSentiment, UserSentiment.UserSentimentCompositeKey> {
}
