package na.knowyourreps;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.twitter.sdk.android.core.models.Tweet;

/**
 * Created by Micah on 2/29/2016.
 */
public class Representative {
    public Representative(String name, String email, String website, String governmentSeat,
                          String party, String endOfTerm, String twitterId,
                          String imageQueryUrl, String bioguideId) {
        this.name = name;
        this.email = email;
        this.website = website;
        this.governmentSeat = governmentSeat;
        this.endOfTerm = endOfTerm;
        this.twitterId = twitterId;
        this.party = party;
        this.imageQueryUrl= imageQueryUrl;
        this.bioguideId = bioguideId;
    }

    private String name;
    private String email;
    private String website;
    private String governmentSeat;
    private String endOfTerm;
    private String twitterId;
    private String party;
    private Bitmap image;
    private String imageQueryUrl;
    private String bioguideId;
    private Long mostRecentTweetId;
    private Tweet mostRecentTweet;

    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
    public String getWebsite() {
        return this.website;
    }
    public String getGovernmentSeat() {
        return this.governmentSeat;
    }
    public String getParty() {
        return this.party;
    }
    public String getEndOfTerm() {
        return this.endOfTerm;
    }
    public String getTwitterId() {
        return this.twitterId;
    }
    public Bitmap getImage() {
        return this.image;
    }
    public String getImageQueryUrl() {
        return this.imageQueryUrl;
    }
    public String getBioguideId() {
        return this.bioguideId;
    }
    public Long getMostRecentTweetId() {
        return mostRecentTweetId;
    }
    public Tweet getMostRecentTweet() {
        return mostRecentTweet;
    }
    public void setMostRecentTweetId(Long id) {
        mostRecentTweetId = id;
    }
    public void setMostRecentTweet(Tweet tweet) {
        mostRecentTweet = tweet;
    }
    public void setImage(Bitmap img) {
        this.image = img;
    }

    //Bundling Method

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("name", name);
        b.putString("governmentSeat", governmentSeat);
        b.putString("endOfTerm", endOfTerm);
        b.putString("party", party);
        b.putString("bioguideId", bioguideId);
        b.putString("imageQueryUrl", imageQueryUrl);

        return b;
    }
}
