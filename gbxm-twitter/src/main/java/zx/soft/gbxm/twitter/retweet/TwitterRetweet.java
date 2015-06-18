package zx.soft.gbxm.twitter.retweet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import zx.soft.gbxm.twitter.api.Follows;
import zx.soft.gbxm.twitter.api.TwitterCurrentUser;

/**
 * Created by jimbo on 5/26/15.
 */
public class TwitterRetweet {
    private static Logger logger = LoggerFactory.getLogger(TwitterRetweet.class);

    private TwitterCurrentUser currentUser = new TwitterCurrentUser();

    private Twitter getTwitter(int tokenIndex) throws InterruptedException {
        return currentUser.setTwitter(tokenIndex);
    }

    protected void getRetweetIds(Twitter twitter, String statuId) throws TwitterException {
        Follows follows = new Follows(twitter);
        follows.getRetweet(statuId);
    }


    public static void main(String[] args) throws InterruptedException, TwitterException {
        TwitterRetweet retweet = new TwitterRetweet();
        Twitter twitter = retweet.getTwitter(2);
        retweet.getRetweetIds(twitter,"516272718426038272");
    }
}
