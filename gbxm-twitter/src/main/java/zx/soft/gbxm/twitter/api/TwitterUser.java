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

    private TwitterDaoImpl twitterDao = new TwitterDaoImpl();
    private TwitterCurrentUser twitterCurrentUser = new TwitterCurrentUser();

    private List<MonitorUser> getMonitorUsers() {
        return twitterDao.getMoitorUsers();
    }

    private void postDataByUser(List<MonitorUser> users) throws InterruptedException {
        twitterCurrentUser.getUserTimeLine(users);
    }

    protected void postUserTweet() throws InterruptedException {
        List<MonitorUser> users = getMonitorUsers();
        logger.info("monitor user count is {} ", users.size());
        if (users.size() != 0 && !users.isEmpty()) {
            postDataByUser(users);
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
