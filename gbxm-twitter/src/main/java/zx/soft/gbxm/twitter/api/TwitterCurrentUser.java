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

import java.io.IOException;
import java.util.*;

/**
 * Created by jimbo on 4/21/15.
 */
public class TwitterCurrentUser {
    private static Logger logger = LoggerFactory.getLogger(TwitterCurrentUser.class);
    private static TwitterDaoImpl twitterDaoImpl = new TwitterDaoImpl();
    private final LinkedList<String> URLs = getPostUrl();
    private int tokenNum = getTokens().size();

//    private final ClientResource clientResource1 = new ClientResource(URLs.get(0));
//    private final ClientResource clientResource2 = new ClientResource(URLs.get(1));
//    private final ClientResource clientResource3 = new ClientResource(URLs.get(2));
//    private final LinkedList<ClientResource> clientResources = setClientResources();

    /**
     * 获取post接口url
     */
    protected LinkedList<String> getPostUrl() {
        LinkedList<String> result = new LinkedList<>();
        String[] strings = PostUrlConfig.getProp("posturl.properties").getProperty("post.url").split(",");
        for (String url:strings){
            result.add(url);
        }
//        result.add(strings[0]);
//        result.add(strings[1]);
//        result.add(strings[2]);
        return result;
    }

    private LinkedList<ClientResource> setClientResources(){
        LinkedList<ClientResource> result = new LinkedList<>();
        for (String url : getPostUrl()){
            result.add(new ClientResource(url));
        }
        return result;
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
    public Twitter setTwitter(int i) throws InterruptedException {
        List<Token> tokens = getTokens();
        logger.info("token number {} " ,i);
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
                if (e.getErrorCode() == 88){
                    logger.info("token's limit is used , next token");
                    index++;
                    if (index>=tokenNum){
                        logger.info("tokens has used, Thread will sleep 0.25 hours");
                        Thread.sleep(1000 * 60 * 10L);
                        index = 0;
                    }
                    setTwitterUserTimeLine(index, i, users);
                }else {
                    logger.error("user {} has something wrong during get tweets",users.get(i).getScreenName());
                    continue;
                }
            }
        }
        logger.info("one round is over ,start sleep ,please check it 0.5 hour's latter");
        Thread.sleep(30 * 60 * 1000L);
        TwitterUser.postUserTweet();

    }

    /**
     * 获取新增用户的历史推文信息
     */
    protected void getUserTimeLine(List<MonitorUser> users) throws InterruptedException {
        int index = 0;
        for (int i = 0; i < users.size(); i++) {
            setTwitterUserTimeLine(index, i, users);
        }
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
    protected void currentUserStatusPost(List<RecordInfo> recordInfos){
        LinkedList<ClientResource> clientResources = setClientResources();
        final List<RecordInfo> params = recordInfos;
        for (int i=0;i<clientResources.size();i++){
            final ClientResource clientResource = clientResources.get(i);
            final Integer index = i;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    PostData postData = new PostData();
                    postData.setNum(params.size());
                    logger.info("post record number is : " + params.size());
                    postData.setRecords(params);
                    Representation entity = new StringRepresentation(JsonUtils.toJson(postData));
                    entity.setMediaType(MediaType.APPLICATION_JSON);

                    try {
                        Representation representation1 = clientResource.post(entity);
                        Response response1 = clientResource.getResponse();
                        try {
                            logger.info("{} :: {}" ,URLs.get(index),response1.getEntity().getText());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (ResourceException e) {
                        e.printStackTrace();
                        logger.error("post data to {} solr error ",URLs.get(index));
                        logger.error(e.getMessage());
                    }
                    clientResource.release();
                }
            },"Post"+i).start();


            System.out.println(Thread.activeCount());
        }
    }
//    protected void currentUserStatusPostGB(List<RecordInfo> recordInfos) {
//        PostData postData = new PostData();
//        postData.setNum(recordInfos.size());
//        logger.info("post GBXM record number is : " + recordInfos.size());
//        postData.setRecords(recordInfos);
//        Representation entity = new StringRepresentation(JsonUtils.toJson(postData));
//        entity.setMediaType(MediaType.APPLICATION_JSON);
//
//        try {
//            Representation representation1 = clientResource1.post(entity);
//            Response response1 = clientResource1.getResponse();
//            try {
//                logger.info("GBXM :: {}" ,response1.getEntity().getText());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (ResourceException e) {
//            e.printStackTrace();
//            logger.error("post data to GBXM solr error ");
//            logger.error(e.getMessage());
//        }
//        clientResource1.release();
//    }
    //将推文信息post到省厅slor接口
//    protected void currentUserStatusPostST(List<RecordInfo> recordInfos) {
//        PostData postData = new PostData();
//        postData.setNum(recordInfos.size());
//        logger.info("post STXM record number is : " + recordInfos.size());
//        postData.setRecords(recordInfos);
//        Representation entity = new StringRepresentation(JsonUtils.toJson(postData));
//        entity.setMediaType(MediaType.APPLICATION_JSON);
//
//        try {
//            logger.info("post to ST ::::  ");
//            Representation representation2 = clientResource2.post(entity);
//            Response response2 = clientResource2.getResponse();
//            try {
//                logger.info("STXM :: {}" ,response2.getEntity().getText());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (ResourceException e) {
//            e.printStackTrace();
//            logger.error("post data to STXM solr error ");
//        }
//        clientResource2.release();
//    }

//    protected void currentUserStatusPostGX(List<RecordInfo> recordInfos) {
//        PostData postData = new PostData();
//        postData.setNum(recordInfos.size());
//        logger.info("post GXQT record number is : " + recordInfos.size());
//        postData.setRecords(recordInfos);
//        Representation entity = new StringRepresentation(JsonUtils.toJson(postData));
//        entity.setMediaType(MediaType.APPLICATION_JSON);
//
//        try {
//            logger.info("post to GX ::::  ");
//            Representation representation3 = clientResource3.post(entity);
//            Response response3 = clientResource3.getResponse();
//            try {
//                logger.info("GXQT :: {}" ,response3.getEntity().getText());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (ResourceException e) {
//            e.printStackTrace();
//            logger.error("post data to GXQT solr error ");
//        }
//        clientResource3.release();
//    }

}
