package zx.soft.gbxm.twitter.domain;

/**
 * Created by jimbo on 4/27/15.
 */
public class MonitorUser {
    private String userId;
    private String screenName;
    private long sinceId;

    public MonitorUser() {
        //
    }

    public MonitorUser(String userId, String screenName, long sinceId) {
        this.userId = userId;
        this.screenName = screenName;
        this.sinceId = sinceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public long getSinceId() {
        return sinceId;
    }

    public void setSinceId(long sinceId) {
        this.sinceId = sinceId;
    }
}
