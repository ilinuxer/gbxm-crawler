package zx.soft.gbxm.twitter.api;

import java.io.Serializable;

public class TwitterStatus implements Serializable {

	private static final long serialVersionUID = 980081269184312030L;

	private long id; // 状态唯一id
	private long user_id; // 用户唯一id, 注意：最后分表最好按照此字段分表
	private String screen_name = ""; // 昵称
	private double latitude; // 纬度
	private double longitude; // 经度
	private String created_at = "2000-03-04 15:26:19"; // 该微博创建时间,2015-03-04 15:26:19
	private String text = ""; // 该微博的文本信息
	private int retweet_count;// 该微博总的转发数量
	private boolean possibly_sensitive; // 可能敏感信息
	private String location = ""; // 所在城市信息
	private long retweeted_id; // 转发状态的id
	private long retweeted_user_id; // 转发用户的id
	private String retweeted_screen_name = ""; // 转发用户的昵称

	public TwitterStatus() {
		//
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getRetweet_count() {
		return retweet_count;
	}

	public void setRetweet_count(int retweet_count) {
		this.retweet_count = retweet_count;
	}

	public boolean isPossibly_sensitive() {
		return possibly_sensitive;
	}

	public void setPossibly_sensitive(boolean possibly_sensitive) {
		this.possibly_sensitive = possibly_sensitive;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getRetweeted_id() {
		return retweeted_id;
	}

	public void setRetweeted_id(long retweeted_id) {
		this.retweeted_id = retweeted_id;
	}

	public long getRetweeted_user_id() {
		return retweeted_user_id;
	}

	public void setRetweeted_user_id(long retweeted_user_id) {
		this.retweeted_user_id = retweeted_user_id;
	}

	public String getRetweeted_screen_name() {
		return retweeted_screen_name;
	}

	public void setRetweeted_screen_name(String retweeted_screen_name) {
		this.retweeted_screen_name = retweeted_screen_name;
	}

}
