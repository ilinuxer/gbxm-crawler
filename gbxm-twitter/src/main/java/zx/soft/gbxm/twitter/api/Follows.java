package zx.soft.gbxm.twitter.api;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import zx.soft.gbxm.twitter.dao.TwitterDaoImpl;
import zx.soft.gbxm.twitter.utils.PostUrlConfig;

public class Follows {

	public static Logger logger = LoggerFactory.getLogger(Follows.class);
	private Twitter twitter;
	private static final int COUNT = 200;//每页的数量,取最大值200
//	private static int page = 1;//获取第一页信息
//	private static long sinceId = 1L;

	private TwitterDaoImpl twitterDaoImpl = new TwitterDaoImpl();
	private final ClientResource clientResource = new ClientResource(URL);
	private static final String URL = getPostUrl();
	/**
	 * 获取post接口url
	 */
	private static String getPostUrl(){
		return PostUrlConfig.getProp("posturl.properties").getProperty("post.url");
	}

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
	public List<Status> getUserTimeLine(String screenName) throws TwitterException {
		List<Status> result = new ArrayList<>();

		long lastSinceId =0L;
		lastSinceId = twitterDaoImpl.getLastSinceId(screenName);
		int page = 1;
		//单页情况
		Paging paging = new Paging(page,COUNT,lastSinceId);
		ResponseList<Status> statuses = twitter.getUserTimeline(screenName,paging);
		if(statuses.size() != 0){
			twitterDaoImpl.updateTwMonitor(screenName,statuses.get(0).getId());
			result.addAll(statuses);
		}


		//分页情况
//		boolean flag = true;
//		while(flag){
//			Paging paging = new Paging(page, COUNT, lastSinceId);
//			ResponseList<Status> statuses = twitter.getHomeTimeline(paging);
//			if(page==1){
//				twitterDaoImpl.updateTwMonitor(screenName,statuses.get(0).getId());
//			}
//			logger.info("page=" + page + ",size=" + statuses.size());
//			page++;
//			if (statuses.size() == 0) {
//				flag = false;
//			} else {
//				result.addAll(statuses);
//			}
//		}
		return result;
	}

	public void setAccessToken(AccessToken accessToken) {
		twitter.setOAuthAccessToken(accessToken);
	}

//	public void setSinceId(long sinceId) {
//		this.sinceId = sinceId;
//	}
//
//	public long getSinceId() {
//		return sinceId;
//	}

	public Twitter getTwitter() {
		return this.twitter;
	}

}
