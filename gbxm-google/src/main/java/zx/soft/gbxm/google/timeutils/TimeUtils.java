package zx.soft.gbxm.google.timeutils;

/**
 * Created by jimbo on 5/14/15.
 */
public class TimeUtils {

    /**
     * 因为服务器部署在东京，所以服务器的系统时间、推文、用户添加获取时间都以东京时间为主
     * 将东京时间转换为北京时间
     */
    public static long exchangeTime(long tokyoTime){
        return tokyoTime - 60*60*1000L;
    }


}
