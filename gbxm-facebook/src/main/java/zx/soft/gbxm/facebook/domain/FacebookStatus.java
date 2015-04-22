package zx.soft.gbxm.facebook.domain;

import java.util.Date;

public class FacebookStatus {

	private String id;//状态id"517825994942542_599748826750258"
	private String from_id;//发布状态的用户id
	private String from_name;//发布状态的用户名
	private String message = "";//详细信息
	private String link = "";//状态相关的原文链接
	private Date createdTime;//"Fri Oct 04 12:00:00 CST 2013创建时间
	private Date updatedTime;//更新时间
	private int sharesCount;//分享数
	private int commentsCount;//评论数
	private int likesCount;//收藏数

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom_id() {
		return from_id;
	}

	public void setFrom_id(String from_id) {
		this.from_id = from_id;
	}

	public String getFrom_name() {
		return from_name;
	}

	public void setFrom_name(String from_name) {
		this.from_name = from_name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public int getSharesCount() {
		return sharesCount;
	}

	public void setSharesCount(int sharesCount) {
		this.sharesCount = sharesCount;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public int getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

}
