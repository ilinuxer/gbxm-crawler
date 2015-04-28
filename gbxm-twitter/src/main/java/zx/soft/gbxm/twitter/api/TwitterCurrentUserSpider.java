package zx.soft.gbxm.twitter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zx.soft.utils.log.LogbackUtil;

/**
 * Created by jimbo on 4/22/15.
 */
public class TwitterCurrentUserSpider {

    private static Logger logger = LoggerFactory.getLogger(TwitterCurrentUserSpider.class);

    public static void main(String[] args) {
        logger.info("get current user's history Tweet");
        TwitterCurrentUser currentUser = new TwitterCurrentUser();
        try {
            currentUser.postUserTweet();
        } catch (InterruptedException e) {
            logger.error("Exception :{} ", LogbackUtil.expection2Str(e));
            throw new RuntimeException(e);
        }
    }
}
