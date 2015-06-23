package zx.soft.gbxm.google.status;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Comment;
import com.google.api.services.plus.model.CommentFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zx.soft.gbxm.google.api.GoogleCurrentUser;
import zx.soft.gbxm.google.common.Convert;
import zx.soft.gbxm.google.common.RestletPost;
import zx.soft.gbxm.google.dao.GoogleDaoImpl;
import zx.soft.gbxm.google.domain.GoogleToken;
import zx.soft.gbxm.google.domain.PostData;
import zx.soft.gbxm.google.domain.RecordInfo;
import zx.soft.utils.json.JsonUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jimbo on 5/3/15.
 */
public class GoogleStatus {

    private static Logger logger = LoggerFactory.getLogger(GoogleStatus.class);

    protected GoogleDaoImpl daoImpl = new GoogleDaoImpl();

    private GoogleCurrentUser googleCurrentUser = new GoogleCurrentUser();

    /**
     * 传入Token来设置plus
     */
    private Plus setGplus(GoogleToken token) throws GeneralSecurityException, IOException {
        Plus result;
        result = googleCurrentUser.getGoolgePlus(token);
        return result;
    }

    /**
     * 传入tokens 和 index 设置plus
     */
    private Plus setGplus(List<GoogleToken> tokens,int index) throws InterruptedException {
        Plus plus;
        try{
            GoogleToken token = tokens.get(index);
            plus = setGplus(token);
        }catch (Exception e){
            logger.info("set plus error ,get next token");
            if(index < tokens.size()-1){
                index++;
                plus = setGplus(tokens,index);
            }else {
                Thread.sleep(15*60*1000);
                index = 0;
                plus = setGplus(tokens,index);
            }
        }
        return plus;
    }

    /**
     * 传入token列表，设置Plus
     *
     */
    private Plus setGplus(List<GoogleToken> tokens){
        int index = 0;
        Plus result;
        try {
            result = setGplus(tokens,index);
        } catch (InterruptedException e) {
            logger.error("error during set Plus");
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 获取指定status的id来获取其评论转发信息
     */
    protected List<Comment> getTweetStatus(Plus plus,String statusId) throws GeneralSecurityException, IOException {

        List<Comment> comments = null;
        Plus.Comments.List commentList = plus.comments().list(statusId);
        commentList.setMaxResults(200L);
        CommentFeed commentFeed = commentList.execute();
        if (commentFeed != null | commentFeed.size() == 0){
            comments = commentFeed.getItems();
        }
        return comments;
    }

    /**
     * Post推文评论信息到指定端口
     */
    protected void postComment(List<Comment> comments){
        long currentTime = System.currentTimeMillis();
        if (!(comments == null) && comments.size()>0){
            List<RecordInfo> records = new ArrayList<>();
            for (Comment comment:comments){
                records.add(Convert.convertComment2Record(comment,currentTime));
            }
            logger.info("post comment number is " + records.size());
            PostData data = new PostData();
            data.setNum(records.size());
            data.setRecords(records);
            RestletPost.postGB(data);
        }

    }

    /**
     * 获取GP跟推文信息列表
     */
    private List<String> getFocusTweets(){
        return daoImpl.getGpFocusStatuses();
    }

    public void getPostComments() throws InterruptedException {
        GoogleCurrentUser currentUser3 = new GoogleCurrentUser();
        List<GoogleToken> tokens = currentUser3.getAppsInfo();
        Plus plus = setGplus(tokens);
        List<String> statuses = getFocusTweets();
        if(statuses==null | statuses.size()==0){
            logger.info("focus status size is 0, 0.5 hours later will try again");
        }
        for(String status : statuses){
            try {
                List<Comment> comments = getTweetStatus(plus,status);
                if (comments == null | comments.size()==0){
                    continue;
                }
                postComment(comments);
            } catch (Exception e) {
                logger.info(" {}  has no comments " , status);
                continue;
            }
        }
        logger.info("one round is over sleep 0.5 hours");
        Thread.sleep(30 * 60 * 1000);
        getPostComments();
    }


    public static void main(String[] args) {
        GoogleStatus status = new GoogleStatus();
        try {
            status.getPostComments();
        } catch (InterruptedException e) {
            logger.error("thread sleep error");
            throw new RuntimeException(e);
        }
    }
}
