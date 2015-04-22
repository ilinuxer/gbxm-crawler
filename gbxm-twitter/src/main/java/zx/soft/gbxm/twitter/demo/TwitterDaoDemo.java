package zx.soft.gbxm.twitter.demo;

import zx.soft.gbxm.twitter.dao.TwitterDaoImpl;
import zx.soft.utils.json.JsonUtils;

/**
 * Created by jimbo on 4/21/15.
 */
public class TwitterDaoDemo {

    public static void main(String[] args) {
        TwitterDaoImpl dao = new TwitterDaoImpl();
        System.out.println(JsonUtils.toJson(dao.selectTwCurrentUser()));
//        System.out.println(JsonUtils.toJson(dao.getTwitterTokens()));
    }
}
