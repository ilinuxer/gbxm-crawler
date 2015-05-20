package zx.soft.gbxm.twitter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zx.soft.utils.log.LogbackUtil;

/**
 * Created by jimbo on 5/5/15.
 */
public class TwitterUserSpider {
    private static Logger logger = LoggerFactory.getLogger(TwitterUserSpider.class);

    public static void main(String[] args) {
        logger.info("get twitter user's Tweet");
        TwitterUser twitterUser = new TwitterUser();
        try {
            twitterUser.postUserTweet();
        } catch (InterruptedException e) {
            logger.error("Exception :{} ", LogbackUtil.expection2Str(e));
            throw new RuntimeException(e);
        }
    }
}
