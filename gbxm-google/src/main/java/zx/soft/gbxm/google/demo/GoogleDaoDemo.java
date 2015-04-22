package zx.soft.gbxm.google.demo;

import zx.soft.gbxm.google.dao.GoogleDaoImpl;
import zx.soft.utils.json.JsonUtils;

/**
 * Created by jimbo on 4/22/15.
 */
public class GoogleDaoDemo {

    public static void main(String[] args) {
        GoogleDaoImpl dao = new GoogleDaoImpl();
        System.out.println(JsonUtils.toJson(dao.getGoogleTokens()));
    }
}
