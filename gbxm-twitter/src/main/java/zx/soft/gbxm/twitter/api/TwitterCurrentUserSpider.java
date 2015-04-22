package zx.soft.gbxm.twitter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jimbo on 4/22/15.
 */
public class TwitterCurrentUserSpider {

    private static Logger logger = LoggerFactory.getLogger(TwitterCurrentUserSpider.class);

    /**
     *
     */
    public static void main(String[] args) {
        TwitterCurrentUser currentUser = new TwitterCurrentUser();
        currentUser.postUserTweet();
    }

}
