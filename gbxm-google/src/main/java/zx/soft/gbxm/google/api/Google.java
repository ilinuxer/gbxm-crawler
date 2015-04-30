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
import zx.soft.gbxm.google.common.ConstUtils;
import zx.soft.gbxm.google.common.Convert;
import zx.soft.gbxm.google.common.RestletPost;
import zx.soft.gbxm.google.dao.GoogleDaoImpl;
import zx.soft.gbxm.google.domain.*;
import zx.soft.utils.json.JsonUtils;
import zx.soft.utils.log.LogbackUtil;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jimbo on 4/23/15.
 */
public class Google {
    private static Logger logger = LoggerFactory.getLogger(Google.class);

    private static FileDataStoreFactory dataStoreFactory;
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    protected GoogleAuthorizationCodeFlow flow;

    private static final int SIZE_USER_COUNT = 100;

    protected GoogleDaoImpl daoImpl = new GoogleDaoImpl();

    /**
     * 获取google应用列表
     */
    private List<GoogleToken> getAppsInfo() {
        return daoImpl.getGoogleTokens();
    }

    /**
     * 设置获取数据的plus
     */
    private Plus setGplus(GoogleToken token) throws GeneralSecurityException, IOException {
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
            if (activity.getPublished().getValue() <= lastUpdateTime) {
                break;
            }
            result.add(Convert.convertActivity2GPS(activity));
        }

        return result;
    }

    /**
     * 获取监控用户推文信息
     *
     * @param token
     * @param userInfo
     * @throws InterruptedException
     */
    private void googleActivitiesAction(GoogleToken token, UserInfo userInfo) throws InterruptedException, GeneralSecurityException, IOException {

        List<RecordInfo> records = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        String userId = userInfo.getUserId();
        long lastUpdateTime = userInfo.getLastUpdateTime().getTime();
        ArrayList<GooglePlusStatus> userStatus = getGoogeActivities(token, userId, lastUpdateTime);
        if (userStatus.size() > 0) {
            for (GooglePlusStatus status : userStatus) {
                records.add(Convert.convertGPS2Record(status, currentTime));
            }
            PostData data = new PostData();
            data.setNum(records.size());
            data.setRecords(records);
            //post并将数据插入数据库
            RestletPost.post(data);
            daoImpl.insertGooglePlusListStatus(userStatus);
        }
    }

    /**
     * 一个token对应多个用户
     */
    private void googleActivitiesAction(GoogleToken token, List<UserInfo> userInfos) throws InterruptedException, GeneralSecurityException, IOException {
        for (UserInfo userInfo : userInfos) {
            googleActivitiesAction(token, userInfo);
        }
    }

    /**
     *
     */
    private void googleActivitiesAction(List<GoogleToken> tokens){
        int userCount = getUserCount();
        int tokensCount = tokens.size();
        int count = userCount/(tokensCount-1);
        logger.info("{} users each token has",count);
        //count 每个token分到多少用户
        int i = 0;
        while(i<=count){

        }
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
     * 获取监控用户的数量
     */
    private int getUserCount() {
        return daoImpl.getUserCount();
    }


    public static void main(String[] args) {
        Google google = new Google();
        System.out.println(google.getUserCount());
        System.out.println(google.getAppsInfo().size());
        int size = google.getUserCount() / google.getAppsInfo().size();

//        System.out.println(JsonUtils.toJson(google.getAppsInfo()));

    }
}
