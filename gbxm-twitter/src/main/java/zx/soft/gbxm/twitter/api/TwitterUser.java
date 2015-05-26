package zx.soft.gbxm.twitter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterException;
import zx.soft.gbxm.twitter.dao.TwitterDaoImpl;
import zx.soft.gbxm.twitter.domain.MonitorUser;

import java.util.List;

/**
 * Created by jimbo on 4/27/15.
 */
public class TwitterUser {
    private static Logger logger = LoggerFactory.getLogger(TwitterUser.class);

    private static TwitterDaoImpl twitterDao = new TwitterDaoImpl();
    private static TwitterCurrentUser twitterCurrentUser = new TwitterCurrentUser();

    private static List<MonitorUser> getMonitorUsers() {
        return twitterDao.getMoitorUsers();
    }

    private static void postDataByUser(List<MonitorUser> users) throws InterruptedException {
        twitterCurrentUser.getUserTimeLine(users);
    }

    public static void postUserTweet() throws InterruptedException {
        List<MonitorUser> users = getMonitorUsers();
        logger.info("monitor user count is {} ", users.size());
        if (users.size() != 0 && !users.isEmpty()) {
            postDataByUser(users);

            logger.info("one round is over ,start sleep ,please check it 0.5 hour's latter");
            Thread.sleep(30 * 60 * 1000L);
            postUserTweet();
        } else {
            logger.info("there has no monitor user ");
            Thread.sleep(1 * 60 * 60 * 1000L);
        }
    }

    public static void main(String[] args) {
        TwitterUser twitterUser = new TwitterUser();
        try {
            twitterUser.postUserTweet();
        } catch (InterruptedException e) {
            logger.error("Exception : {} ", e);
            e.printStackTrace();
        }
    }
}
