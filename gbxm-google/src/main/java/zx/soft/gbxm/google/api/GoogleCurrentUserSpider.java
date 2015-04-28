package zx.soft.gbxm.google.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zx.soft.utils.log.LogbackUtil;

/**
 * Created by jimbo on 4/22/15.
 */
public class GoogleCurrentUserSpider {
    private static Logger logger = LoggerFactory.getLogger(GoogleCurrentUserSpider.class);

    public static void main(String[] args) {
        logger.info("获取Gplus新增用户历史推文信息");
        GoogleCurrentUser currentUser = new GoogleCurrentUser();
        try {
            currentUser.postUserTweet();
        } catch (InterruptedException e) {
            logger.error("Exception : {} " + LogbackUtil.expection2Str(e));
            throw new RuntimeException(e);
        }
    }
}
