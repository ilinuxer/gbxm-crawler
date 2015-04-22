package zx.soft.gbxm.twitter.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

public class Follows {

	public static Logger logger = LoggerFactory.getLogger(Follows.class);
	private Twitter twitter;
	private static final int COUNT = 200;//每页的数量,取最大值200
	private static int page = 1;//获取第一页信息
	private static long sinceId = 1L;

	public Follows(Twitter twitter) {
		this.twitter = twitter;
	}

	/**
	 * 获取twitter用户的主页动态信息
	 * @return
	 * @throws TwitterException
	 * @throws InterruptedException
	 */
	public List<Status> getHomeTimeLine() throws InterruptedException, TwitterException {
		List<Status> statuses = new ArrayList<>();
		boolean flag = true;
		long lastTimeSinceId = sinceId;
		while (flag) {
			Paging nextPaging = new Paging(page, COUNT, lastTimeSinceId);
			ResponseList<Status> nextStatuses = twitter.getHomeTimeline(nextPaging);
			logger.info("page=" + page + ",size=" + nextStatuses.size());
			page++;
			if (nextStatuses.size() == 0) {
				flag = false;
			} else {
				statuses.addAll(nextStatuses);
				sinceId = statuses.get(0).getId();
				nextStatuses = null;
			}
		}
		page = 1;
		return statuses;
	}

	//获取新增用户的历史推文信息
	public List<Status> getUserTimeLine(String userId) throws TwitterException {
		List<Status> result = new ArrayList<>();
		Paging paging = new Paging(page,COUNT);
		ResponseList<Status> statuses = twitter.getUserTimeline(userId,paging);
		result.addAll(statuses);
		return result;
	}

	public void setAccessToken(AccessToken accessToken) {
		twitter.setOAuthAccessToken(accessToken);
	}

	public void setSinceId(long sinceId) {
		this.sinceId = sinceId;
	}

	public long getSinceId() {
		return sinceId;
	}

	public Twitter getTwitter() {
		return this.twitter;
	}

}
