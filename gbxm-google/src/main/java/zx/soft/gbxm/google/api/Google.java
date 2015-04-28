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
     * 传入gplus 应用列表，设置Plus 对象
     */
    private Plus setGplus(List<GoogleToken> tokens, int index) throws GeneralSecurityException, IOException {
        Plus result;
        GoogleToken token = tokens.get(index);
        result = setGplus(token);
        return result;
    }

    /**
     * 根据userId获取用户的推文信息
     */
    private ArrayList<GooglePlusStatus> getGoogeActivities(List<GoogleToken> tokens, int index, String userId, long lastUpdateTime) throws InterruptedException {
        ArrayList<GooglePlusStatus> result = new ArrayList<>();
        try {
            Plus plus = setGplus(tokens, index);
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
        } catch (Exception e) {
            logger.error("Exception : {} ", LogbackUtil.expection2Str(e));

            if (index < tokens.size()) {
                index++;
            } else {
                Thread.sleep(24 * 60 * 60 * 1000L);
                index = 0;
            }
            result = getGoogeActivities(tokens, index, userId, lastUpdateTime);
        }

        return result;
    }

    /**
     *
     */
    private void googleActivitiesAction(List<GoogleToken> tokens, int index, UserInfo userInfo) throws InterruptedException {
        List<RecordInfo> records = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        String userId = userInfo.getUserId();
        long lastUpdateTime = userInfo.getLastUpdateTime().getTime();
        ArrayList<GooglePlusStatus> userStatus = getGoogeActivities(tokens, index, userId,lastUpdateTime);
        if(userStatus.size()>0){
            for(GooglePlusStatus status:userStatus){
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
     *
     */
    private void googleActivitiesAction(UserInfo userInfo) throws InterruptedException {
        List<GoogleToken> tokens = getAppsInfo();
        int index = 0;
        googleActivitiesAction(tokens,index,userInfo);
    }

    /**
     * 获取分页内的监控用户列表
     */
    private List<UserInfo> getGplusUserInfos(int index, int size) {
        List<UserInfo> result;
        result = daoImpl.getUsers(index, size);
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
//        System.out.println(JsonUtils.toJson(google.getAppsInfo()));

    }
}
