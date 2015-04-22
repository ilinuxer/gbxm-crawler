package zx.soft.gbxm.google.domain;

import java.sql.Timestamp;

public class UserInfo {

	private String userId;
	private String userName;
	private Timestamp lastUpdateTime;

	@Override
	public String toString() {
		return "UserInfo [userId=" + userId + ", userName=" + userName + ", lastUpdateTime=" + lastUpdateTime + "]";
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

}
