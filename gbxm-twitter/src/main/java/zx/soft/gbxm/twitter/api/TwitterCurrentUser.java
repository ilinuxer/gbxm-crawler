package zx.soft.gbxm.twitter.api;

import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import zx.soft.gbxm.twitter.dao.TwitterDaoImpl;
import zx.soft.gbxm.twitter.domain.MonitorUser;
import zx.soft.gbxm.twitter.domain.PostData;
import zx.soft.gbxm.twitter.domain.RecordInfo;
import zx.soft.gbxm.twitter.domain.Token;
import zx.soft.gbxm.twitter.utils.PostUrlConfig;
import zx.soft.gbxm.twitter.utils.TimeUtil;
import zx.soft.model.user.CurrentUserInfo;
import zx.soft.utils.checksum.CheckSumUtils;
import zx.soft.utils.config.ConfigUtil;
import zx.soft.utils.json.JsonUtils;
import zx.soft.utils.log.LogbackUtil;

import java.io.IOException;
import java.util.*;

/**
 * Created by jimbo on 4/21/15.
 */
public class TwitterCurrentUser {
    private static Logger logger = LoggerFactory.getLogger(TwitterCurrentUser.class);
    private static TwitterDaoImpl twitterDaoImpl = new TwitterDaoImpl();
    private final ClientResource clientResource = new ClientResource(URL);
    private static final String URL = getPostUrl();

    /**
     * 获取post接口url
     */
    protected static String getPostUrl() {
        return PostUrlConfig.getProp("posturl.properties").getProperty("post.url");
    }

    /**
     * 获取数据库中所有的twitter token
     */
    private List<Token> getTokens() {
        return twitterDaoImpl.getTwitterTokens();
    }

    /**
     * 设置Twitter 实例
     */
    private Twitter setTwitter(int i) throws InterruptedException {
        List<Token> tokens = getTokens();
        if (i >= tokens.size()) {
            logger.info("tokens has used, Thread will sleep 0.5 hours");
            Thread.sleep(1000 * 60 * 15L);
            i = 0;
            setTwitter(i);
        }
        Token token = tokens.get(i);
        Properties properties = ConfigUtil.getProps("oauthconsumer.properties");
        Twitter result = new TwitterFactory().getInstance();
        result.setOAuthConsumer(properties.getProperty("consumerKey"), properties.getProperty("consumerSecret"));
        result.setOAuthAccessToken(new AccessToken(token.getTokenkey(), token.getTokensecret()));
        return result;
    }

    /**
     * 获取用户推文信息并POST
     */
    protected void getUserTimeLineIn(Twitter twitter, String screenName) throws TwitterException {
        Follows follows = new Follows(twitter);
        follows.getUserTimeLine(screenName);
    }

    /**
     * 设置twitter并获取用户推文信息post
     */
    protected void setTwitterUserTimeLine(int index, int usersIndex, List<MonitorUser> users) throws InterruptedException {
        logger.info("This is token number {}", index);
        Twitter twitter = setTwitter(index);
        for (int i = usersIndex; i < users.size(); i++) {
            String userScreenName = users.get(i).getScreenName();
            try {
                getUserTimeLineIn(twitter, userScreenName);
            } catch (TwitterException e) {
                logger.info("token's limit is used , next token");
                index++;
                setTwitterUserTimeLine(index, i, users);
            }
        }
    }

    /**
     * 获取新增用户的历史推文信息
     */
    protected void getUserTimeLine(List<MonitorUser> users) throws InterruptedException {
        int index = 0;
        for (int i = 0; i < users.size(); i++) {
            setTwitterUserTimeLine(index, i, users);
        }
        logger.info("start sleep ,please check it 0.5 hour's latter");
        Thread.sleep(30 * 60 * 1000L);

    }

    /**
     * 转化获取用户的推文信息，并将其转换做接口需求的格式
     */
    protected List<RecordInfo> exchageTweet(List<Status> statuses) {
        List<RecordInfo> result = new ArrayList<>();
        List<TwitterStatus> twitterStatuses = new ArrayList<>();
        Iterator<Status> iterator = statuses.iterator();
        while (iterator.hasNext()) {
            TwitterStatus twitterStatus = new TwitterStatus();
            RecordInfo record = new RecordInfo();
            Status status = iterator.next();
            twitterStatus.setId(status.getId());
            twitterStatus.setUser_id(status.getUser().getId());
            twitterStatus.setScreen_name(status.getUser().getName());

            if (status.getGeoLocation() != null) {
                twitterStatus.setLatitude(status.getGeoLocation().getLatitude());
                twitterStatus.setLongitude(status.getGeoLocation().getLongitude());
            }

            twitterStatus.setCreated_at(status.getCreatedAt().toString());
            twitterStatus.setText(status.getText());
            twitterStatus.setRetweet_count(status.getRetweetCount());
            twitterStatus.setPossibly_sensitive(status.isPossiblySensitive());
            if (status.getPlace() != null) {
                twitterStatus.setLocation(status.getPlace().getFullName());
            }
            if (status.getRetweetedStatus() != null) {
                twitterStatus.setRetweeted_id(status.getRetweetedStatus().getId());
                twitterStatus.setRetweeted_user_id(status.getRetweetedStatus().getUser().getId());
                twitterStatus.setRetweeted_screen_name(status.getRetweetedStatus().getUser().getScreenName());
            }
            twitterStatuses.add(twitterStatus);

            String url = "https://twitter.com/" + status.getUser().getScreenName() + "/status/"
                    + twitterStatus.getId();
            record.setId(CheckSumUtils.getMD5(url).toUpperCase());//状态id,用户id进行MD5加密
            record.setMid(Long.toString(twitterStatus.getId()));//主id
            record.setUsername(twitterStatus.getUser_id() + ""); // uid
            record.setNickname(twitterStatus.getScreen_name()); //用户昵称
            record.setOriginal_id(Long.toString(twitterStatus.getRetweeted_id())); //原创记录id
            record.setOriginal_uid(Long.toString(twitterStatus.getRetweeted_user_id())); //原创用户id
            record.setOriginal_name(twitterStatus.getRetweeted_screen_name()); //原创用户昵称
            record.setUrl(url);//url
            record.setContent(twitterStatus.getText()); //该记录内容
            //评论数置零
            record.setComment_count(0);
            record.setRepost_count(twitterStatus.getRetweet_count());//转发数
            record.setTimestamp(TimeUtil.exchangeTime(status.getCreatedAt().getTime()));//该记录发布时间
            long currentTime = TimeUtil.exchangeTime(System.currentTimeMillis());
            record.setLasttime(currentTime);//lasttime
            record.setUpdate_time(currentTime); //update_time
            record.setFirst_time(currentTime); //first_time
            record.setLocation(twitterStatus.getLocation());//该记录发布的地理位置信息
            result.add(record);
        }
        return result;
    }

    /**
     * 将用户推文信息post到指定接口
     */
    protected void currentUserStatusPost(List<RecordInfo> recordInfos) {
        PostData postData = new PostData();
        postData.setNum(recordInfos.size());
        logger.info("post record number is : " + recordInfos.size());
        postData.setRecords(recordInfos);
        Representation entity = new StringRepresentation(JsonUtils.toJson(postData));
        entity.setMediaType(MediaType.APPLICATION_JSON);

        try {
            Representation representation = clientResource.post(entity);
            Response response = clientResource.getResponse();
            try {
                logger.info(response.getEntity().getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ResourceException e) {
            logger.error("post data to solr error ");
            logger.error(e.getMessage());
        }
    }
}
