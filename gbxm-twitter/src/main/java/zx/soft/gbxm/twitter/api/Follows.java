package zx.soft.gbxm.twitter.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import zx.soft.gbxm.twitter.dao.TwitterDaoImpl;
import zx.soft.gbxm.twitter.domain.RecordInfo;
import zx.soft.utils.json.JsonUtils;

public class Follows {
	TwitterCurrentUser twitterCurrentUser = new TwitterCurrentUser();

	public static Logger logger = LoggerFactory.getLogger(Follows.class);
	private Twitter twitter;
	private static final int COUNT = 200;//每页的数量,取最大值200

	private TwitterDaoImpl twitterDaoImpl = new TwitterDaoImpl();
//	private final ClientResource clientResource1 = new ClientResource(URLs[0]);
//	private final ClientResource clientResource2 = new ClientResource(URLs[1]);
//	private static final String[] URLs = TwitterCurrentUser.getPostUrl();


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
		long sinceId = 1L;
		long lastTimeSinceId = sinceId;
		int page = 1;
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
		return statuses;
	}

	/**
	 * 获取新增用户的历史信息，
	 *
	 */
	public void getUserTimeLine(String screenName) throws TwitterException {
		int tweetCount = 0;

		long lastSinceId;
		lastSinceId = twitterDaoImpl.getLastSinceId(screenName);
		int page = 1;

		//分页情况
		boolean flag = true;
		while(flag){
			Paging paging = new Paging(page, COUNT, lastSinceId);
			ResponseList<Status> statuses = twitter.getUserTimeline(screenName, paging);
			if(page==1 && statuses.size()!=0){
				twitterDaoImpl.updateTwMonitor(screenName,statuses.get(0).getId());
			}
			logger.info("page=" + page + ",size=" + statuses.size());
			tweetCount = tweetCount + statuses.size();
			page++;
			if (statuses.size() == 0) {
				flag = false;
			} else {
				//获取数据并post到指定接口
				List<RecordInfo> recordInfos = twitterCurrentUser.exchageTweet(statuses);
				twitterCurrentUser.currentUserStatusPostGB(recordInfos);
				twitterCurrentUser.currentUserStatusPostST(recordInfos);
			}
		}
		logger.info("{} tweet "+tweetCount +" tweet ",screenName);
	}

	public ResponseList<Status> getRetweet(String statusId) throws TwitterException {
		long statusIdL = Long.parseLong(statusId);
		ResponseList<Status> result = twitter.getRetweets(statusIdL);
		logger.info(JsonUtils.toJson(result));
		return result;
	}

	public void setAccessToken(AccessToken accessToken) {
		twitter.setOAuthAccessToken(accessToken);
	}

	public Twitter getTwitter() {
		return this.twitter;
	}

}
