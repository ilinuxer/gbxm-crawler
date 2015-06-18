package zx.soft.gbxm.google.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zx.soft.gbxm.google.common.Convert;
import zx.soft.gbxm.google.common.RestletPost;
import zx.soft.gbxm.google.dao.GoogleDaoImpl;
import zx.soft.gbxm.google.domain.*;
import zx.soft.utils.json.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jimbo on 4/23/15.
 */
public class GoogleUser {
    private static Logger logger = LoggerFactory.getLogger(GoogleUser.class);

    private static FileDataStoreFactory dataStoreFactory;
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    protected GoogleAuthorizationCodeFlow flow;

    private static final long SIZE_HISTORY_COUNT = 100L;

    protected GoogleDaoImpl daoImpl = new GoogleDaoImpl();
    protected GoogleCurrentUser currentUser = new GoogleCurrentUser();

    /**
     * 设置获取数据的plus
     */
    protected Plus setGplus(GoogleToken token) throws GeneralSecurityException, IOException {
        Plus result;
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        String appName = token.getApp_name();
        String clientId = token.getClient_id();
        String clientSecret = token.getClient_secret();

        File credentialsDir = new File(System.getProperty("user.home"), ".gplus/" + appName);
        dataStoreFactory = new FileDataStoreFactory(credentialsDir);
        flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientId, clientSecret,
                Collections.singleton(PlusScopes.PLUS_ME)).setDataStoreFactory(dataStoreFactory).build();
        Credential credential = flow.loadCredential("user");

        credential.refreshToken();
        credential.getAccessToken();
        result = new Plus.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(appName).build();
        return result;
    }


    /**
     * 根据userId获取用户的推文信息
     */
    private ArrayList<GooglePlusStatus> getGoogeActivities(GoogleToken token, String userId, long lastUpdateTime) throws InterruptedException, GeneralSecurityException, IOException {
        ArrayList<GooglePlusStatus> result = new ArrayList<>();

        Plus plus = setGplus(token);
        Plus.Activities.List activities = plus.activities().list(userId, "public");
        activities.setMaxResults(SIZE_HISTORY_COUNT);//最大可设置为100

        //设置获取推文信息参数
        activities.setFields("nextPageToken,items(id,title,published,updated,url,actor/id,actor/displayName,"
                + "object/id,object/actor/id,object/actor/displayName,object/originalContent,object/url,"
                + "object/replies/totalItems,object/plusoners/totalItems,object/resharers/totalItems,"
                + "object/attachments,annotation,geocode,placeName)");
        ActivityFeed feed = activities.execute();

        if (feed.getItems() == null | feed.getItems().isEmpty()) {
            return null;
        }

        for (Activity activity : feed.getItems()) {
            if (activity.getPublished().getValue() <= lastUpdateTime) {
                break;
            }
            result.add(Convert.convertActivity2GPS(activity));
        }
        return result;
    }

    /**
     * 获取监控用户推文信息(一对一)
     */
    private void googleActivitiesSingle(GoogleToken token, UserInfo userInfo) throws InterruptedException, GeneralSecurityException, IOException {

        List<RecordInfo> records = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        String userId = userInfo.getUserId();
//        String userId = "115765325971597823747";
        long lastUpdateTime = userInfo.getLastUpdateTime().getTime();
        ArrayList<GooglePlusStatus> userStatus = getGoogeActivities(token, userId, lastUpdateTime);
        if (userStatus == null || userStatus.size() == 0) {
            logger.info("google+ user {} 's tweets number is 0", userInfo.getUserName());
        } else if (userStatus.size() > 0) {
            for (GooglePlusStatus status : userStatus) {
                records.add(Convert.convertGPS2Record(status, currentTime));
            }
            PostData data = new PostData();
            data.setNum(records.size());
            data.setRecords(records);
            //post并将数据插入数据库
            RestletPost.post(data);
            logger.info("google+ user {} 's tweets number is " + records.size(), userInfo.getUserName());
        }

        daoImpl.updatedUserInfo(userId, new Timestamp(zx.soft.gbxm.google.timeutils.TimeUtils.exchangeTime(currentTime)));
    }

    /**
     * 一对多(此处应该设为多线程，暂时未实现)
     */
    private void googleActivitiesByToken(GoogleToken token, List<UserInfo> userInfos) throws InterruptedException, GeneralSecurityException, IOException {
        for (UserInfo userInfo : userInfos) {
            logger.info("get {} 's tweet ", userInfo.getUserName());
            googleActivitiesSingle(token, userInfo);
        }
    }

    /**
     * 获取监控用户的数量
     */
    private int getUserCount() {
        return daoImpl.getUserCount();
    }

    /**
     * 获取分页内的监控用户列表
     */
    private List<UserInfo> getGplusUserInfos(int start, int size) {
        List<UserInfo> result;
        result = daoImpl.getUsers(start, size);
        return result;
    }

    /**
     *
     */
    private void googleActivitiesMutils(List<GoogleToken> tokens) throws InterruptedException {
        int userCount = getUserCount();
        int tokensCount = tokens.size();
        if (userCount <= tokensCount * 10) {
            List<UserInfo> users = getGplusUserInfos(0, userCount);
            logger.info("there has {} users",userCount);
            try {
                googleActivitiesByToken(tokens.get(0), users);
            } catch (Exception e) {
                logger.error("this token is over limited ");
            }
        } else {

            int count = userCount / (tokensCount - 1);
            logger.info("{} users each token has", count);
            for (int i = 0; i < tokensCount; i++) {
                logger.info("this is token number {}",i);
                GoogleToken token = tokens.get(i);
                List<UserInfo> users = getGplusUserInfos((count * i), count);
                try {
                    googleActivitiesByToken(token, users);
                } catch (Exception e) {
                    logger.error("this token is over limited ");
                    continue;
                }
            }

        }
        logger.info("start sleep ,please check it 1 hour's latter");
        Thread.sleep(60 * 60 * 1000L);
        googleActivitiesMutils(tokens);
    }

    private void googleAction() throws InterruptedException {
        List<GoogleToken> tokens = currentUser.getAppsInfo();
        googleActivitiesMutils(tokens);
    }


    public static void main(String[] args) {
        GoogleUser google = new GoogleUser();
        try {
            google.googleAction();
        } catch (InterruptedException e) {
            logger.error("Thread sleep exception :{}", e);
            throw new RuntimeException(e);
        }

    }
}
