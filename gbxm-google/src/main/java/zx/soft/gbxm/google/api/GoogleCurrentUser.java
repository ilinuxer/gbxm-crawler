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
import zx.soft.gbxm.google.domain.GooglePlusStatus;
import zx.soft.gbxm.google.domain.GoogleToken;
import zx.soft.gbxm.google.domain.PostData;
import zx.soft.gbxm.google.domain.RecordInfo;
import zx.soft.model.user.CurrentUserInfo;
import zx.soft.utils.log.LogbackUtil;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jimbo on 4/22/15.
 */
public class GoogleCurrentUser {
    private static Logger logger = LoggerFactory.getLogger(GoogleCurrentUser.class);

    private static FileDataStoreFactory dataStoreFactory;
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    protected GoogleAuthorizationCodeFlow flow;

    protected GoogleDaoImpl daoImpl = new GoogleDaoImpl();

    /**
     * 获取google＋应用列表
     */
    public List<GoogleToken> getAppsInfo() {
        return daoImpl.getGoogleTokens();
    }

    /**
     * 配置plus
     */
    public Plus getGoolgePlus(GoogleToken token) throws GeneralSecurityException, IOException {
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
     * 获取配置后的plus
     */
    private Plus getGooglePlus(List<GoogleToken> tokens, int index) {
        Plus result;
        GoogleToken token = tokens.get(index);
        try {
            result = getGoolgePlus(token);
        } catch (Exception e) {
            logger.error("新建google flow plus 时出错");
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 根据用户id获取用户的历史推文信息
     */
    private ArrayList<GooglePlusStatus> getGoogeActivities(List<GoogleToken> tokens, int index, String userId) throws InterruptedException {
        ArrayList<GooglePlusStatus> result = new ArrayList<>();
        try {
            Plus plus = getGooglePlus(tokens, index);
            Plus.Activities.List activities = plus.activities().list(userId, "public");
            activities.setMaxResults(100L);//最大可设置为100
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
                result.add(Convert.convertActivity2GPS(activity));
            }
        } catch (Exception e) {
            logger.error("Exception : {} ", LogbackUtil.expection2Str(e));

            if (index < tokens.size()) {
                index++;
            } else {
                Thread.sleep(24 * 60 * 60 * 1000L);
                index = 0;
            }
            result = getGoogeActivities(tokens, index, userId);
        }
        return result;
    }

    /**
     * 根据用户id获取用户的历史推文信息
     */
    private ArrayList<GooglePlusStatus> getGoogleActivities(String userId) {
        List<GoogleToken> tokens = getAppsInfo();
        ArrayList<GooglePlusStatus> result;

        int index = 0;
        try {
            result = getGoogeActivities(tokens, index, userId);
        } catch (InterruptedException e) {
            logger.error("Exception : {}", LogbackUtil.expection2Str(e));
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 根据用户userId 获取用户历史推文信息，并将其post 到指定的接口
     */
    private void getGoogleActivitiesAndPost(String userId) {
        List<GooglePlusStatus> statuses = getGoogleActivities(userId);
        long currentTime = System.currentTimeMillis();
        logger.info("get {} 's tweet " ,userId);
        if (statuses != null && statuses.size() > 0) {
            List<RecordInfo> records = new ArrayList<>();
            for (GooglePlusStatus status : statuses) {
                records.add(Convert.convertGPS2Record(status, currentTime));
            }
            logger.info("post record number is " + records.size());
            PostData data = new PostData();
            data.setNum(records.size());
            data.setRecords(records);
            RestletPost.post(data);
        }
        daoImpl.updatedUserInfo(userId,new Timestamp(currentTime));
    }

    /**
     * 获取新增Google用户信息列表
     */
    public List<CurrentUserInfo> getCurrentUser() {
        List<CurrentUserInfo> result;
        result = daoImpl.getGpCurrentUser();
        return result;
    }

    /**
     * 查找新增用户并获取其历史推文信息post到指定接口
     */
    public void postUserTweet() throws InterruptedException {
        List<CurrentUserInfo> users = getCurrentUser();
        logger.info("本次查找到新增用户数量为 ：" + users.size());
        if (users.size() != 0) {
            for (CurrentUserInfo user : users) {
                String userId = user.getUserId();
                String userName = user.getUserName();
                getGoogleActivitiesAndPost(userId);
                daoImpl.delGpCurrentUserById(userId);
            }
        }
        logger.info("start sleep ,please check it 0.5 hour's latter");
        Thread.sleep(30 * 60 * 1000L);
        postUserTweet();
    }


    public static void main(String[] args) {
        GoogleCurrentUser spider = new GoogleCurrentUser();
        try {
            spider.postUserTweet();
        } catch (InterruptedException e) {
            logger.error("Thread sleep error");
            e.printStackTrace();
        }
    }

}
