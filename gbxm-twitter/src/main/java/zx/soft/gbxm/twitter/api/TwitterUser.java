package zx.soft.gbxm.twitter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private List<MonitorUser> getMonitorUsers(){
        return twitterDao.getMoitorUsers();
    }

    private void postDataByUser(String screenName){
        twitterCurrentUser.getUserTimeLine(screenName);
    }

    protected void postUserTweet() throws InterruptedException {
        List<MonitorUser> users = getMonitorUsers();
        logger.info("monitor user count is {} " , users.size());
        if(users.size()!=0 && !users.isEmpty()){
            for(MonitorUser user : users){
                postDataByUser(user.getScreenName());
            }
        }
        logger.info("start sleep ,please check it 0.5 hour's latter");
        Thread.sleep(30*60*1000L);
        postUserTweet();
    }

    public static void main(String[] args) {
        TwitterUser twitterUser = new TwitterUser();
        try {
            twitterUser.postUserTweet();
        } catch (InterruptedException e) {
            logger.error("Exception : {} " , e);
            e.printStackTrace();
        }
    }


}
