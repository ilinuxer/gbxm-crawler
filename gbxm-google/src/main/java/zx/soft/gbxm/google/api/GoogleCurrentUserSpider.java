package zx.soft.gbxm.google.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jimbo on 4/22/15.
 */
public class GoogleCurrentUserSpider {
    private static Logger logger = LoggerFactory.getLogger(GoogleCurrentUserSpider.class);

    public static void main(String[] args) {
        GoogleCurrentUser currentUser = new GoogleCurrentUser();
        currentUser.postUserTweet();
    }
}
