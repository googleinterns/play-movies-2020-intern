package com.google.moviestvsentiments.assetSentiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A controller that handles requests related to Assets and UserSentiments.
 */
@RestController
public class AssetSentimentController {

    @Autowired
    private AssetSentimentRepository assetSentimentRepository;

    @Autowired
    private UserSentimentRepository userSentimentRepository;

    /**
     * Returns a list of AssetSentiments that match the given AssetType, account name and SentimentType.
     * @param assetType The type of Asset to match.
     * @param accountName The name of the account to use when checking for user sentiments.
     * @param sentimentType The type of user sentiment to match.
     * @return A list of matching AssetSentiments.
     */
    @GetMapping("/assets")
    public List<AssetSentiment> getAssets(@RequestParam("assetType") AssetType assetType,
                                          @RequestParam("accountName") String accountName,
                                          @RequestParam("sentimentType") SentimentType sentimentType) {
        List<AssetSentiment> withReaction = assetSentimentRepository.getAssetsWithSentiment(assetType, accountName,
                sentimentType);
        if (sentimentType != SentimentType.UNSPECIFIED) {
            return withReaction;
        }

        List<AssetSentiment> withoutReaction = assetSentimentRepository.getAssetsWithoutSentiment(assetType, accountName);
        withReaction.addAll(withoutReaction);
        return withReaction;
    }

    /**
     * Inserts or replaces the UserSentiment with the given fields into the user sentiments table. If the UserSentiment
     * is saved successfully, the saved version is returned. If the user sentiment cannot be saved, an error message is
     * returned.
     * @param accountName The name of the account associated with the UserSentiment.
     * @param assetId The id of the asset associated with the UserSentiment.
     * @param assetType The type of the asset associated with the UserSentiment.
     * @param sentimentType The type of the UserSentiment.
     * @param timestamp The timestamp of the UserSentiment.
     * @return A ResponseEntity with either the saved UserSentiment or the error message.
     */
    @PutMapping("/sentiment")
    public ResponseEntity updateSentiment(@RequestParam String accountName, @RequestParam String assetId,
                                          @RequestParam AssetType assetType, @RequestParam SentimentType sentimentType,
                                          @RequestParam Instant timestamp) {
        try {
            UserSentiment userSentiment = UserSentiment.create(accountName, assetId, assetType, sentimentType, timestamp);
            userSentiment = userSentimentRepository.save(userSentiment);
            return ResponseEntity.ok().body(userSentiment);
        } catch (JpaSystemException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Updates the given list of UserSentiments in the database and returns the list of successfully updated
     * UserSentiments. If a UserSentiment does not already exist in the database, then it will be added. If the
     * UserSentiments can not be saved, then an error message is returned.
     * @param sentiments The list of UserSentiments to update.
     * @return A ResponseEntity with either the updated UserSentiments or the error message.
     */
    @PutMapping("/sentiments")
    public ResponseEntity<UpdateSentimentResponse> updateSentiments(@RequestBody List<UserSentiment> sentiments) {
        try {
            Iterable<UserSentiment> iterable = userSentimentRepository.saveAll(sentiments);
            List<UserSentiment> savedSentiments = StreamSupport.stream(iterable.spliterator(), false)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(UpdateSentimentResponse.create(savedSentiments, null));
        } catch (JpaSystemException e) {
            return ResponseEntity.badRequest().body(UpdateSentimentResponse.create(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    UpdateSentimentResponse.create(null, e.getMessage()));
        }
    }
}
