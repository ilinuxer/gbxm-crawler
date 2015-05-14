package zx.soft.gbxm.twitter.utils;

/**
 * Created by jimbo on 5/14/15.
 */
public class TimeUtil {

    public static long exchangeTime(long tokoyTime){
        return tokoyTime - 60*60*1000L;
    }
}
